import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

/**
 * A simple program where the user can sketch curves in a variety of
 * colors.  A color palette is shown along the right edge of the canvas.
 * The user can select a drawing color by clicking on a color in the
 * palette.  Under the colors is a "Clear button" that the user
 * can click to clear the sketch.  The user draws by clicking and
 * dragging in a large white area that occupies most of the canvas.
 */
public class SimpleToolPaint extends Application {

    /**
     * This main routine allows this class to be run as a program.
     */
    public static void main(String[] args) {
        launch();
    }

    //-----------------------------------------------------------------

    
    /*
     * Array of colors corresponding to available colors in the palette.
     * (The last color is a slightly darker version of yellow for
     * better visibility on a white background.)
     */
    private final Color[] palette = {
            Color.BLACK, Color.RED, Color.GREEN, Color.BLUE,
            Color.CYAN, Color.MAGENTA, Color.color(0.95,0.9,0)
    };

    private int currentColorNum = 0;  // The currently selected drawing color,
                                      //coded as an index into the above array

    private int currentToolNum = 0;

    private double prevX, prevY;   // The previous location of the mouse, when
                                   // the user is drawing by dragging the mouse.
    private double initialX, initialY;

    private double squareInitialX, squareInitialY;

    private boolean dragging;   // This is set to true while the user is drawing

    private Canvas canvas;  // The canvas on which everything is drawn.

    private GraphicsContext g;  // For drawing on the canvas.


    /**
     * The start() method creates the GUI, sets up event listening, and
     * shows the window on the screen.
     */
    public void start(Stage stage) {
        
        /* Create the canvans and draw its content for the first time. */
        
        canvas = new Canvas(600,400);
        g = canvas.getGraphicsContext2D();
        g.setLineWidth(2);  // Use a 2-pixel-wide line for drawing.
        clearAndDrawPalette();
        
        /* Respond to mouse events on the canvas
        by calling methods in this class. */
        
        canvas.setOnMousePressed( e -> mousePressed(e) );
        canvas.setOnMouseDragged( e -> mouseDragged(e) );
        canvas.setOnMouseReleased( e -> mouseReleased(e) );
        
        /* Configure the GUI and show the window. */
        
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Simple Paint");
        stage.show();
    }


    /**
     * Fills the canvas with white and draws the color palette and (simulated)
     * "Clear" button on the right edge of the canvas.  This method is called
     * when the canvas is created and when the user clicks "Clear."
     */
    public void clearAndDrawPalette() {

        //get original line width so it can be reset at end
        double originalWidth = g.getLineWidth();
        int width = (int)canvas.getWidth();    // Width of the canvas.
        int height = (int)canvas.getHeight();  // Height of the canvas.

        g.setFill(Color.WHITE);
        g.fillRect(0,0,width,height);

        int colorSpacing = (height - 56) / 7;
        // Distance between the top of one colored rectangle in the palette
        // and the top of the rectangle below it.  The height of the
        // rectangle will be colorSpacing - 3.  There are 7 colored rectangles,
        // so the available space is divided by 7.  The available space allows
        // for the gray border and the 50-by-50 CLEAR button.

        /* Draw a 3-pixel border around the canvas in gray.  This has to be
             done by drawing three rectangles of different sizes. */

        g.setStroke(Color.GRAY);
        g.setLineWidth(3);
        g.strokeRect(1.5, 1.5, width-3, height-3);

        /* Draw a gray rectangle along the right edge of the canvas.
             The color palette and Clear button will be drawn on top of this.
             (This covers some of the same area as the border I just drew. */

        //change the width of this to fit new options

        g.setFill(Color.GRAY);
        g.fillRect(width - 112, 0, 112, height);

        /* Draw the "Clear button" as a white rectangle in the lower right
             corner of the canvas, allowing for a 3-pixel border. */

        g.setFill(Color.WHITE);
        g.fillRect(width-53,  height-53, 50, 50);
        g.setFill(Color.BLACK);
        g.fillText("CLEAR", width-48, height-23); 

        /* Draw the seven color rectangles. */
        
        for (int N = 0; N < 7; N++) {
            g.setFill( palette[N] );
            g.fillRect(width-53,3+N*colorSpacing,50,colorSpacing-3);
        }

        //draw each tool's white square with a loop
        for (int i=0; i<8; i++) {

            int toolX = width - 109; //was 106
            int toolY = 3 + i * colorSpacing;

            int toolWidth = 50;
            int toolHeight = colorSpacing - 3;

            //draw each tool's square
            g.setFill(Color.WHITE);
            g.fillRect(toolX, toolY, toolWidth, toolHeight);

            //draw each tool's image
            g.setFill(Color.BLACK);
            if (i == 0) {
                //subtract half the circles width&height from x and y position
                double ovalX = (toolX + (toolWidth / 2.0)) - 1.5;
                double ovalY = (toolY + (toolHeight / 2.0)) - 1.5;
                g.fillOval(ovalX, ovalY, 3, 3);
            }

            if (i == 1) {
                double ovalX = (toolX + (toolWidth / 2.0)) - 2.5;
                double ovalY = (toolY + (toolHeight / 2.0)) - 2.5;
                g.fillOval(ovalX, ovalY, 5, 5);
            }

            if (i == 2) {
                double ovalX = (toolX + (toolWidth / 2.0)) - 4;
                double ovalY = (toolY + (toolHeight / 2.0)) - 4;
                g.fillOval(ovalX, ovalY , 8, 8);
            }

            if (i == 3) {
                double ovalX = (toolX + (toolWidth / 2.0)) - 6;
                double ovalY = (toolY + (toolHeight / 2.0)) - 6;
                g.fillOval(ovalX, ovalY , 12, 12);
            }

            if (i == 4) {
                double linePadding = 10;
                double startX = toolX + linePadding;
                double startY = toolY + linePadding;
                double endX = toolX + toolWidth - linePadding;
                double endY = toolY + toolHeight - linePadding;

                g.setStroke(Color.BLACK);
                g.strokeLine(startX, startY, endX, endY);
            }

            if (i == 5) {
                double squarePadding = 5;
                double startX = toolX + squarePadding;
                double startY = toolY + squarePadding;
                double endX = toolWidth - 2 * squarePadding;
                double endY = toolHeight - 2 * squarePadding;
                g.fillRect(startX, startY, endX, endY);
            }

            if (i == 6) {
                double ovalX = (toolX + (toolWidth / 2.0)) - 20;
                double ovalY = (toolY + (toolHeight / 2.0)) - 20;
                g.fillOval(ovalX, ovalY, 40, 40);
            }

            if (i == 7) {
                //TODO is this doing nothing?
                double squarePadding = 5;
                double startX = toolX + squarePadding;
                double startY = toolY + squarePadding;
                double endX = toolWidth - 2 * squarePadding;
                double endY = toolHeight - 2 * squarePadding;
                double a = 15;
                g.fillRoundRect(startX, startY, endX, endY, a,a);
            }
        }

        /* Draw a 2-pixel white border around the color rectangle
             of the current drawing color. */

        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        double borderX = width-54;
        double borderY = 2 + currentColorNum*colorSpacing;
        g.strokeRect(borderX, borderY, 52, colorSpacing-1);

        //Draw border over current tool
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        borderX = width-110;
        borderY = 2 + currentColorNum*colorSpacing;
        g.strokeRect(borderX, borderY, 52, colorSpacing-1);

        g.setLineWidth(originalWidth); //reset line width

        g.setFill(palette[currentColorNum]);
    } // end clearAndDrawPalette()


    /**
     * Change the drawing color after the user has clicked the
     * mouse on the color palette at a point with y-coordinate y.
     */
    private void changeColor(int y) {

        int width = (int)canvas.getWidth(); 
        int height = (int)canvas.getHeight();
        // Space for one color rectangle.
        int colorSpacing = (height - 56) / 7;
        // Which color number was clicked?
        int newColor = y / colorSpacing;
        // Make sure the color number is valid.
        if (newColor < 0 || newColor > 6)
            return;

        /* Remove the highlight from the current color, by drawing over it in
        gray. Then change the current drawing color and draw a highlight around
        the new drawing color.  */

        double originalWidth = g.getLineWidth();
        g.setLineWidth(2);
        g.setStroke(Color.GRAY);
        double rectX = width-54;
        double rectY = 2 + currentColorNum*colorSpacing;
        g.strokeRect(rectX, rectY, 52, colorSpacing-1);
        currentColorNum = newColor;
        g.setStroke(Color.WHITE);
        double whiteX = width-54;
        double whiteY = 2 + currentColorNum*colorSpacing;
        g.strokeRect(whiteX, whiteY, 52, colorSpacing-1);
        g.setLineWidth(originalWidth);

        g.setFill(palette[currentColorNum]);

    } // end changeColor()


    private void changeTool(int y){
        //get canvas dimensions
        int width = (int)canvas.getWidth();
        int height = (int)canvas.getHeight();

        //TODO assign currentToolNum based on spacing
        int toolSpacing = (height-2)/ 8;
        int newTool = y / toolSpacing;
        if (newTool < 0 || newTool > 7)
            return;
        System.out.println(newTool);


        //TODO set border over current tool
        double originalWidth = g.getLineWidth();
        g.setLineWidth(2);
        g.setStroke(Color.GRAY);
        double borderX = width-110;
        double borderY = 2 + currentToolNum*toolSpacing;
        g.strokeRect(borderX, borderY, 52, toolSpacing-1);
        currentToolNum = newTool;
        g.setStroke(Color.WHITE);
        double whiteX = width-110;
        double whiteY = 2 + currentToolNum*toolSpacing;
        g.strokeRect(whiteX, whiteY, 52, toolSpacing-1);

        g.setLineWidth(originalWidth);

        //TODO Functionality
        //set global variable so mousePressed knows what function is selected
    }

    /**
     * This is called when the user presses the mouse anywhere in the canvas.  
     * There are three possible responses, depending on where the user clicked:  
     * Change the current color, clear the drawing, or start drawing a curve.  
     * (Or do nothing if user clicks on the border.)
     */
    public void mousePressed(MouseEvent evt) {

        if (dragging == true)  // Ignore mouse presses that occur
            return;            //    when user is already drawing a curve.
                               //    (This can happen if the user presses
                               //    two mouse buttons at the same time.)

        int x = (int)evt.getX();   // x-coordinate where the user clicked.
        int y = (int)evt.getY();   // y-coordinate where the user clicked.

        int width = (int)canvas.getWidth();    // Width of the canvas.
        int height = (int)canvas.getHeight();  // Height of the canvas.

        //updated to width - 54 instead 53
        if (x > width - 54) {
            // User clicked to the right of the drawing area.
            // This click is either on the clear button or
            // on the color palette.
            if (y > height - 54)
                clearAndDrawPalette();  //  Clicked on "CLEAR button".
            else
                System.out.println("Clicked on color");
                changeColor(y);  // Clicked on the color palette.
        }

        //clicked on tool
        else if (x>= width - 110){
            //call change tool method with y coordinate as param
            System.out.println("Clicked on tool");
            changeTool(y);

            //TODO check for each tool based off current tool
            switch(currentToolNum){
                //2 pixel width
                case(0):
                    g.setLineWidth(2);
                    break;
                //4 pixel width
                case(1):
                    g.setLineWidth(4);
                    break;
                case(2):
                    g.setLineWidth(6);
                    break;
                case(3):
                    g.setLineWidth(8);
                    break;
                //line tool
                case(4):
                    //nothing so far
                    g.setFill(palette[currentColorNum]);
                    g.setLineWidth(4);
                    break;
                //square tool
                case(5):
                    g.setFill(palette[currentColorNum]);
                    break;
                case(6):
                    g.setFill(palette[currentColorNum]);
                    break;
            }
        }

        //updating width of canvas area from 56 to 112
        else if (x > 3 && x < width - 112 && y > 3 && y < height - 3) {
            // The user has clicked on the white drawing area.
            // Start drawing a curve from the point (x,y).
            //TODO add check for which tool

            List<Integer> list = Arrays.asList(0, 1, 2, 3);
            if(list.contains(currentToolNum)) {
                prevX = x;
                prevY = y;
                dragging = true;
                g.setStroke(palette[currentColorNum]);
            }

            //straight line
            else if(currentToolNum ==4){
                //track input on click down
                //update a global variable
                //start tracking as soon as its dragging
                //actually draw it once mouse is released
                System.out.println("click initiated from drag");
                initialX = x;
                initialY = y;
                dragging = true;
                g.setStroke(palette[currentColorNum]);
            }
            else if(currentToolNum == 5){
                /*
                double squarePadding = 25;
                double squareDimension = 50;
                double startX = x - squarePadding;
                double startY = y - squarePadding;
                double endX = squareDimension;
                double endY = squareDimension;
                g.fillRect(startX, startY, endX, endY);
                 */

                //set global variable with initial x and y
                //in dragging the square will be drawn
                squareInitialX = x;
                squareInitialY = y;
                dragging = true;
                g.setStroke(palette[currentColorNum]);

            }
            else if(currentToolNum == 6){
                squareInitialX = x;
                squareInitialY = y;
                dragging = true;
                g.setStroke(palette[currentColorNum]);
            }
            else if(currentToolNum ==7){
                squareInitialX = x;
                squareInitialY = y;
                dragging = true;
                g.setStroke(palette[currentColorNum]);
            }
        }

    } // end mousePressed()


    /**
     * Called whenever the user releases the mouse button. Just sets
     * dragging to false.
     */
    public void mouseReleased(MouseEvent evt) {
        dragging = false;
    }


    /**
     * Called whenever the user moves the mouse while a mouse button is held
     * down. If the user is drawing, draw a line segment from the previous
     * mouse location to the current mouse location, and set up prevX and
     * prevY for the next call. Note that in case the user drags outside of
     * the drawing area, the values of x and y are "clamped" to lie within this
     * area. This avoids drawing on the color palette or clear button.
     */
    public void mouseDragged(MouseEvent evt) {

        //adding a check for what tool is in use


        if (dragging == false)
            return;  // Nothing to do because the user isn't drawing.

        double x = evt.getX();   // x-coordinate of mouse.
        double y = evt.getY();   // y-coordinate of mouse.

        //updating width from 57 to 112
        if (x < 3)                          // Adjust the value of x,
            x = 3;                           //   to make sure it's in
        if (x > canvas.getWidth() - 112)       //   the drawing area.
            x = (int)canvas.getWidth() - 112;

        if (y < 3)                          // Adjust the value of y,
            y = 3;                           //   to make sure it's in
        if (y > canvas.getHeight() - 4)       //   the drawing area.
            y = canvas.getHeight() - 4;



        //check for tool 4
        System.out.println("dragging with tool" + currentToolNum);
        if (currentToolNum == 4){
            System.out.println("hit the if statement" + currentToolNum);
            g.setFill(palette[currentColorNum]);
            g.strokeLine(initialX, initialY, x, y);

        }
        else if(currentToolNum==5){
            g.setFill(palette[currentColorNum]);

            double rectX = 2* Math.abs(x-squareInitialX);
            double rectY = 2 *Math.abs(y-squareInitialY);

            //TODO fix for dragging in other directions

            g.fillRect(Math.abs(x-rectX), Math.abs(y-rectY), rectX, rectY);
        }
        else if (currentToolNum==6){
            //TODO refactor with proper names for circles
            g.setFill(palette[currentColorNum]);

            double rectX = 2* Math.abs(x-squareInitialX);
            double rectY = 2 *Math.abs(y-squareInitialY);
            g.fillOval(Math.abs(x-rectX), Math.abs(y-rectY), rectX, rectY);
        }
        else if(currentToolNum == 7){
            //TODO refactor with proper names for circles
            g.setFill(palette[currentColorNum]);

            double rectX = 2* Math.abs(x-squareInitialX);
            double rectY = 2 *Math.abs(y-squareInitialY);

            double roundX = Math.abs(x-rectX);
            double roundY = Math.abs(y-rectY);
            g.fillRoundRect(roundX, roundY, rectX, rectY,15,15);
        }
        //put this as default
        else {
            g.strokeLine(prevX, prevY, x, y);  // Draw the line.
        }

        prevX = x;  // Get ready for the next line segment in the curve.
        prevY = y;

    } // end mouseDragged()


} // end class SimplePaint
