package afterglow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class GlowPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Mat current;
	private ArrayList<Filter> filters;

	public GlowPanel() throws IOException {
		super();
		
		filters = new ArrayList<Filter>();
		filters.add(new MirrorFilter());
	}

	private boolean matToBufferedImage(Mat matrix) {
		MatOfByte mb = new MatOfByte();
		Highgui.imencode(".jpg", matrix, mb);
		try {
			this.image = ImageIO.read(new ByteArrayInputStream(mb.toArray()));
		} catch (IOException e) {
			e.printStackTrace();
			return false; // Error
		}
		return true; // Successful
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null)
			return;
		int height = this.getParent().getHeight();
		int width = this.getParent().getWidth();
		
		if (height > width * this.image.getHeight() / this.image.getWidth())
			height = width * this.image.getHeight() / this.image.getWidth();
		else
			width = height * this.image.getWidth() / this.image.getHeight();
		
		g.drawImage(this.image, (this.getParent().getWidth() - width) / 2,
				(this.getParent().getHeight() - height) / 2, width, height,
				null);
		
		this.setBackground(Color.BLACK);
	}

	@SuppressWarnings("unchecked")
	public void update(Mat webcam_image) {
		Mat old = current;
		if (current == null)
			old = webcam_image;
		current = webcam_image;
		
		for (Filter filter : (ArrayList<Filter>) filters.clone())
			current = filter.process(old, current);
		this.matToBufferedImage(current);
		this.repaint();
	}
	
	public void run() {
		
		// open and read from the video stream
		Mat webcam_image = new Mat();
		VideoCapture webCam = new VideoCapture(0);

		if (webCam.isOpened()) {
			while (true) {
				webCam.read(webcam_image);
				if (!webcam_image.empty()) {
					update(webcam_image);
				} else {
					System.out.println(" --(!) No captured frame from webcam !");
					break;
				}
			}
		}
		webCam.release(); // release the webcam
	}
	
	public void setFilters(ArrayList<Filter> fs) {
		filters = fs;
		filters.add(0, new MirrorFilter());
	}
	
	public void addFilter(int index, Filter filter) {
		filters.add(index + 1, filter);
	}
	
	public void removeFilter(int index) {
		filters.remove(index + 1);
	}

}