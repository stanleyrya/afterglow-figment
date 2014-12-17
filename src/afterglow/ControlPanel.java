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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ControlPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 1L;
	private Font roboto;
	private BufferedImage logo;
	private GlowPanel canvas;
	
	Rectangle2D time;
	RectangleButton bulkSort;
	RectangleButton fade;
	RectangleButton halo;
	RectangleButton invert;
	RectangleButton mask;
	RectangleButton mirror;
	RectangleButton sort;
	RectangleButton threshold;
	RectangleButton trace;
	RectangleButton[] buttons;
	
	RectangleButton selected;
	RectangleButton[] appliedFilters;
	

	public ControlPanel() throws FontFormatException, IOException {
		super();
		setPreferredSize(new Dimension(500,500)); //size of program
    	setBackground(Color.black); //initial background color
    	addMouseListener(this); //add the listener for mouse movement
    	addMouseMotionListener(this);
		init();
	}
	
	private void init() throws FontFormatException, IOException{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		roboto = Font.createFont(Font.PLAIN, new File("assets/fonts/Roboto-Thin.ttf")).deriveFont(40f);
		ge.registerFont(roboto);
		logo = ImageIO.read(new File("logo.png"));
		
		time = new Rectangle2D.Double(50, 50, 100, 100);
		
		bulkSort = new RectangleButton(50, 250, 100, 100,"Sort",Color.blue);
		fade = new RectangleButton(200, 250, 100, 100,"Fade", Color.cyan);
		halo = new RectangleButton(350, 250, 100, 100,"Halo", Color.green);
		invert = new RectangleButton(500, 250, 100, 100,"Invert", Color.magenta);
		mask = new RectangleButton(650, 250, 100, 100,"Mask", Color.red);
		mirror = new RectangleButton(800, 250, 100, 100,"Mirror", Color.pink);
		sort = new RectangleButton(950, 250, 100, 100,"Sort", Color.YELLOW);
		threshold = new RectangleButton(1100, 250, 100, 100,"Threshold", Color.orange);
		trace = new RectangleButton(1250, 250, 100, 100,"Trace", Color.blue);
		
		buttons = new RectangleButton[] {bulkSort, fade, halo, invert, mask, mirror, sort, threshold, trace};
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g); //clears the board
		g.setFont(roboto);
		Graphics2D g2 = (Graphics2D) g;
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rectText = null;
		
		//logo
		g.drawImage(logo, 150, 0, null);
		
		//time
		g2.setColor(Color.orange);
		g2.fill(time);
		drawText(g, g2, fm, time, rectText, Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE));
		
		//draw each draggable rectangle button
		for(RectangleButton button : buttons){
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
	}

	//draws text in the center of a rectangle, could use refactoring with method parameters
	private void drawText(Graphics g, Graphics2D g2, FontMetrics fm, Rectangle2D rect, Rectangle2D rectText, String text) {
		g.setColor(Color.white);
		rectText = fm.getStringBounds(text, g2);
        int x = ((int) rect.getWidth() - (int) rectText.getWidth()) / 2 + (int) rect.getX();
        int y = ((int) rect.getHeight() - (int) rectText.getHeight()) / 2 + fm.getAscent() + (int) rect.getY();
        g.drawString(text, x, y);
	}

	public void mouseClicked(MouseEvent e) {
		if(bulkSort.contains(e.getX(),e.getY())){
			canvas.setFilter(new BulkSortFilter());
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		if(selected != null) selected = null;
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void setCanvas(GlowPanel frame) {
		canvas = frame;
	}

	public void mouseDragged(MouseEvent e) {
		Point point = e.getPoint();
		double x = point.getX();
		double y = point.getY();
		
		if(selected != null && selected.contains(point)){
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

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

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
}