package afterglow;

import java.awt.Color;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ControlPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ComponentListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private Font roboto;
	private BufferedImage logo;
	private BufferedImage background;
	private GlowPanel canvas;
	private Timer clock = new Timer(10000, this); // timer - every 10 seconds

	// boxes
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

	// dynamic
	RectangleButton selected;
	ArrayList<RectangleButton> appliedFilters;

	// screen areas
	Rectangle2D dropBox;
	Rectangle2D buttonBox;

	public ControlPanel() throws FontFormatException, IOException {
		super();
		setBackground(Color.black); // initial background color
		addMouseListener(this); // add the listener for mouse movement
		addMouseMotionListener(this);
		addComponentListener(this);
		addKeyListener(this);
		init();
	}
	
	public void start() throws FontFormatException, IOException {
		initBoxes();
		this.requestFocusInWindow();
	}

	private void init() throws FontFormatException, IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		roboto = Font.createFont(Font.PLAIN, new File("assets/fonts/Roboto-Thin.ttf")).deriveFont(40f);
		ge.registerFont(roboto);
		logo = ImageIO.read(new File("logo.png"));
		background = ImageIO.read(new File("assets/transparent.png"));
		clock.start();
		appliedFilters = new ArrayList<RectangleButton>();
	}

	// used to dynamically choose positions, sizes, etc...
	private void initBoxes() {
		time = new Rectangle2D.Double(50, 50, 100, 100);

		dropBox = new Rectangle2D.Double(this.getWidth() * 3/4, 0, this.getWidth() * 1/4, this.getHeight());
		buttonBox = new Rectangle2D.Double(0, 225, this.getWidth() * 3/4, this.getHeight());

		bulkSort = createButtonRectangle(0, "Sort", Color.blue);
		fade = createButtonRectangle(1, "Fade", Color.cyan);
		halo = createButtonRectangle(2, "Halo", Color.green);
		invert = createButtonRectangle(3, "Invert", Color.magenta);
		mask = createButtonRectangle(4, "Mask", Color.red);
		threshold = createButtonRectangle(5, "Threshold", Color.orange);
		trace = createButtonRectangle(6, "Trace", Color.blue);
		buttons = new RectangleButton[] { bulkSort, fade, halo, invert, mask, threshold, trace };
	}

	// first index is 0
	private RectangleButton createButtonRectangle(int index, String text, Color color) {
		int buffer = 20;
		int buttonWidth = 200;
		int buttonHeight = 50;
		
		double areaX = buttonBox.getX() + buffer;
		double areaY = buttonBox.getY() + buffer;
		int areaWidth = (int) buttonBox.getWidth() - (buttonWidth + buffer);
		
		// which row
		int yIndex = ((buttonWidth + buffer) * index) / areaWidth;

		// which coloumn
		int xIndex = (((buttonWidth + buffer) * index) % areaWidth) / (buttonWidth + buffer);

		return new RectangleButton(new Rectangle2D.Double(areaX + (buttonWidth + buffer) * xIndex, areaY + (buttonHeight + buffer) * yIndex, buttonWidth, buttonHeight), text, color);
	}

	public void setCanvas(GlowPanel panel) {
		canvas = panel;
	}

	@SuppressWarnings("unchecked")
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // clears the board
		g.setFont(roboto);
		Graphics2D g2 = (Graphics2D) g;
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rectText = null;

		// logo
		g.drawImage(logo, 150, 0, null);

		// screen areas
		g2.setColor(Color.gray);
		g2.fill(dropBox);

		// time
		g2.setColor(Color.orange);
		g2.fill(time);
		String minute = "" + Calendar.getInstance().get(Calendar.MINUTE);
		if (minute.length() == 1) minute = "0" + minute;
		drawText(g, g2, fm, time, rectText, Calendar.getInstance().get(Calendar.HOUR) + ":" + minute);

		// draw each draggable rectangle button
		for (RectangleButton button : buttons) {
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g, g2, fm, button, rectText, button.getText());
		}

		for (RectangleButton button : (ArrayList<RectangleButton>) appliedFilters.clone()) {
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g, g2, fm, button, rectText, button.getText());
		}
		
		// "background"
		g2.drawImage(background, 0, 400, null);

		// draw the selected rectangle if you are dragging one
		if (selected != null) {
			g2.setColor(selected.getColor());
			g2.fill(selected);
			drawText(g, g2, fm, selected, rectText, selected.getText());
		}
	}

	// draws text in the center of a rectangle, could use refactoring with method parameters
	private void drawText(Graphics g, Graphics2D g2, FontMetrics fm, Rectangle2D rect, Rectangle2D rectText, String text) {
		g.setColor(Color.white);
		rectText = fm.getStringBounds(text, g2);
		int x = ((int) rect.getWidth() - (int) rectText.getWidth()) / 2 + (int) rect.getX();
		int y = ((int) rect.getHeight() - (int) rectText.getHeight()) / 2 + fm.getAscent() + (int) rect.getY();
		g.drawString(text, x, y);
	}

	// APPLIED FILTER LIST LOGIC
	// ---------------------------------------------------------

	private void addToApplied(int index, RectangleButton selected) {
		RectangleButton temp = (RectangleButton) selected.clone();
		temp.setRect(createAppliedRectangle(index));
		appliedFilters.add(index, temp);
		canvas.addFilter(index, temp.getFilter());

		// if top filter, index == size right now
		if (index < appliedFilters.size()) {
			redrawAppliedRectangles(index++); // slide the buttons after the added one
		}
	}

	private void removeFromApplied(int index) {
		appliedFilters.remove(index);
		canvas.removeFilter(index);

		// if top filter, index == size right now
		if (index < appliedFilters.size()) {
			redrawAppliedRectangles(index); // slide the buttons after removed one
		}
	}
	
	private void clearApplied() {
		appliedFilters.clear();
		canvas.setFilters(new ArrayList<Filter>());
	}

	// redraw rectangles starting at index
	private void redrawAppliedRectangles(int index) {
		for (RectangleButton button : appliedFilters.subList(index, appliedFilters.size())) {
			button.setRect(createAppliedRectangle(index));
			index++;
		}
	}

	// first index is 0
	private Rectangle2D createAppliedRectangle(int index) {
		double x = dropBox.getX();
		double width = dropBox.getWidth();
		return new Rectangle2D.Double(x + width * 1 / 5, 20 + (20 + 50) * (index), 200, 50);
	}

	private int getAppliedIndex(Point point) {
		int index = (int) (point.getY()) / (20 + 50); // leaves out extra decimals! :)
		if (index >= appliedFilters.size()) {
			return appliedFilters.size();
		} else
			return index;
	}

	// LISTENER LOGIC
	// --------------------------------------------------------------------

	// clock update
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clock) {
			repaint(new Rectangle((int) time.getX(), (int) time.getY(), (int) time.getWidth(), (int) time.getHeight()));
		}
	}

	public void mouseClicked(MouseEvent e) {
		for (RectangleButton button : buttons) {
			if (button.contains(e.getPoint())) {
				addToApplied(appliedFilters.size(), button);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void mouseDragged(MouseEvent e) {
		Point point = e.getPoint();
		double x = point.getX();
		double y = point.getY();

		if (selected != null) {
			selected.setRect(x - selected.getWidth() / 2,
					y - selected.getHeight() / 2, selected.getWidth(), selected.getHeight());
		} else {
			for (RectangleButton button : buttons) {
				if (button.contains(point)) {
					selected = new RectangleButton(button);
				}
			}

			for (RectangleButton button :  (ArrayList<RectangleButton>) appliedFilters.clone()) {
				if (button.contains(point)) {
					selected = new RectangleButton(button);
					removeFromApplied(getAppliedIndex(point));
					break;
				}
			}
		}
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		// drag & dropped
		if (selected != null) {
			if (dropBox.contains(e.getPoint())) {
				addToApplied(getAppliedIndex(e.getPoint()), selected);
			}
			selected = null;
		}
		repaint();
	}

	// dynamically change view
	public void componentResized(ComponentEvent e) {
		initBoxes();
		redrawAppliedRectangles(0);
		repaint();
	}
	
	//for use with wii remote after key mapping
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()){
		case 'a':
			break;
		case 'b':
			break;
		case '1':
			break;
		case '2':
			break;
		case '-':
			removeFromApplied(appliedFilters.size()-1);
			break;
		case '+':
			break;
		case 'h':
			clearApplied();
			break;
		}
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
}

class RectangleButton extends Rectangle2D.Double {

	private static final long serialVersionUID = 1L;
	private String text;
	private Color color;
	private Filter filter;

	public RectangleButton(double x, double y, double width, double height,
			String text, Color color) {
		super(x, y, width, height);
		this.text = text;
		this.color = color;
		this.filter = Filter.makeFilter(text);
	}

	public RectangleButton(Rectangle2D rect, String text, Color color) {
		super(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
		this.text = text;
		this.color = color;
		this.filter = Filter.makeFilter(text);
	}

	public RectangleButton(RectangleButton button) {
		super(button.getX(), button.getY(), button.getWidth(), button.getHeight());
		this.text = button.getText();
		this.color = button.getColor();
		this.filter = Filter.makeFilter(text);
	}

	public String getText() {
		return text;
	}

	public Color getColor() {
		return color;
	}

	public Filter getFilter() {
		return filter;
	}
}