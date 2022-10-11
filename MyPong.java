import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

import java.awt.event.MouseEvent;
import java.util.Random;

public class MyPong extends Application {

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

        model.getChildren().addAll(bat, ball);

        Label fpsLabel = new Label();
        fpsLabel.setTranslateX(2);
        model.getChildren().add(fpsLabel);

        AnimationTimer loop = new AnimationTimer() {
            int ticks = 0;
            double old = -1;
            double ballAngle = Math.toRadians(270);
            double ballMagnitude = 1;
            @Override
            public void handle(long now) {
                double ball_x = ball.getBoundsInParent().getMaxX();
                double ball_y = ball.getBoundsInParent().getMaxY();
                double ball_x_angle = Math.round(Math.cos(ballAngle) *100)/100;
                double ball_y_angle = Math.round(Math.sin(ballAngle) *100)/100;
                if(old<0) old =now;
                double delta = (now-old)/1e9;
                if(ticks%50 ==0) {
                    fpsLabel.setText((1 / delta) + "avg fps");
                }
                if (delta == 0){
                    fpsLabel.setText(String.valueOf(delta));
                    restart(ball);
                }
                //collisions
                if(!Shape.intersect(bat,ball).getBoundsInLocal().isEmpty()){
                    //bat collision
                    //calculate speed of bat for angle
                    ballAngle = ballAngle-Math.toRadians(180);
                    ballMagnitude++;
                }
                else if(ball_x<2){ //left collistion 2px padding
                    //check if ball is going up or down
                    if(ball_y_angle >0) {
                        //ball going up
                        ballAngle = ballAngle + Math.toRadians(90);
                    }
                    else if(ball_y_angle<0){
                        //ball going down
                        ballAngle = ballAngle - Math.toRadians(90);
                    }
                    ballMagnitude++;
                }
                else if(ball_x>APP_W - 55){ //right collistion 2px padding
                    //check if ball is going up or down
                    if(ball_y_angle >0) {
                        //ball going up
                        ballAngle = ballAngle - Math.toRadians(90);
                    }
                    else if(ball_y_angle<0){
                        //ball going down
                        ballAngle = ballAngle + Math.toRadians(90);
                    }
                    ballMagnitude++;
                }
                else if(ball_y<50){
                    //ceiling collision
                    //ball going L -> R or R ->L?
                    if(ball_x_angle >0) {
                        //ball going L -> R
                        ballAngle = ballAngle - Math.toRadians(90);
                    }
                    else if(ball_x_angle<0){
                        //ball going R -> L
                        ballAngle = ballAngle + Math.toRadians(90);
                    }
                    else{
                        //ball is straight

                        ballAngle = ballAngle + Math.toRadians(180);
                    }
                    ballMagnitude++;
                }
                else if(ball_y > APP_H){
                    //ball misses bat
                    restart(ball);
                    ballMagnitude =1;
                }
                moveBall(ball,ballAngle,ballMagnitude);

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

    // restarts ball from random point at top of screen
    public void restart(Rectangle ball){
        Random rand = new Random();
        //100 for padding 50 for ball width 1 in case max value
        int newStartX = rand.nextInt(APP_W-200)+50;

        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        ball.getTransforms().addAll(new Translate(newStartX-x,100-y));

    }

    public void moveBat(double x, Rectangle bat){
        double offset = x - (.5 * bat.getWidth());
        bat.setX(offset);
    }

    public void moveBall(Rectangle ball, double angle, double magnitude){

        double newX = (Math.round(Math.cos(angle) *100)/100)* magnitude;
        double newY = (Math.round(Math.sin(angle) *100)/100)* magnitude *-1;

        ball.getTransforms().addAll(new Translate(newX,newY));
        Bounds bounds = ball.getBoundsInParent();
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        //System.out.println(x+ ": " + y);
    }
}
