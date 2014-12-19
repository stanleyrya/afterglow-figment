package afterglow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ControlPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ComponentListener{
	private static final long serialVersionUID = 1L;
	private Font roboto;
	private BufferedImage logo;
	private BufferedImage background;
	private GlowPanel canvas;
	private Timer clock = new Timer(10000,this); //timer - every 10 seconds
	
	//boxes
	Rectangle2D time;
	RectangleButton bulkSort;
	RectangleButton fade;
	RectangleButton halo;
	RectangleButton invert;
	RectangleButton mask;
	RectangleButton mirror;
	RectangleButton threshold;
	RectangleButton trace;
	RectangleButton[] buttons;
	
	//dynamic
	RectangleButton selected;
	List<RectangleButton> appliedFilters;
	
	//screen areas
	Rectangle2D dropBox;

	public ControlPanel() throws FontFormatException, IOException {
		super();
		setPreferredSize(new Dimension(500,500)); //size of program
    	setBackground(Color.black); //initial background color
    	addMouseListener(this); //add the listener for mouse movement
    	addMouseMotionListener(this);
    	addComponentListener(this);
		
		init();
		initBoxes();
	}
	
	private void init() throws FontFormatException, IOException{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		roboto = Font.createFont(Font.PLAIN, new File("assets/fonts/Roboto-Thin.ttf")).deriveFont(40f);
		ge.registerFont(roboto);
		logo = ImageIO.read(new File("logo.png"));
		background = ImageIO.read(new File("assets/transparent.png"));
		clock.start();
		appliedFilters = new ArrayList<RectangleButton>();
	}
	
	//used to dynamically choose positions, sizes, etc...
	private void initBoxes(){	
		time = new Rectangle2D.Double(50, 50, 100, 100);
		
		bulkSort = new RectangleButton(50, 250, 100, 100,"Sort",Color.blue);
		fade = new RectangleButton(200, 250, 100, 100,"Fade", Color.cyan);
		halo = new RectangleButton(350, 250, 100, 100,"Halo", Color.green);
		invert = new RectangleButton(500, 250, 100, 100,"Invert", Color.magenta);
		mask = new RectangleButton(650, 250, 100, 100,"Mask", Color.red);
		mirror = new RectangleButton(800, 250, 100, 100,"Mirror", Color.pink);
		threshold = new RectangleButton(950, 250, 100, 100,"Threshold", Color.orange);
		trace = new RectangleButton(1100, 250, 100, 100,"Trace", Color.blue);
		buttons = new RectangleButton[] {bulkSort, fade, halo, invert, mask, mirror, threshold, trace};
		
		dropBox = new Rectangle2D.Double(this.getWidth()*3/4, 0, this.getWidth()*1/4, this.getHeight());
	}
	
	public void setCanvas(GlowPanel frame) {
		canvas = frame;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g); //clears the board
		g.setFont(roboto);
		Graphics2D g2 = (Graphics2D) g;
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rectText = null;
		
		//screen areas
		g2.setColor(Color.orange);
		g2.fill(dropBox);
		
		//logo
		g.drawImage(logo, 150, 0, null);
		
		//time
		g2.setColor(Color.orange);
		g2.fill(time);
		String minute = "" + Calendar.getInstance().get(Calendar.MINUTE);
		if(minute.length() == 1) minute = "0" + minute;
		drawText(g, g2, fm, time, rectText, Calendar.getInstance().get(Calendar.HOUR) + ":" + minute);
		
		//draw each draggable rectangle button
		for(RectangleButton button : buttons){
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g, g2, fm, button, rectText, button.getText());
		}
		
		for(RectangleButton button : appliedFilters){
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g, g2, fm, button, rectText, button.getText());
		}
		
		//draw the selected rectangle if you are dragging one
		if(selected != null){
			g2.setColor(selected.getColor());
			g2.fill(selected);
			drawText(g, g2, fm, selected, rectText, selected.getText());
		}
		
		//"background"
		g2.drawImage(background, 0, 400, null);
	}

	//draws text in the center of a rectangle, could use refactoring with method parameters
	private void drawText(Graphics g, Graphics2D g2, FontMetrics fm, Rectangle2D rect, Rectangle2D rectText, String text) {
		g.setColor(Color.white);
		rectText = fm.getStringBounds(text, g2);
        int x = ((int) rect.getWidth() - (int) rectText.getWidth()) / 2 + (int) rect.getX();
        int y = ((int) rect.getHeight() - (int) rectText.getHeight()) / 2 + fm.getAscent() + (int) rect.getY();
        g.drawString(text, x, y);
	}
	
	private void addToApplied(int index, RectangleButton selected){
		RectangleButton temp = (RectangleButton) selected.clone();
		temp.setRect(createAppliedRectangle(index));
		appliedFilters.add(index, temp);
		
		//if top filter, index == size right now
		if(index < appliedFilters.size()){
			index++; //slide the buttons after the added one
			for(RectangleButton button : appliedFilters.subList(index, appliedFilters.size())){
				button.setRect(createAppliedRectangle(index));
				index++;
			}
		}
	}
	
	//first index is 0
	private Rectangle2D createAppliedRectangle(int index){
		double x = dropBox.getX();
		double width = dropBox.getWidth();
		return new Rectangle2D.Double(x + width*1/5, 20 + (20 + 50)*(index), width*3/5, 50);
	}
	
	private int getAppliedIndex(Point point){
		int index = (int) (point.getY())/(20+50); //leaves out extra decimals! :)
		if(index >= appliedFilters.size()){
			return appliedFilters.size();
		}
		else return index;
	}
	
	//clock update
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clock){
			repaint(new Rectangle((int)time.getX(),(int)time.getY(),(int)time.getWidth(),(int)time.getHeight()));
		}
	}

	public void mouseDragged(MouseEvent e) {
		Point point = e.getPoint();
		double x = point.getX();
		double y = point.getY();
		
		if(selected != null){
			selected.setRect(x-selected.getWidth()/2, y-selected.getHeight()/2, selected.getWidth(), selected.getHeight());
		}
		else{
			for(RectangleButton button : buttons){
				if(button.contains(point)){
					selected = new RectangleButton(button);
				}
			}
		}
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		//drag & dropped
		if(selected != null){
			if(dropBox.contains(e.getPoint())){
				addToApplied(getAppliedIndex(e.getPoint()), selected);
				canvas.setFilter(selected.getFilter());
			}
			selected = null;
		}
		repaint();
	}

	//dynamically change view
	public void componentResized(ComponentEvent e) {
		initBoxes();
		repaint();
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
}

class RectangleButton extends Rectangle2D.Double{

	private static final long serialVersionUID = 1L;
	private String text;
	private Color color;
	
	public RectangleButton(double x, double y, double width, double height, String text, Color color){
		super(x,y,width,height);
		this.text = text;
		this.color = color;
	}
	
	public RectangleButton(RectangleButton button){
		super(button.getX(),button.getY(),button.getWidth(),button.getHeight());
		this.text = button.getText();
		this.color = button.getColor();
	}
	
	public String getText(){ return text; }
	public Color getColor(){ return color; }
	public Filter getFilter(){ return Filter.makeFilter(text); }
}