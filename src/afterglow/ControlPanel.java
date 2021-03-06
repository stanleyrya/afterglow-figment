package afterglow;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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

import afterglow.filters.Filter;

public class ControlPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ComponentListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private Font roboto;
	private BufferedImage logo;
	private BufferedImage background;
	private BufferedImage downloadIcon;
	private GlowPanel canvas;
	private Timer clock = new Timer(10000, this); // timer - every 10 seconds
	private Timer render = new Timer(1000/60, this); // for rendering - 60 fps
	private Timer downloader = new Timer(200, this); // for download action - .2 seconds
	private Timer blinker = new Timer(500, this); // for blinking record button - every half second
	double recordCircleSize = .75;
	private double recordButtonSize = .5;
	private int speedDial = 0;
	private boolean downloaded = false;
	private boolean recording = false;
	private boolean blink = false;

	// boxes
	Rectangle2D time;
	Rectangle2D download;
	Rectangle2D record;
	RectangleButton bulkSort;
	RectangleButton fade;
	RectangleButton halo;
	RectangleButton invert;
	RectangleButton mask;
	RectangleButton threshold;
	RectangleButton trace;
	RectangleButton symbol;
	ArrayList<RectangleButton> buttons;
	
	private int buffer = 20;
	private int buttonWidth = 200;
	private int buttonHeight = 50;

	// dynamic
	RectangleButton selected;
	ArrayList<RectangleButton> appliedFilters;
	ArrayList<RectangleButton> highlightedList;
	int highlightedIndex;

	// screen areas
	Rectangle2D dashBox;
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
		clock.start();
		this.requestFocusInWindow();
	}
	
	public void setCanvas(GlowPanel panel) {
		canvas = panel;
	}

	private void init() throws FontFormatException, IOException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		roboto = Font.createFont(Font.PLAIN, new File("assets/fonts/Roboto-Light.ttf")).deriveFont(40f);
		ge.registerFont(roboto);
		logo = ImageIO.read(new File("logo.png"));
		background = ImageIO.read(new File("assets/transparent2.png"));
		downloadIcon = ImageIO.read(new File("assets/download.png"));
		appliedFilters = new ArrayList<RectangleButton>();
	}

	// used to dynamically choose positions, sizes, etc...
	private void initBoxes() {
		dashBox = new Rectangle2D.Double(buffer, buffer, 100, 200);
		buttonBox = new Rectangle2D.Double(0, 225, this.getWidth() - (buttonWidth + buffer*2), this.getHeight());
		dropBox = new Rectangle2D.Double(this.getWidth() - (buttonWidth + buffer*2), 0, buttonWidth + buffer*2, this.getHeight());
		
		time = new Rectangle2D.Double(0, dashBox.getY() + 10, dashBox.getWidth() + dashBox.getX(), 50);
		download = new Rectangle2D.Double(dashBox.getCenterX() - 50, time.getY() + time.getHeight() + 10, 100, 50);
		record = new Rectangle2D.Double(dashBox.getCenterX() - 50, download.getY() + download.getHeight() + 10, 100, 50);

		buttons = new ArrayList<RectangleButton>(7);
		
		buttons.add(bulkSort = createButtonRectangle(0, "Sort", Color.blue.darker()));
		buttons.add(fade = createButtonRectangle(1, "Fade", Color.cyan.darker()));
		buttons.add(halo = createButtonRectangle(2, "Halo", Color.green.darker()));
		buttons.add(invert = createButtonRectangle(3, "Invert", Color.magenta.darker()));
		buttons.add(mask = createButtonRectangle(4, "Mask", Color.red.darker()));
		buttons.add(threshold = createButtonRectangle(5, "Threshold", Color.orange.darker()));
		buttons.add(trace = createButtonRectangle(6, "Trace", Color.blue.darker()));
		buttons.add(symbol = createButtonRectangle(7, "Symbol", Color.cyan.darker()));
	}

	// first index is 0
	private RectangleButton createButtonRectangle(int index, String text, Color color) {
		
		double areaX = buttonBox.getX() + buffer;
		double areaY = buttonBox.getY() + buffer;
		int areaWidth = (int) buttonBox.getWidth() - (buttonWidth + buffer);
		
		// which row
		int yIndex = getButtonRectangleYIndex(index);

		// which coloumn
		int xIndex = (((buttonWidth + buffer) * index) % areaWidth) / (buttonWidth + buffer);

		return new RectangleButton(new Rectangle2D.Double(areaX + (buttonWidth + buffer) * xIndex, areaY + (buttonHeight + buffer) * yIndex, buttonWidth, buttonHeight), text, color);
	}
	
	private int getButtonRectangleYIndex(int index){
		int areaWidth = (int) buttonBox.getWidth() - (buttonWidth + buffer);
		return ((buttonWidth + buffer) * index) / areaWidth;
	}

	@SuppressWarnings("unchecked")
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // clears the board
		Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	    		RenderingHints.VALUE_ANTIALIAS_ON);

		// logo and background
		g2.drawImage(logo, (int)dashBox.getWidth() - 20, -10,(int) (logo.getWidth()*.38), (int) (logo.getHeight()*.38), null);
        g2.drawImage(background, 0, getHeight()-350, null);

		// screen areas
		g2.setColor(Color.darkGray);
		Composite c = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		g2.fill(dropBox);
		g2.fillRect(0, (int) download.getY(), (int) download.getWidth(), (int) download.getHeight());
		g2.fillRect(0, (int) record.getY(), (int) record.getWidth(), (int) record.getHeight());
		g2.setComposite(c);

		// time
		String minute = "" + Calendar.getInstance().get(Calendar.MINUTE);
		if (minute.length() == 1) minute = "0" + minute;
		drawText(g2, roboto, Color.white, time, Calendar.getInstance().get(Calendar.HOUR) + ":" + minute);
		
		// download
		if (downloaded)
			g2.drawImage(downloadIcon/*.getScaledInstance(downloadIcon., height, Image.SCALE_FAST)*/, (int) (dashBox.getCenterX() - download.getHeight()*.6), (int) (download.getCenterY() -  download.getHeight()*.6), (int) (download.getHeight() * 1.2), (int) (download.getHeight() * 1.2), null);
		else
			g2.drawImage(downloadIcon, (int) (dashBox.getCenterX() - download.getHeight()/2), (int) download.getY(), (int) download.getHeight(), (int) download.getHeight(), null);

		
		// record
		g2.drawOval((int) (dashBox.getCenterX() - record.getHeight()*recordCircleSize/2), (int)(record.getCenterY() - record.getHeight()*recordCircleSize/2), (int) (record.getHeight()*recordCircleSize), (int) (record.getHeight()*recordCircleSize));
		g2.setColor(Color.red);
		g2.fillOval((int) (dashBox.getCenterX() - record.getHeight()*recordButtonSize/2), (int)(record.getCenterY() - record.getHeight()*recordButtonSize/2), (int) (record.getHeight()*recordButtonSize), (int) (record.getHeight()*recordButtonSize));
		if (blink) {
			drawText(g2, roboto.deriveFont(20f), Color.red, new Rectangle2D.Double(0,record.getY(),(int) (dashBox.getCenterX() - record.getHeight()*recordCircleSize/2), record.getHeight()), "REC");
		}
		
		
		// draw each draggable rectangle button
		//System.out.println(buttons.toString());
		for (RectangleButton button : buttons) {
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g2, roboto, Color.white, button, button.getText());
		}

		for (RectangleButton button : (ArrayList<RectangleButton>) appliedFilters.clone()) {
			g2.setColor(button.getColor());
			g2.fill(button);
			drawText(g2, roboto, Color.white, button, button.getText());
		}

		// draw the selected rectangle if you are dragging one
		if (selected != null) {
			g2.setColor(selected.getColor());
			g2.fill(selected);
			drawText(g2, roboto, Color.white, selected, selected.getText());
		}
		
		if (highlightedList != null && highlightedList.size() > highlightedIndex) {
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(5));
			int buffer = 7;
			RectangleButton highlighted = highlightedList.get(highlightedIndex);
			g2.drawRect((int) highlighted.getX() - buffer, (int) highlighted.getY() - buffer, (int) highlighted.getWidth() + buffer*2, (int) highlighted.getHeight() + buffer*2);
		}
	}

	// draws text in the center of a rectangle, could use refactoring with method parameters
	private void drawText(Graphics2D g2, Font f, Color c, Rectangle2D rect, String text) {
		g2.setFont(f);
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(c);
		Rectangle2D rectText = fm.getStringBounds(text, g2);
		int x = ((int) rect.getWidth() - (int) rectText.getWidth()) / 2 + (int) rect.getX();
		int y = ((int) rect.getHeight() - (int) rectText.getHeight()) / 2 + fm.getAscent() + (int) rect.getY();
		g2.drawString(text, x, y);
	}

	// APPLIED FILTER LIST LOGIC
	// -------------------------------------------------------------------

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
		if(appliedFilters.size() == 0) return;
		appliedFilters.remove(index);
		canvas.removeFilter(index);

		// if top filter, index == size right now
		if (index < appliedFilters.size()) {
			redrawAppliedRectangles(index); // slide the buttons after removed one
		}
		
		//if deleting the highlighted button, move highlight
		if(highlightedList == appliedFilters){
			int size = highlightedList.size();
			if(size == highlightedIndex){
				if(size == 0){
					highlightedList = buttons;
				}
				else highlightedIndex--;
			}
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
		return new Rectangle2D.Double(x + (width - buttonWidth)/2, 20 + (20 + 50) * (index), buttonWidth, buttonHeight);
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
		if (e.getSource() == render) {
			if(recording){
				if(recordButtonSize < recordCircleSize + .04){
					recordButtonSize += .01;
					recordCircleSize -= .01;
				}
				else{
					blinker.start();
					render.stop();
				}
			}
			else{
				blinker.stop();
				blink = false;
				if(recordButtonSize > .5){
					recordButtonSize -= .01;
				}
				if(recordCircleSize < .75){
					recordCircleSize += .01;
				}
				else{
					render.stop();
				}
			}
			repaint(new Rectangle(0, (int) record.getY(), (int) record.getWidth(), (int) record.getHeight()));
		}
		if (e.getSource() == blinker) {
			blink = !blink;
			repaint(new Rectangle(0, (int) record.getY(), (int) record.getWidth(), (int) record.getHeight()));
		}
		if (e.getSource() == downloader) {
			downloader.stop();
			downloaded = false;
			repaint(new Rectangle(0, (int) download.getY(), (int) download.getWidth(), (int) download.getHeight()));
		}
	}

	@SuppressWarnings("unchecked")
	public void mouseClicked(MouseEvent e) {
		Point point = e.getPoint();
		for (RectangleButton button : buttons) {
			if (button.contains(point)) {
				addToApplied(appliedFilters.size(), button);
				break;
			}
		}
		for (RectangleButton button : (ArrayList<RectangleButton>) appliedFilters.clone()) {
			if (button.contains(point)) {
				removeFromApplied(getAppliedIndex(point));
				break;
			}
		}
		if (download.contains(point)) {
			canvas.screenshot();
			downloaded = true;
			repaint(new Rectangle(0, (int) download.getY(), (int) download.getWidth(), (int) download.getHeight()));
			downloader.start();
		}
		else if (record.contains(point)){
			if (!recording) {
				recordButtonSize = .5;
				canvas.startRecording();
			} else
				canvas.stopRecording();
			recording = !recording;
			render.start();
		}
		repaint();
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
			if(highlightedList == buttons){
				addToApplied(appliedFilters.size(), highlightedList.get(highlightedIndex));
			}
			else if(highlightedList == appliedFilters){
				removeFromApplied(highlightedIndex);
			}
			break;
		case 'b':
			canvas.clear();
			break;
		case '1':
			canvas.screenshot();
			downloaded = true;
			repaint(new Rectangle(0, (int) download.getY(), (int) download.getWidth(), (int) download.getHeight()));
			downloader.start();
			break;
		case '2':
			if (!recording) {
				recordButtonSize = .5;
				canvas.startRecording();
			} else
				canvas.stopRecording();
			recording = !recording;
			render.start();
			break;
		case '-':
			removeFromApplied(appliedFilters.size()-1);
			break;
		case '+':
			addToApplied(appliedFilters.size(), buttons.get((int) Math.round(Math.random() * (buttons.size()-1))));
			break;
		case 'h':
			clearApplied();
			addToApplied(0, trace);
			addToApplied(1, fade);
			break;
		}
		repaint();
	}
	
	//traverse between the two lists using the arrow keys.
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch( keyCode ) {
		case KeyEvent.VK_UP:
			speedDial(1);
			break;
		case KeyEvent.VK_LEFT:
			// activate highlight if it isn't already activated
			if (highlightedList == null) {
				highlightedList = buttons;
				highlightedIndex = 0;
				break;
			}

			// traverse list backwards
			highlightedIndex--;
			if (highlightedIndex == -1) {
				if (highlightedList == buttons) {
					if (appliedFilters.size() > 0)
						highlightedList = appliedFilters;
				} else {
					highlightedList = buttons;
				}
				highlightedIndex = highlightedList.size() - 1;
			}
			break;
		case KeyEvent.VK_DOWN:
			speedDial(-1);
			break;
		case KeyEvent.VK_RIGHT :
			if (highlightedList == null) {
				highlightedList = buttons;
				highlightedIndex = 0;
				break;
			}

			// if(getButtonRectangleYIndex(highlightedIndex + 1) != getButtonRectangleYIndex(highlightedIndex)){
			// //going out of buttons
			// }
			highlightedIndex++;
			if (highlightedIndex == highlightedList.size()) {
				if (highlightedList == buttons) {
					if (appliedFilters.size() > 0)
						highlightedList = appliedFilters;
				} else {
					highlightedList = buttons;
				}
				highlightedIndex = 0;
			}
			break;
		}
		
		repaint();
	}
	
	private void speedDial(int dial){
		clearApplied();
		speedDial += dial;
		
		switch(speedDial){
		case -1:
			speedDial = 6;
			speedDial(0);
			break;
		case 0:
			break;
		case 1: // rainbow sort
			addToApplied(0, bulkSort);
			addToApplied(1, halo);
			break;
		case 2: // hollow
			addToApplied(0, invert);
			addToApplied(1, threshold);
			break;
		case 3:
			addToApplied(0, threshold);
			addToApplied(1, invert);
			break;
		case 4: // spectrum-frame
			addToApplied(0, trace);
			addToApplied(1, invert);
			addToApplied(2, halo);
			break;
		case 5: // journey	
			addToApplied(0, trace);
			addToApplied(1, invert);
			addToApplied(2, halo);
			addToApplied(3, trace);
			addToApplied(4, fade);
			break;
		case 6: // prism break
			addToApplied(0, invert);
			addToApplied(1, fade);
			addToApplied(2, trace);
			addToApplied(3, threshold);
			addToApplied(4, invert);
			addToApplied(5, fade);
			break;
		default:
			speedDial = 0;
			speedDial(0);
		}
	}
	
	
	public void mouseMoved(MouseEvent e) {
		highlightedList = null;
		repaint();
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
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