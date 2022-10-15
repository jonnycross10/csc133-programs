import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

public class Pong extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Model model = new Model(); // prob gonna be a Model

        model.getChildren().addAll(model.ball.r, model.bat.r);
        Scene scene = new Scene(model,600,600);
        scene.setOnMouseMoved(event -> {
            model.bat.translate(event.getX());
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

class Model extends Group{
    //Will need to make a ball and bat class
    Ball ball;
    Bat bat;
    public Model(){
        ball = new Ball();
        bat = new Bat();
    }


}

class Bat{
    double length;
    Rectangle r;
    public Bat(){
        this.length = 100;
        r = new Rectangle(200,575, 100,25);
    }
    public void shrink(){
        if (length>60) {
            length -= 5;
        }
    }
    public void translate(double x){
        //calculate length offset
        double offset = x- (.5 * this.length);
        this.r.setX(offset);
    }
    public void activate(){

    }
    public void deactivate(){

    }
}

class Ball{
    //pretty much just a spinny rectangle
    Rectangle r;
    public Ball(){
        r = new Rectangle(100,100,100,100);
        r.setFill(Color.BLUE);
    }
}