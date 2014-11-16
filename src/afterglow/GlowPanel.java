package afterglow;


/*  
 * Captures the camera stream with OpenCV  
 * Search for the faces  
 * Display a circle around the faces using Java  
 */
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class GlowPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Mat current;
	private Filter filter;

	// Create a constructor method
	public GlowPanel(Filter f) {
		super();
		filter = f;
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
		g.drawImage(this.image, 10, 10, this.image.getWidth(), this.image.getHeight(), null);
	}
	
	public void update(Mat webcam_image) {
		
		if (current == null)
			current = webcam_image;
		current = filter.process(current, webcam_image);
		this.matToBufferedImage(current);
		this.repaint();
	}

	public static void main(String arg[]) throws InterruptedException {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// make the JFrame
		JFrame frame = new JFrame("Afterglow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GlowPanel panel = new GlowPanel(new MirrorFilter());
		frame.setSize(400, 400); // give the frame some arbitrary size
		frame.setBackground(Color.BLUE);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);

		// Open and Read from the video stream
		Mat webcam_image = new Mat();
		VideoCapture webCam = new VideoCapture(0);

		if (webCam.isOpened()) {
			Thread.sleep(100); // This one-time delay allows the Webcam to initialize itself
			while (true) {
				webCam.read(webcam_image);
				if (!webcam_image.empty()) {
					frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
					
					panel.update(webcam_image);
					Thread.sleep(10); // / This delay eases the computational load
				} else {
					System.out
							.println(" --(!) No captured frame from webcam !");
					break;
				}
			}
		}
		webCam.release(); // release the webcam

	} // end main


}