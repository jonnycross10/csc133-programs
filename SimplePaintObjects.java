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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SimplePaintObjects extends Application {
    public ArrayList<AbstractTool> activeTools = new
            ArrayList<AbstractTool>(); //TODO sorry

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
    //arraylist of active tools


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
        ArrayList<AbstractTool> colorIcons = getColorList();

        colorBox.getChildren().addAll(colorIcons);
        colorBox.getChildren().addAll(getActionList());

        //toolBox additions
        ArrayList<AbstractTool> toolIcons = getShapeList();

        toolBox.getChildren().addAll(toolIcons);

        //add first color and tool
        AbstractTool initialColor = colorIcons.get(0);
        AbstractTool initialTool = toolIcons.get(0);
        activeTools.add(0,initialColor);
        activeTools.add(1,initialTool);
        initialColor.r.setStroke(Color.BLACK);
        initialColor.r.setStrokeWidth(4);
        initialTool.r.setStroke(TOOL_RECT_FG);
        initialTool.r.setStrokeWidth(4);

        Pane root = new Pane(mainBox);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Simple Paint");
        primaryStage.show();

    }


    private ArrayList<AbstractTool> getColorList(){
        ArrayList<AbstractTool> colors = new ArrayList<AbstractTool>();
        for(int i=0; i<7; i++){
            //Question: are these supposed to be ColorTools or just StackPanes
            ColorTool c = addMouseHandlerColorTool(
                    new ColorTool(palette[i])
            );

            //Rectangle rect = new Rectangle(50,50, c.toolColor);//TODO put in constructor of tool
            c.getChildren().add(c.r);
            colors.add(c);
        }
        return colors;
    }

    private StackPane getActionList(){
        //TODO further implementation
        ActionTool a = addMouseHandlerActionTool(new ActionTool());
        a.getChildren().addAll(a.r, a.label);
        return a;
    }

    private ArrayList<AbstractTool> getShapeList(){
        ArrayList<AbstractTool> shapes = new ArrayList<>();
        Color toolFg = SimplePaintObjects.TOOL_FG;
        for (int i=0; i<8; i++){
            ShapeTool shape = addMouseHandlerShapeTool(
                    new ShapeTool()
            );
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

    private ColorTool addMouseHandlerColorTool(ColorTool colorTool){
        colorTool.setOnMouseClicked((e) ->{
            AbstractTool previousTool = getActiveTool(colorTool);
            if(colorTool != previousTool){
                colorTool.deactivate(previousTool, colorTool.toolType);
                setActiveTool(colorTool);
                colorTool.activate(colorTool);
            }
        });
        return colorTool;
    }

    private ShapeTool addMouseHandlerShapeTool(ShapeTool shapeTool){
        shapeTool.setOnMouseClicked((e)->{
            //shape clicked
            AbstractTool previousTool = getActiveTool(shapeTool);
            if(shapeTool != previousTool){
                shapeTool.deactivate(previousTool, shapeTool.toolType);
                setActiveTool(shapeTool);
                shapeTool.activate(shapeTool);
            }
        });
        return shapeTool;
    }

    private ActionTool addMouseHandlerActionTool(ActionTool actionTool){
        actionTool.setOnMouseReleased(e ->actionTool.activate(actionTool));
        actionTool.setOnMousePressed(e -> actionTool.activate(actionTool));

        actionTool.setOnMouseClicked(e -> actionTool.myClearAction.run());
        return actionTool;
    }

    public AbstractTool getActiveTool(AbstractTool tool){
        return activeTools.get(tool.toolType);
    }
    public void setActiveTool(AbstractTool tool){
        activeTools.set(tool.toolType, tool);
    }
}

abstract class AbstractTool extends StackPane{

    //should be able to set the value of the rectangle
    Color toolBg = SimplePaintObjects.TOOL_RECT_FG;
    Rectangle r = new Rectangle(60,60,toolBg);
    boolean clearClicked = false;
    int toolType;
    //TODO add activate method
    public void activate(AbstractTool s){


        System.out.println("Clicked tool type " + s.toolType);


        //calls getters and setters
        if(s.toolType ==0 ){

            //change size of box
            s.r.setStroke(((ColorTool) s).getColor());
            s.r.setStrokeWidth(4);

        }
        else if(s.toolType ==1){
            //change size of box
            s.r.setStroke(Color.LIGHTCORAL);
            s.r.setStrokeWidth(4);

        }
        else if(s.toolType ==2){
            //clear clicked
            if(!clearClicked){
                System.out.println("if");
                s.r.setStroke(Color.LIGHTCORAL);
                s.r.setStrokeWidth(2);
            }
            else{
                System.out.println("else");
                s.r.setFill(Color.LIGHTCORAL);
                s.r.setStrokeWidth(0);
            }
            clearClicked = !clearClicked;
        }

    }
    public void deactivate(AbstractTool s, int tool){
        //calls getters and setters
        //change size of box to be smaller
        if(tool ==0) {
            System.out.println("deactivating tool");
            //s.r.setStroke(((ColorTool) s).getColor());
            s.r.setStrokeWidth(0);
        }
        else if(tool==1){
            System.out.println("deactivating tool");
            //s.r.setStroke(Color.LIGHTCORAL);
            s.r.setStrokeWidth(0);
        }
    }
}

class ColorTool extends AbstractTool{
    Color toolColor;
    public ColorTool(Color color) {
        toolType=0;
        this.toolColor = color;
        this.r.setFill(color);

    }

    public Color getColor(){
        return toolColor;
    }
    public ColorTool getColorTool(){
        return this;
    }

}

class ShapeTool extends AbstractTool{
    ShapeObject shapeObject;
    ShapeObject getPaintShape(){
        return shapeObject;
    }
    public ShapeTool(){
        toolType=1;

    }
}

class ActionTool extends AbstractTool{
    //rectangle, label, action
    Color textColor = SimplePaintObjects.TOOL_FG;
    String text = "Clear";
    Runnable myClearAction = () -> {
        //call clear canvas method
        System.out.println("Clear button pressed");
    };

    javafx.scene.control.Label label;
    public ActionTool(){
        toolType=2;
        label = new Label(text);
        label.setFont(Font.font(null, FontWeight.BOLD, 20));
        label.setTextFill(textColor);

    }
}

class PointTool extends ShapeTool{


    public PointTool() {
        super();
    }

    public void draw(){

    }


    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}

interface ShapeObject{
    void draw();
    boolean dragUpdate();

}

class LineSegmentShape implements ShapeObject{
    public LineSegmentShape(){

    }
    public boolean dragUpdate(){
        return true;
    }

    public void draw(){

    }

}

class LineShape implements ShapeObject{
    public LineShape(){

    }
    public boolean dragUpdate(){
        return false;
    }

    public void draw(){

    }

}

class FilledPolyShape implements ShapeObject{
    public FilledPolyShape(){

    }
    public boolean dragUpdate(){
        return false;
    }
    public void draw(){

    }
}

