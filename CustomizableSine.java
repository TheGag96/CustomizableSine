import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *  Author: TheGag96
 *
 *  A GUI I made to help play around with different sine shapes and visualize the waveforms they make.
 *  Heavily inspired by these <a href="http://squine.milosz.ca/">Reddit</a> <a href="http://www.reddit.com/r/math/comments/2rbt2f/the_squine_post_yesterday_inspired_me_to_make/">posts</a>.
 *
 */
public class CustomizableSine extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new CustomizableSine();
    }

    final DrawPanel drawPanel = new DrawPanel();
    final JToolBar toolBar = new JToolBar();
    final BorderLayout layout = new BorderLayout();
    final JButton squareButton = new JButton();
    final JButton circleButton = new JButton();
    final JButton triangleButton = new JButton();
    final JButton polygonButton = new JButton();
    final JButton drawButton = new JButton();
    final JLabel rotationLabel = new JLabel();
    final JSlider rotationSlider = new JSlider();
    final JLabel polygonLabel = new JLabel();
    final JSlider polygonSlider = new JSlider();
    final JLabel frequencyLabel = new JLabel();
    final JSlider frequencySlider = new JSlider();

    public CustomizableSine() {
        super();
        setTitle("Customizable Sine!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1516,589);
        //setResizable(false);

        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        circleButton.addActionListener(this);
        circleButton.setActionCommand("circle");
        squareButton.addActionListener(this);
        squareButton.setActionCommand("square");
        triangleButton.addActionListener(this);
        triangleButton.setActionCommand("triangle");
        polygonButton.addActionListener(this);
        polygonButton.setActionCommand("polygon");
        drawButton.addActionListener(this);
        drawButton.setActionCommand("draw");
        polygonSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //update label (centered and multiline, of course) and create new polygon
                polygonLabel.setText("<html><center>Sides:<br>" + polygonSlider.getValue() + "</center></html>");
                actionPerformed(new ActionEvent(this, 0, "polygon"));
            }
        });
        rotationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //update label and create new polygon
                rotationLabel.setText("<html><center>Rotation:<br>" + rotationSlider.getValue() + "%</center></html>");
                performRotation();
            }
        });
        frequencySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                frequencyLabel.setText("<html><center>Frequency:<br>"+String.format("%.1f", frequencySlider.getValue()/10.0)+"</center></html>");
            }
        });

        //TODO: use getClass().getResource("x.png") for image icons in release build
        circleButton.setIcon(new ImageIcon("circle.png"));
        squareButton.setIcon(new ImageIcon("square.png"));
        triangleButton.setIcon(new ImageIcon("triangle.png"));
        polygonButton.setIcon(new ImageIcon("polygon.png"));
        drawButton.setIcon(new ImageIcon("draw.png"));

        circleButton.setToolTipText("Circle");
        squareButton.setToolTipText("Square");
        triangleButton.setToolTipText("Triangle");
        polygonButton.setToolTipText("Polygon");
        drawButton.setToolTipText("Free Draw");

        frequencySlider.setMinimum(0);
        frequencySlider.setMaximum(40);
        frequencySlider.setPaintTicks(true);
        frequencySlider.setMinorTickSpacing(5);
        frequencySlider.setMajorTickSpacing(10);
        frequencySlider.setValue(10);

        rotationSlider.setMinimum(0);
        rotationSlider.setMaximum(100);
        rotationSlider.setPaintTicks(true);
        rotationSlider.setMinorTickSpacing(10);
        rotationSlider.setMajorTickSpacing(50);
        rotationSlider.setValue(0);

        polygonSlider.setMinimum(3);
        polygonSlider.setMaximum(15);
        polygonSlider.setValue(3);
        polygonSlider.setMinorTickSpacing(1);
        polygonSlider.setMajorTickSpacing(12);
        polygonSlider.setSnapToTicks(true);
        polygonSlider.setPaintTicks(true);
        polygonSlider.setVisible(false);
        //polygonSlider.setPaintLabels(true);   //note: resizes toolbar due to size
        polygonLabel.setVisible(false);

        Container contentPane = getContentPane();
        toolBar.add(circleButton);
        toolBar.add(squareButton);
        toolBar.add(triangleButton);
        toolBar.add(polygonButton);
        toolBar.add(drawButton);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(frequencyLabel);
        toolBar.add(frequencySlider);
        toolBar.add(rotationLabel);
        toolBar.add(rotationSlider);
        toolBar.add(polygonLabel);
        toolBar.add(polygonSlider);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar,BorderLayout.NORTH);
        contentPane.add(drawPanel);

        //create timer that updates the drawing panel every frame (a little faster than 60 fps)
        Timer timer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.repaint();
            }
        });

        setVisible(true);
        actionPerformed(new ActionEvent(this, 0, "circle"));
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("circle")) {
            circleButton.setEnabled(false);
            squareButton.setEnabled(true);
            triangleButton.setEnabled(true);
            polygonButton.setEnabled(true);
            drawButton.setEnabled(true);
            drawPanel.mode = 0;
            rotationSlider.setEnabled(true);
            polygonSlider.setVisible(false);
            polygonLabel.setVisible(false);
            rotationSlider.setValue(0);

            drawPanel.drawingLines.clear();
            drawPanel.waveLines.clear();

            double angle = 0;
            double step = 2*Math.PI/360;
            double center = drawPanel.getHeight()/2;
            double radius = drawPanel.getHeight()/4;

            double lastX = center + radius;
            double lastY = center;

            while (angle <= 2*Math.PI) {
                angle += step;
                double newX = center + radius*Math.cos(angle);
                double newY = center + radius*Math.sin(angle);
                drawPanel.drawingLines.add(new Line2D.Double(lastX, lastY, newX, newY));
                lastX = newX;
                lastY = newY;
            }

        }
        else if (command.equals("square")) {
            circleButton.setEnabled(true);
            squareButton.setEnabled(false);
            triangleButton.setEnabled(true);
            polygonButton.setEnabled(true);
            drawButton.setEnabled(true);
            drawPanel.mode = 1;
            rotationSlider.setEnabled(true);
            polygonSlider.setVisible(false);
            polygonLabel.setVisible(false);
            rotationSlider.setValue(0);

            drawPanel.drawingLines.clear();
            drawPanel.waveLines.clear();

            double center = drawPanel.getHeight()/2;

            drawPanel.drawingLines.add(new Line2D.Double(center/2, center+center/2, center+center/2, center+center/2));
            drawPanel.drawingLines.add(new Line2D.Double(center/2, center/2, center+center/2, center/2));
            drawPanel.drawingLines.add(new Line2D.Double(center+center/2, center/2, center+center/2, center+center/2));
            drawPanel.drawingLines.add(new Line2D.Double(center/2, center/2, center/2, center+center/2));
        }
        else if (command.equals("triangle")) {
            circleButton.setEnabled(true);
            squareButton.setEnabled(true);
            triangleButton.setEnabled(false);
            polygonButton.setEnabled(true);
            drawButton.setEnabled(true);
            drawPanel.mode = 2;
            rotationSlider.setEnabled(true);
            polygonSlider.setVisible(false);
            polygonLabel.setVisible(false);
            rotationSlider.setValue(0);

            drawPanel.drawingLines.clear();
            drawPanel.waveLines.clear();

            double center = drawPanel.getHeight()/2;

            drawPanel.drawingLines.add(new Line2D.Double(center/2, center+center/2, center+center/2, center+center/2));
            drawPanel.drawingLines.add(new Line2D.Double(center/2, center+center/2, center, center/2));
            drawPanel.drawingLines.add(new Line2D.Double(center, center/2, center+center/2, center+center/2));
        }
        else if (command.equals("polygon")) {
            circleButton.setEnabled(true);
            squareButton.setEnabled(true);
            triangleButton.setEnabled(true);
            polygonButton.setEnabled(false);
            drawButton.setEnabled(true);
            drawPanel.mode = 3;
            rotationSlider.setEnabled(true);
            polygonSlider.setVisible(true);
            polygonLabel.setVisible(true);
            rotationSlider.setValue(0);

            drawPanel.drawingLines.clear();
            drawPanel.waveLines.clear();

            double angle = 0;
            double step = 2*Math.PI/polygonSlider.getValue();
            double center = drawPanel.getHeight()/2;
            double radius = drawPanel.getHeight()/4;

            double lastX = center + radius;
            double lastY = center;

            while (angle <= 2*Math.PI) {
                angle += step;
                double newX = center + radius*Math.cos(angle);
                double newY = center + radius*Math.sin(angle);
                drawPanel.drawingLines.add(new Line2D.Double(lastX, lastY, newX, newY));
                lastX = newX;
                lastY = newY;
            }

        }
        else if (command.equals("draw")) {
            circleButton.setEnabled(true);
            squareButton.setEnabled(true);
            triangleButton.setEnabled(true);
            polygonButton.setEnabled(true);
            drawButton.setEnabled(false);
            drawPanel.mode = 4;
            rotationSlider.setEnabled(true);
            polygonSlider.setVisible(false);
            polygonLabel.setVisible(false);
            rotationSlider.setValue(0);

            drawPanel.drawingLines.clear();
            drawPanel.waveLines.clear();
        }
    }

    public void performRotation() {
        drawPanel.rotatedLines.clear();
        double rotAngle = 2*Math.PI*rotationSlider.getValue()/100;
        double center = drawPanel.getHeight()/2;
        double x1,y1,x2,y2;
        for (Line2D.Double line : drawPanel.drawingLines) {
            x1 = (line.x1-center)*Math.cos(rotAngle) - (line.y1-center)*Math.sin(rotAngle) + center;
            y1 = (line.x1-center)*Math.sin(rotAngle) + (line.y1-center)*Math.cos(rotAngle) + center;
            x2 = (line.x2-center)*Math.cos(rotAngle) - (line.y2-center)*Math.sin(rotAngle) + center;
            y2 = (line.x2-center)*Math.sin(rotAngle) + (line.y2-center)*Math.cos(rotAngle) + center;
            drawPanel.rotatedLines.add(new Line2D.Double((int)x1,(int)y1,(int)x2,(int)y2));
        }
    }

    public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener {
        public ArrayList<Line2D.Double> drawingLines, rotatedLines, waveLines;
        private int lastX, lastY, originalX, originalY;
        private double angle, angleChange;
        private boolean currentlyDrawing = false;
        public int mode; //0: circle, 1: square, 2: triangle, 3: draw

        public int drawRegionSize;
        public static final int waveSpeed = 2;

        public DrawPanel() {
            super();
            setBackground(Color.white);
            addMouseMotionListener(this);
            addMouseListener(this);
            drawingLines = new ArrayList<Line2D.Double>();
            rotatedLines = new ArrayList<Line2D.Double>();
            waveLines = new ArrayList<Line2D.Double>();
            angle = 0;
            angleChange = 0.016*2*Math.PI;
            mode = 0;
            drawRegionSize = getHeight();
            lastX = -1;
            lastY = -1;
        }

        @Override
        /**
         *
         *  Pretty much all main logic here
         *
         */
        public void paint(Graphics g) {
            super.paint(g);

            //choose line list to use based on whether or not we chose to rotate our drawing
            ArrayList<Line2D.Double> chosenLines = (rotationSlider.getValue() == 0 ? drawingLines : rotatedLines);

            drawRegionSize = getHeight();
            double center = drawRegionSize /2;

            //draw all lines of the drawing
            g.setColor(Color.black);
            for (Line2D.Double line : chosenLines) {
                g.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
            }

            //draw line to starting point that gets updated every frame
            if (mode == 4 && currentlyDrawing) {
                g.drawLine(originalX, originalY, lastX, lastY);
            }

            //draw crosshair
            g.setColor(Color.gray);
            g.drawLine((int) center - 10, (int) center, (int) center + 10, (int) center);
            g.drawLine((int) center, (int) center - 10, (int) center, (int) center + 10);

            angleChange = .016*2*Math.PI*frequencySlider.getValue()/10;
            angle += angleChange;
            if (angle >= -2*Math.PI) angle += 2*Math.PI;    //restrict angle to [0, 2*pi)

            //make sure drawing lines don't make their way into the wave region
            g.setColor(Color.white);
            g.fillRect(drawRegionSize+1,0,getWidth(),getHeight());

            //draw divider and wave x-axis line
            g.setColor(Color.gray);
            g.drawLine(drawRegionSize, 0, drawRegionSize, drawRegionSize);
            g.setColor(Color.gray.brighter());
            g.drawLine(drawRegionSize + 1, getHeight() / 2, getWidth(), getHeight() / 2);

            //givin' myself some props
            g.setColor(Color.gray.brighter());
            g.drawString("made by TheGag96", getWidth() - 115, getHeight() - 10);

            //if we're drawing or there is no drawing, we don't need to calculate any intersections nor draw the wave
            if (currentlyDrawing || drawingLines.size() == 0) return;

            g.setColor(Color.red);

            //negative angle is used in the trig functions because in swing coordinates, increasing y means going down, not up
            Line2D.Double centerLine = new Line2D.Double(center, center,
                    center+ drawRegionSize /2*Math.sqrt(2)*Math.cos(-angle),
                    center+ drawRegionSize /2*Math.sqrt(2)*Math.sin(-angle));

            //find intersection from center to the nearest point on the drawing
            double intersectX = -1, intersectY = -1;
            for (Line2D.Double line : chosenLines) {
                if (line.intersectsLine(centerLine)) {
                    //use some good old line geometry to figure out the intersection point!
                    double m1 = getLineSlope(line);
                    double m2 = getLineSlope(centerLine);
                    double b1 = line.getY1() - m1 * line.getX1();
                    double b2 = centerLine.getY1() - m2 * centerLine.getX1();
                    double newIntersectX = (b2-b1)/(m1-m2);
                    double newIntersectY = m1*newIntersectX+b1;

                    //correct some vertical line issues
                    if ((int)centerLine.getX2()-(int)centerLine.getX1() == 0) {
                        newIntersectX = center;
                        newIntersectY = m1*newIntersectX+b1;
                    }

                    if ((int)line.getX2()-(int)line.getX1() == 0 || m1 > 1000) {  //second condition needed for some really odd slope issues
                        newIntersectX = line.getX1();
                        newIntersectY = m2*newIntersectX+b2;
                    }

                    if (intersectX == -1 || Math.sqrt( Math.pow((center-newIntersectX),2) + Math.pow((center-newIntersectY),2) ) < Math.sqrt( Math.pow((center-intersectX),2) + Math.pow((center-intersectY),2) )) {
                        intersectX = newIntersectX;
                        intersectY = newIntersectY;
                    }
                }
            }

            //draw line from center to new intersection point and highlight point
            if (intersectX != -1) {
                g.drawLine((int) centerLine.x1, (int) centerLine.y1, (int) intersectX, (int) intersectY);
                g.fillOval((int) intersectX - 3, (int) intersectY - 3, 6, 6);
            }

            //draw red line from intersection to divider
            if (intersectX != -1) {
                g.drawLine((int) intersectX, (int) intersectY, drawRegionSize, (int) intersectY);
            }

            //move over all wave lines and add new one
            for (int i = 0; i < waveLines.size(); i++) {
                Line2D.Double line = waveLines.get(i);
                line.x1 += waveSpeed;
                line.x2 += waveSpeed;
                if (line.x1 > getWidth())   //remove if off-screen
                {
                    waveLines.remove(i);
                }
            }

            if (waveLines.size() == 0) {
                waveLines.add(new Line2D.Double(drawRegionSize, intersectY, drawRegionSize + waveSpeed, intersectY));
            }
            else {
                Line2D.Double lastLine = waveLines.get(waveLines.size() - 1);
                waveLines.add(new Line2D.Double(drawRegionSize, intersectY, lastLine.x1, lastLine.y1));
            }

            //draw all wave lines
            g.setColor(Color.red);
            for (Line2D.Double line : waveLines) {
                g.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
            }


        }


        @Override
        public void mouseDragged(MouseEvent e) {
            //only within the drawing region!
            if (e.getX() >= drawRegionSize || e.getX() < 0 || e.getY() < 0 || e.getY() > drawRegionSize) return;

            //left click only (special code for mousemotionlistener hooks)
            if (SwingUtilities.isLeftMouseButton(e) && mode == 4) {
                //when the mouse gets dragged, add the line from the last point to this one for our drawing
                drawingLines.add(new Line2D.Double(lastX, lastY, e.getX(), e.getY()));
            }
            else if (SwingUtilities.isRightMouseButton(e) && lastX != -1) {    //right click to drag image!
                for (Line2D.Double line : drawingLines) {
                    line.x1 += e.getX() - lastX;
                    line.y1 += e.getY() - lastY;
                    line.x2 += e.getX() - lastX;
                    line.y2 += e.getY() - lastY;
                }

                for (Line2D.Double line : rotatedLines) {
                    line.x1 += e.getX() - lastX;
                    line.y1 += e.getY() - lastY;
                    line.x2 += e.getX() - lastX;
                    line.y2 += e.getY() - lastY;
                }
            }

            //save last positions for next drag
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            //within the region only!
            if (e.getX() >= drawRegionSize || e.getX() < 0 || e.getY() < 0 || e.getY() > drawRegionSize) return;

            //if we're drawing, clear current drawing, store our starting x and y mouse positions for later
            if (e.getButton() == MouseEvent.BUTTON1 && mode == 4) {
                drawingLines.clear();
                rotatedLines.clear();
                waveLines.clear();
                currentlyDrawing = true;
            }

            originalX = e.getX();
            originalY = e.getY();
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //handle left clicking in drawing mode
            if (e.getButton() == MouseEvent.BUTTON1 && mode == 4) {
                //finish off the drawing with a line back to the starting point from the current one
                if (e.getX() < drawRegionSize)
                    drawingLines.add(new Line2D.Double(originalX, originalY, e.getX(), e.getY()));
                currentlyDrawing = false;
            }

            if (e.getButton() == 3 && rotationSlider.getValue() != 0) {
                //copy rotatedLines into drawingLines, since this is our "new" drawing to be rotated
                drawingLines.clear();
                for (Line2D.Double line : rotatedLines) {
                    drawingLines.add(new Line2D.Double(line.x1, line.y1, line.x2, line.y2));
                }
                System.out.println("zing");
            }

            rotationSlider.setValue(0);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        public double getLineSlope(Line2D.Double line) {
            return (line.getY2()-line.getY1()) / (line.getX2()-line.getX1());
        }
    }
}
