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
                if(ticks%50 ==0) {
                    fpsLabel.setText(String.format(" %.3f", frameRate) + "avg fps");
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

        scene.setOnMouseMoved(event -> {
            moveBat(event.getX(), bat);

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
