import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;

public class SimplePaintObjects extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //we can create a transparent canvas on top of our regular canvas
    //while drawing it's on transparent
    //once mouse is unclicked, it will be committed to main canvas
    //and deleted from transparent canvas

    //need an hbox{Canvas,Canvas, Vbox,Vbox}

    static final Color TOOL_RECT_FG = Color.LIGHTCORAL;
    static final Color TOOL_RECT_BG = Color.WHITE;
    static final Color TOOL_FG = Color.LEMONCHIFFON;
    static final int CELL_W = 60;
    static final int PADDING = 5;

    private final Color[] palette = {
            Color.BLACK, Color.RED, Color.GREEN, Color.BLUE,
            Color.CYAN, Color.MAGENTA, Color.color(0.95,0.9,0)
    };

    @Override
    public void start(Stage primaryStage) {
        //root's children
        Canvas mainCanvas = new Canvas(600,400);
        VBox toolBox = new VBox();
        toolBox.setSpacing(PADDING);
        toolBox.setPadding(new Insets(5,5,5,5));
        VBox colorBox = new VBox();
        colorBox.setSpacing(PADDING);
        colorBox.setPadding(new Insets(5,5,5,5));

        toolBox.setBackground(new Background(new BackgroundFill(Color.WHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        colorBox.setBackground(new Background(new BackgroundFill(Color.WHITE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

        //root of application
        HBox mainBox = new HBox();
        mainBox.setSpacing(PADDING); //TODO may be wrong, did he mean spacing?
        mainBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        mainBox.setPadding(new Insets(5,5,5,5));
        ObservableList list = mainBox.getChildren();

        list.addAll(mainCanvas, toolBox, colorBox);

        //colorBox additions
        ArrayList<StackPane> colorIcons = getColorList();

        colorBox.getChildren().addAll(colorIcons);
        colorBox.getChildren().addAll(getActionList());

        //toolBox additions
        ArrayList<StackPane> toolIcons = getShapeList();

        toolBox.getChildren().addAll(toolIcons);

        Pane root = new Pane(mainBox);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Simple Paint");
        primaryStage.show();

    }

    Runnable myClearAction = () -> {
        //call clear canvas method
        System.out.println("Clear button pressed");
    };

    Runnable myColorAction = () -> {
        //color selected
        System.out.println("Color Pressed");
    };

    Runnable myToolAction = () -> {
        //tool selected
        System.out.println("Tool pressed");
    };

    private ArrayList<StackPane> getColorList(){
        ArrayList<StackPane> colors = new ArrayList<StackPane>();
        for(int i=0; i<7; i++){
            //Question: are these supposed to be ColorTools or just StackPanes
            ColorTool c = new ColorTool(palette[i], this.myColorAction);

            //Rectangle rect = new Rectangle(50,50, c.toolColor);//TODO put in constructor of tool
            c.getChildren().add(c.r);
            colors.add(c);
        }
        return colors;
    }

    private StackPane getActionList(){
        //TODO further implementation
        ActionTool a = new ActionTool(this.myClearAction);
        a.getChildren().addAll(a.r, a.label);
        return a;
    }

    private ArrayList<StackPane> getShapeList(){
        ArrayList<StackPane> shapes = new ArrayList<>();
        Color toolFg = SimplePaintObjects.TOOL_FG;
        for (int i=0; i<8; i++){
            ShapeTool shape = new ShapeTool(this.myToolAction);
            shape.getChildren().add(shape.r);
            Circle circle = new Circle();
            //TODO add a switch case to draw icons and add to shape
            switch(i){

                case(0):
                    //add a 2 pixel circle

                    circle.setFill(toolFg);
                    circle.setRadius(3);
                    shape.getChildren().add(circle);
                    break;
                case(1):
                    circle.setFill(toolFg);
                    circle.setRadius(5);
                    shape.getChildren().add(circle);
                    break;
                case(2):
                    circle.setFill(toolFg);
                    circle.setRadius(8);
                    shape.getChildren().add(circle);
                    break;
                case(3):
                    circle.setFill(toolFg);
                    circle.setRadius(12);
                    shape.getChildren().add(circle);
                    break;
                case(4):
                    Line line = new Line(0,0,40,40);
                    line.setStrokeWidth(3);
                    line.setStroke(toolFg);
                    shape.getChildren().add(line);
                    break;
                case(5):
                    Rectangle rectangle = new Rectangle();
                    rectangle.setWidth(40);
                    rectangle.setHeight(40);
                    rectangle.setFill(toolFg);
                    shape.getChildren().add(rectangle);
                    break;
                case(6):
                    circle.setFill(toolFg);
                    circle.setRadius(20);
                    shape.getChildren().add(circle);
                    break;
                case(7):
                    Rectangle roundRectangle = new Rectangle();
                    roundRectangle.setWidth(45);
                    roundRectangle.setHeight(45);
                    roundRectangle.setFill(toolFg);
                    shape.getChildren().add(roundRectangle);
                    roundRectangle.setArcWidth(20);
                    roundRectangle.setArcHeight(20);
                    break;
            }


            shapes.add(shape);
        }
        return shapes;
    }
}

abstract class AbstractTool extends StackPane{
    //should be able to set the value of the rectangle
    Color toolBg = SimplePaintObjects.TOOL_RECT_FG;
    Rectangle r = new Rectangle(60,60,toolBg);
    //TODO add activate method
}

class ColorTool extends AbstractTool{
    Color toolColor;

    public ColorTool(Color color, Runnable action){

        this.toolColor = color;
        this.r.setFill(color);
        setOnMouseClicked(e -> action.run());
    }

    public Color getColor(){
        return toolColor;
    }

}

class ShapeTool extends AbstractTool{
    public ShapeTool(Runnable action){
        setOnMouseClicked(e -> action.run());
    }
}

class ActionTool extends AbstractTool{
    //rectangle, label, action
    Color textColor = SimplePaintObjects.TOOL_FG;
    String text = "Clear";

    javafx.scene.control.Label label;
    public ActionTool(Runnable action){
        label = new Label(text);
        label.setFont(Font.font(null, FontWeight.BOLD, 20));
        label.setTextFill(textColor);
        setOnMouseClicked(e -> action.run());

    }
}