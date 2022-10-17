import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class MyPong extends Application {

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false;
    private boolean batCollision = false;
    private boolean ceilingCollision = false;
    private boolean sideCollision = false;
    private boolean showLabel = true;
    private boolean soundOn = true;
    private ArrayList<Double> mouseSpeedList = new ArrayList<>();
    private int score =0;

    private static final int APP_W = 600;
    private static final int APP_H = 600;

    private static final int BALL_W = 50;
    private static final int BALL_H = 50;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group model = new Group();
        Scene scene = new Scene(model,APP_W,APP_H);

        BinkBonkSound bink = new BinkBonkSound();

        Rectangle bat = new Rectangle(200,575, 200,25);
        Rectangle ball = new Rectangle(100,100,50,50);
        ball.setFill(Color.BLUE);

        Group ballGroup = new Group();
        ballGroup.getChildren().addAll(ball);

        model.getChildren().addAll(bat, ballGroup);

        Label fpsLabel = new Label();
        fpsLabel.setTranslateX(2);

        Label scoreLabel = new Label();
        scoreLabel.setTranslateX(APP_W/2);
        model.getChildren().addAll(fpsLabel, scoreLabel);

        AnimationTimer loop = new AnimationTimer() {
            Rotate rotation = new Rotate();
            int ticks = 0;
            double old = -1;
            double ballAngle = Math.toRadians(270);
            double ballMagnitude = 1;
            @Override
            public void handle(long now) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                mouseSpeedList.add((double)p.x);
                Point2D minCoords = getMinBallCoords(ballGroup);
                double ballMinX = minCoords.getX();
                double ballMinY = minCoords.getY();
                Point2D maxCoords = getMaxBallCoords(ballGroup);
                double ballMaxX = maxCoords.getX();
                double ballMaxY = maxCoords.getY();
                Point2D centerCoords = getBallCenterCoords(ball);
                double ballCenterX = centerCoords.getX();
                double ballCenterY = centerCoords.getY();

                double ball_x_angle = (Math.round(Math.cos(ballAngle)
                        * Math.pow(10, 3)) / Math.pow(10, 3));
                double ball_y_angle = (Math.round(Math.sin(ballAngle)
                        * Math.pow(10, 3)) / Math.pow(10, 3));;

                double frameRate = getFrameRate(now);

                if(old<0) old =now;
                double delta = (now-old)/1e9;
                if(ticks%50 ==0 && showLabel) {
                    fpsLabel.setText(String.format(" %.3f", frameRate) + " avg fps "
                    + String.format(" %.2f", 1000*(1/frameRate)) + " FT(ms) "
                    + String.format(" %.0f", delta) + " Game Time");
                    fpsLabel.setVisible(true);
                }
                else if(ticks%50 ==0 && !showLabel){
                    fpsLabel.setVisible(false);
                }
                scoreLabel.setText(String.valueOf(score));
                if (delta == 0){
                    fpsLabel.setText(String.valueOf(delta));
                    restart(ballGroup, ball);
                    //double oldRot = rotation.getAngle();
                    rotation.setAngle(0);
                }
                //collisions
                if(!Shape.intersect(bat,ball).getBoundsInLocal().isEmpty()){
                    //bat collision
                    //calculate speed of bat for angle
                    //determine the direction the ball should go. takes
                    if(!batCollision) {
                        double speed = batSpeed();
                        ballAngle = handleHitTrajectory(ballAngle, speed);
                        rotation = handleHitRotation(ballCenterX,ballCenterY,speed, rotation);
                        batCollision=true;
                        ballMagnitude+=.5;
                        score++;
                        if(soundOn) bink.play(true);
                    }

                    //ballAngle = ballAngle-Math.toRadians(180);

                }
                else if(ballMinX<2){ //left collision 2px padding
                    //check if ball is going up or down
                    if(!sideCollision) {
                        if (ball_y_angle > 0) {
                            //ball going up
                            ballAngle = Math.toRadians(180) - ballAngle % Math.toRadians(360);
                        } else if (ball_y_angle < 0) {
                            //ball going down
                            ballAngle = Math.toRadians(180) - ballAngle % Math.toRadians(360);
                        }
                        sideCollision = true;
                        ballMagnitude+=.5;
                        score++;
                        if(soundOn) bink.play(true);
                    }
                }
                else if(ballMaxX>APP_W - 55){ //right collision 2px padding
                    //check if ball is going up or down
                    if(!sideCollision) {
                        if (ball_y_angle > 0) {
                            //ball going up
                            ballAngle = Math.toRadians(180) - ballAngle % Math.toRadians(360);
                        } else if (ball_y_angle < 0) {
                            //ball going down
                            ballAngle = Math.toRadians(180) - ballAngle % Math.toRadians(360);
                        }
                        sideCollision = true;
                        ballMagnitude+=.5;
                        score++;
                        if(soundOn) bink.play(true);
                    }
                }
                else if(ballMinY<50){
                    //ceiling collision
                    //ball going L -> R or R ->L?
                    if(!ceilingCollision) {
                        if (ball_x_angle != 0) {
                            //ball going L -> R
                            ballAngle = Math.toRadians(360) - ballAngle%Math.toRadians(360);
                        } else {
                            //ball is straight

                            ballAngle = ballAngle + Math.toRadians(180);
                        }
                        ceilingCollision= true;
                        ballMagnitude+=.5;
                        score++;
                        if(soundOn) bink.play(true);
                    }

                }
                else if(ballMaxY > APP_H){
                    //ball misses bat
                    score = 0;
                    restart(ballGroup, ball);

                    centerCoords = getBallCenterCoords(ball);
                    rotation.setAngle(0);
                    rotation.setPivotX(centerCoords.getX());
                    rotation.setPivotY(centerCoords.getY());
                    ballMagnitude =1;
                    ballAngle = Math.toRadians(270);
                    return;
                }
                else{
                    //no collision
                    batCollision = false;
                    ceilingCollision = false;
                    sideCollision = false;
                }
                moveBall(ball,ballGroup,ballAngle,ballMagnitude, rotation);

                ticks++;
            }
        };
        loop.start();

        scene.setOnMouseExited(event ->{
            bat.setFill(Color.RED);
        });

        scene.setOnMouseEntered(event ->{
            bat.setFill(Color.BLACK);
        });

        scene.setOnMouseMoved(event -> {
            double x = event.getX();
            double y = event.getY();
            moveBat(x, bat);

        });
        scene.setOnKeyReleased(event -> {
            if(event.getText().equals("i")){
                showLabel = !showLabel;
            }
            else if(event.getText().equals("s")){
                soundOn = !soundOn;
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Rotate handleHitRotation(double ballX, double ballY, double speed, Rotate rot) {
        if (speed ==0){
            return rot;
        }
        double angleConstant = speed *.1;
        Rotate rotation = new Rotate(angleConstant,ballX,ballY);
        System.out.println(ballX + ": " + ballY);

        return rotation;
    }

    private double handleHitTrajectory(double ballAngle, double speed) {
        double ball_x_angle = (Math.round(Math.cos(ballAngle)
                * Math.pow(10, 3)) / Math.pow(10, 3));
        double angleChangeConstant = .01 * speed *-1;
        if(angleChangeConstant>0.2){
            angleChangeConstant = .2;
        }
        else if(angleChangeConstant<-.2){
            angleChangeConstant = -.2;
        }
        //.01 radians  ~= .5 degrees. speed range ~= -20 to +20

        double hitTrajectory = ballAngle+Math.toRadians(180);
        if(ball_x_angle != 0){
            hitTrajectory = Math.toRadians(360) - ballAngle%Math.toRadians(360);

        }
        hitTrajectory += angleChangeConstant;
        return hitTrajectory;

    }

    public double getFrameRate(long now){
        double frameRate=0;
        long oldFrameTime = frameTimes[frameTimeIndex] ;
        frameTimes[frameTimeIndex] = now ;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
        if (frameTimeIndex == 0) {
            arrayFilled = true ;
        }
        if (arrayFilled) {
            long elapsedNanos = now - oldFrameTime ;
            long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
            frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
        }
        return frameRate;
    }

    public double batSpeed(){
        //calculate the speed of the bat
        //loop through mouse speed list
        double lastX = mouseSpeedList.get(mouseSpeedList.size()-1);
        double prevX = mouseSpeedList.get(mouseSpeedList.size()-6);
        if(lastX-prevX ==0){
            //System.out.println(0);
            return 0;
        }
        double speed = (lastX - prevX) / 5; //5 is the number of entries btw them
        return speed;

    }

    // restarts ball from random point at top of screen
    public void restart(Group ball, Rectangle b){
        //b.getTransforms().removeAll();
        mouseSpeedList.clear();
        Random rand = new Random();
        //100 for padding 50 for ball width 1 in case max value
        int newStartX = rand.nextInt(APP_W-200)+50;


        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getMinX();
        double y = bounds.getMinY();

        double newX = newStartX-x;
        double newY = 100-y;
        ball.getTransforms().addAll(new Translate(newX,newY));


        Bounds bound = ball.getBoundsInParent();
        System.out.println("\nnewX: " + bound.getMinX() + "\nnewY: " + bound.getMaxY());
        double centerX = bound.getCenterX();
        double centerY = bound.getCenterY();
        double xx = ball.getLocalToSceneTransform().getMxx();
        double xy = ball.getLocalToSceneTransform().getMxy();
        double angle = Math.atan2(-xy, xx);
        System.out.println(angle);
        double newAngle = Math.toDegrees(angle)*-1;
        ball.getTransforms().addAll(new Rotate(newAngle,centerX,centerY));

        System.out.println("\nX: " + x + "\nY: " + y);
        System.out.println("\nnewX: " + bound.getMinX()+ "\nnewY: " + bound.getMinY());

    }

    public void moveBat(double x, Rectangle bat){
        double offset = x - (.5 * bat.getWidth());
        bat.setX(offset);
    }

    public void moveBall(Rectangle ball, Group ballGroup, double angle, double magnitude, Rotate rotation){
        //round the trig evaluation bc radians are not 100% accurate

        double newX = (Math.round(Math.cos(angle)
                * Math.pow(10, 3)) / Math.pow(10, 3))* magnitude;

        double newY = (Math.round(Math.sin(angle)
                * Math.pow(10, 3)) / Math.pow(10, 3))* magnitude *-1;

        ball.getTransforms().addAll(rotation);
        ballGroup.getTransforms().addAll(new Translate(newX,newY));


    }
    public Point2D getMinBallCoords(Group ball){
        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        Point2D coords = new Point2D(x,y);
        return coords;
    }

    public Point2D getMaxBallCoords(Group ball){
        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getMaxX();
        double y = bounds.getMaxY();
        Point2D coords = new Point2D(x,y);
        return coords;
    }

    public Point2D getBallCenterCoords(Rectangle ball){
        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getCenterX();
        double y = bounds.getCenterY();
        Point2D coords = new Point2D(x,y);
        return coords;
    }
}
class BinkBonkSound {

    // magic numbers that are not common knowledge unless one
    // has studied the GM2 standard and the midi sound system
    //
    // The initials GM mean General Midi. This GM standard
    // provides for a set of common sounds that respond
    // to midi messages in a common way.
    //
    // MIDI is a standard for the encoding and transmission
    // of musical sound meta-information, e.g., play this
    // note on this instrument at this level and this pitch
    // for this long.
    //
    private static final int MAX_PITCH_BEND = 16383;
    private static final int MIN_PITCH_BEND = 0;
    private static final int REVERB_LEVEL_CONTROLLER = 91;
    private static final int MIN_REVERB_LEVEL = 0;
    private static final int MAX_REVERB_LEVEL = 127;
    private static final int DRUM_MIDI_CHANNEL = 9;
    private static final int CLAVES_NOTE = 76;
    private static final int NORMAL_VELOCITY = 100;
    private static final int MAX_VELOCITY = 127;

    Instrument[] instrument;
    MidiChannel[] midiChannels;
    boolean playSound;

    public BinkBonkSound(){
        playSound=true;
        try{
            Synthesizer gmSynthesizer = MidiSystem.getSynthesizer();
            gmSynthesizer.open();
            instrument = gmSynthesizer.getDefaultSoundbank().getInstruments();
            midiChannels = gmSynthesizer.getChannels();

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    // This method has more comments than would typically be needed for
    // programmers using the Java sound system libraries. This is because
    // most students will not have exposure to the specifics of midi and
    // the general midi sound system. For example, drums are on channel
    // 10 and this cannot be changed. The GM2 standard defines much of
    // the detail that I have chosen to use static constants to encode.
    //
    // The use of midi to play sounds allows us to avoid using external
    // media, e.g., wav files, to play sounds in the game.
    //
    void play(boolean hiPitch){
        if(playSound) {

            // Midi pitch bend is required to play a single drum note
            // at different pitches. The high and low pongs are two
            // octaves apart. As you recall from high school physics,
            // each additional octave doubles the frequency.
            //
            midiChannels[DRUM_MIDI_CHANNEL]
                    .setPitchBend(hiPitch ? MAX_PITCH_BEND : MIN_PITCH_BEND);

            // Turn the reverb send fully off. Drum sounds play until they
            // decay completely. Reverb extends the audible decay and,
            // from a gameplay point of view, is distracting.
            //
            midiChannels[DRUM_MIDI_CHANNEL]
                    .controlChange(REVERB_LEVEL_CONTROLLER, MIN_REVERB_LEVEL);

            // Play the claves on the drum channel at a "normal" volume
            //
            midiChannels[DRUM_MIDI_CHANNEL]
                    .noteOn(CLAVES_NOTE, NORMAL_VELOCITY);
        }
    }

    public void toggleSound() {
        playSound = !playSound;
    }
}