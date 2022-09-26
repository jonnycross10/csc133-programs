import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EventListener;

public class SimplePaintObjects extends Application {
    public ArrayList<AbstractTool> activeTools = new
            ArrayList<>(); //TODO sorry

    public ArrayList<ShapeObject> shapeList = new
            ArrayList<>();

    public Point2D initialClick;

    public static void main(String[] args) {
        launch(args);
    }

    public ShapeObject currentShape;

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
    Canvas mainCanvas;
    GraphicsContext g;



    @Override
    public void start(Stage primaryStage) {

        //root's children
        mainCanvas = new Canvas(600,536);
        g = mainCanvas.getGraphicsContext2D();
        clearCanvas();

        mainCanvas.setOnMousePressed( e -> mousePressed(e) );
        mainCanvas.setOnMouseDragged( e -> mouseDragged(e) );
        mainCanvas.setOnMouseReleased( e -> mouseReleased(e) );

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

    private void drawCanvas(GraphicsContext g){
        //loop through shape object array and draw them in order

    }

    private void clearCanvas(){
        double originalWidth = g.getLineWidth();
        int width = (int)mainCanvas.getWidth();    // Width of the canvas.
        int height = (int)mainCanvas.getHeight();  // Height of the canvas.

        g.setFill(Color.WHITE);
        g.fillRect(0,0,width,height);
    }

    Runnable myClearAction = () -> {
        //call clear canvas method
        clearCanvas();
        shapeList = new ArrayList<>();
        System.out.println("Clear button pressed");
    };

    public void mousePressed(MouseEvent e){
        //store mouse event meta data
        //get the current tool
        //clearCanvas();
        ShapeTool currentTool = (ShapeTool) getActiveTool(1);
        int currentToolIndex = currentTool.getToolIndex();
        ColorTool currentColorObject = (ColorTool) getActiveTool(0);
        Color currentColor = currentColorObject.getColor();
        System.out.println("Tool: " + currentToolIndex);
        System.out.println("Color: " + currentColor.toString());

        initialClick = new Point2D(e.getX(),e.getY());
        System.out.println("initial click");
    }

    public void mouseDragged(MouseEvent e){
        //update mouse event meta data
        //get the current tool
        //clear canvas
        //check draggedUpdate thingy
        //if true draw object
        //else just call current tool's draw
        clearCanvas();

        //draw all the previous objects
        for (int i=0;i<shapeList.size();i++){
            shapeList.get(i).draw(g);
        }

        ShapeTool currentTool = (ShapeTool) getActiveTool(1);
        int currentToolIndex = currentTool.getToolIndex();
        ColorTool currentColorObject = (ColorTool) getActiveTool(0);
        Color currentColor = currentColorObject.getColor();
        Point2D currentPoint = new Point2D(e.getX(),e.getY());
        //switch case on tool index
        ShapeObject newShape;
        switch (currentToolIndex){
            case(0):
                //first 4 should be the only one here that commits the shape
                //order of draw and get paint shape matters
                PointTool p1 = (PointTool) currentTool;
                p1.draw(g, 3, currentColor, initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                shapeList.add(newShape);
                initialClick = currentPoint;
                break;
            case(1):
                PointTool p2 = (PointTool) currentTool;
                p2.draw(g, 5, currentColor, initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                shapeList.add(newShape);
                initialClick = currentPoint;
                break;
            case(2):
                PointTool p3 = (PointTool) currentTool;
                p3.draw(g, 8, currentColor, initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                shapeList.add(newShape);
                initialClick = currentPoint;
                break;
            case(3):
                PointTool p4 = (PointTool) currentTool;
                p4.draw(g, 12, currentColor, initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                shapeList.add(newShape);
                initialClick = currentPoint;
                break;
            case(4):
                LineTool l = (LineTool) currentTool;
                l.draw(g,currentColor,initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                currentShape = newShape;
                break;
            case(5):
                RectangleTool r = (RectangleTool) currentTool;
                r.draw(g,currentColor,initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                currentShape = newShape;
                break;
            case(6):
                OvalTool o = (OvalTool) currentTool;
                o.draw(g,currentColor,initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                currentShape = newShape;
                break;
            case(7):
                RoundedRectangleTool ro = (RoundedRectangleTool) currentTool;
                ro.draw(g,currentColor,initialClick,currentPoint);
                newShape = currentTool.getPaintShape();
                currentShape = newShape;
                break;
        }


        System.out.println("mouse dragging");
    }

    public void mouseReleased(MouseEvent e){
        //check draggedupdate is false
        //get tool from
        //reset initial click
        System.out.println("mouse released");
        if(currentShape==null){return;}
        if(currentShape.dragUpdate()){return;}
        shapeList.add(currentShape);

        clearCanvas();
        for (int i=0;i<shapeList.size();i++){
            shapeList.get(i).draw(g);
        }

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
            ShapeTool shape = new ShapeTool(i); //will be set in cases
            Circle circle;
            //TODO add a switch case to draw icons and add to shape
            switch(i){

                case(0):
                    //add a 2 pixel circle
                    shape = addMouseHandlerShapeTool(
                            new PointTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    circle = new Circle();
                    circle.setFill(toolFg);
                    circle.setRadius(3);
                    shape.getChildren().add(circle);
                    break;
                case(1):
                    shape = addMouseHandlerShapeTool(
                            new PointTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    circle = new Circle();
                    circle.setFill(toolFg);
                    circle.setRadius(5);
                    shape.getChildren().add(circle);
                    break;
                case(2):
                    shape = addMouseHandlerShapeTool(
                            new PointTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    circle = new Circle();
                    circle.setFill(toolFg);
                    circle.setRadius(8);
                    shape.getChildren().add(circle);
                    break;
                case(3):
                    shape = addMouseHandlerShapeTool(
                            new PointTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    circle = new Circle();
                    circle.setFill(toolFg);
                    circle.setRadius(12);
                    shape.getChildren().add(circle);
                    break;
                case(4):
                    shape = addMouseHandlerShapeTool(
                            new LineTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    Line line = new Line(0,0,40,40);
                    line.setStrokeWidth(3);
                    line.setStroke(toolFg);
                    shape.getChildren().add(line);
                    break;
                case(5):
                    shape = addMouseHandlerShapeTool(
                            new RectangleTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setWidth(40);
                    rectangle.setHeight(40);
                    rectangle.setFill(toolFg);
                    shape.getChildren().add(rectangle);
                    break;
                case(6):
                    shape = addMouseHandlerShapeTool(
                            new OvalTool(i)
                    );
                    shape.getChildren().add(shape.r);
                    circle = new Circle();
                    circle.setFill(toolFg);
                    circle.setRadius(20);
                    shape.getChildren().add(circle);
                    break;
                case(7):
                    shape = addMouseHandlerShapeTool(
                            new RoundedRectangleTool(i)
                    );
                    shape.getChildren().add(shape.r);
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
            AbstractTool previousTool = getActiveTool(colorTool.toolType);
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
            AbstractTool previousTool = getActiveTool(shapeTool.toolType);
            if(shapeTool != previousTool){
                shapeTool.deactivate(previousTool, shapeTool.toolType);
                setActiveTool(shapeTool);
                shapeTool.activate(shapeTool);
            }
            System.out.println("tool clicked: " + shapeTool.getToolIndex());
        });
        return shapeTool;
    }

    private ActionTool addMouseHandlerActionTool(ActionTool actionTool){
        actionTool.setOnMouseReleased(e ->actionTool.activate(actionTool));
        actionTool.setOnMousePressed(e -> actionTool.activate(actionTool));

        actionTool.setOnMouseClicked(e -> myClearAction.run());
        return actionTool;
    }

    public AbstractTool getActiveTool(int toolType){
        return activeTools.get(toolType);
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
                s.r.setStroke(Color.LIGHTCORAL);
                s.r.setStrokeWidth(2);
            }
            else{
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
    int toolIndex;
    ShapeObject shapeObject;
    ShapeObject getPaintShape(){
        return shapeObject;
    }
    public ShapeTool(int toolIndex){
        toolType=1;
        this.toolIndex = toolIndex;
    }
    public int getToolIndex(){
        return toolIndex;
    }

}

class ActionTool extends AbstractTool{
    //rectangle, label, action
    Color textColor = SimplePaintObjects.TOOL_FG;
    String text = "Clear";


    javafx.scene.control.Label label;
    public ActionTool(){
        toolType=2;
        label = new Label(text);
        label.setFont(Font.font(null, FontWeight.BOLD, 20));
        label.setTextFill(textColor);

    }
}

/*
    SHAPE TOOLS
 */

class PointTool extends ShapeTool{

    public PointTool(int toolIndex){
        super(toolIndex);
    }

    public void draw(GraphicsContext g, int width, Color color, Point2D start,
                     Point2D end){

        shapeObject = new LineSegmentShape(width,color,start,end);
        shapeObject.draw(g);
    }
    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}

class LineTool extends ShapeTool{

    public LineTool(int toolIndex){
        super(toolIndex);
    }

    public void draw(GraphicsContext g, Color color, Point2D start, Point2D end){
        shapeObject = new LineShape(color,start,end);
        shapeObject.draw(g);
    }
    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}

class RectangleTool extends ShapeTool{
    public RectangleTool(int i){
        super(i);
    }
    public void draw(GraphicsContext g, Color color, Point2D start, Point2D end){

        shapeObject = new RectangleShape(color,start,end);
        shapeObject.draw(g);
    }
    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}

class OvalTool extends ShapeTool{
    public OvalTool(int i){
        super(i);
    }
    public void draw(GraphicsContext g, Color color, Point2D start, Point2D end){
        shapeObject = new OvalShape(color,start,end);
        shapeObject.draw(g);
    }
    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}

class RoundedRectangleTool extends ShapeTool{
    public RoundedRectangleTool(int toolIndex){
        super(toolIndex);
    }
    public void draw(GraphicsContext g, Color color, Point2D start, Point2D end){
        shapeObject = new RoundedRectangleShape(color,start,end);
        shapeObject.draw(g);
    }
    public ShapeObject getPaintShape(){
        return shapeObject;
    }
}


/*
    SHAPE OBJECTS
 */
interface ShapeObject{
    void draw(GraphicsContext g);
    boolean dragUpdate();

}

class LineSegmentShape implements ShapeObject{
    int width;
    Color color;
    Point2D start;
    Point2D end;
    public LineSegmentShape(int width, Color color, Point2D start,
                            Point2D end){
        this.width = width;
        this.color = color;
        this.start = start;
        this.end = end;
    }
    public boolean dragUpdate(){
        return true;
    }

    public void draw(GraphicsContext g){
        g.setStroke(color);
        g.setLineWidth(width);
        g.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

}

class LineShape implements ShapeObject{
    Color color;
    Point2D start;
    Point2D end;
    public LineShape(Color color, Point2D start, Point2D end){
        this.color = color;
        this.start = start;
        this.end = end;
    }
    public boolean dragUpdate(){
        return false;
    }

    public void draw(GraphicsContext g){
        g.setStroke(color);
        g.setLineWidth(5);
        g.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

}
//parent class to a few shape objects
class FilledPolyShape implements ShapeObject{
    //corrects the size of the polygon
    Color color;
    Point2D start;
    Point2D end;
    public FilledPolyShape(Color color, Point2D start, Point2D end){
        this.color = color;
        this.start = start;
        this.end = end;
    }
    public boolean dragUpdate(){
        return false;
    }
    public void draw(GraphicsContext g){

    }
}

class RectangleShape extends FilledPolyShape{
    public RectangleShape(Color color, Point2D start, Point2D end){
        super(color, start, end);
    }
    public void draw(GraphicsContext g){
        //need to offset so start in middle and bottom R corner is on mouse
        double startX = start.getX();
        double startY = start.getY();
        double endX = end.getX();
        double endY = end.getY();
        double width = Math.abs(endX-startX);
        double height = Math.abs(endY-startY);
        double x = startX -  width;
        double y = startY - height;
        g.setFill(color);
        g.fillRect(x,y,2*width,2*height);
    }

}

class OvalShape extends FilledPolyShape{
    public OvalShape(Color color, Point2D start, Point2D end){
        super(color, start, end);
    }
    public void draw(GraphicsContext g){
        double startX = start.getX();
        double startY = start.getY();
        double endX = end.getX();
        double endY = end.getY();
        double width = Math.abs(endX-startX);
        double height = Math.abs(endY-startY);

        double x = startX - (width);
        double y = startY - (height);

        g.setFill(color);
        g.fillOval(x,y,2*width,2*height);
    }
}

class RoundedRectangleShape extends FilledPolyShape{
    public RoundedRectangleShape(Color color, Point2D start, Point2D end){
        super(color, start, end);
    }
    public void draw(GraphicsContext g){
        double startX = start.getX();
        double startY = start.getY();
        double endX = end.getX();
        double endY = end.getY();
        double width = Math.abs(endX-startX);
        double height = Math.abs(endY-startY);
        double x = startX -  width;
        double y = startY - height;
        g.setFill(color);
        g.fillRoundRect(x,y,2*width,2*height,20,20);
    }
}