package afterglow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

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
	private Filter filter;

	public GlowPanel() throws IOException {
		super();
		
		filter = new MirrorFilter();
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

	public void update(Mat webcam_image) {
		if (current == null)
			current = webcam_image;
		current = filter.process(current, webcam_image);
		this.matToBufferedImage(current);
		this.repaint();
	}
	
	public void run() {
		
		// Open and Read from the video stream
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
	
	public void setFilter(Filter f) {
		filter = f;
	}
	
	public void addFilter(Filter f) {
		f.setFilter(filter);
		filter = f;
	}

}