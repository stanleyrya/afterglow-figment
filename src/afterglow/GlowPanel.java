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
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class GlowPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Mat current;

	// Create a constructor method
	public GlowPanel() {
		super();
	}

	/*
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix Mat of type CV_8UC3 or CV_8UC1
	 * 
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public boolean matToBufferedImage(Mat matrix) {
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

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image == null)
			return;
		g.drawImage(this.image, 10, 10, this.image.getWidth(),
				this.image.getHeight(), null);
	}

	public static Mat process(Mat frame) {
		Mat frameBlur = new Mat();
		Imgproc.blur(frame, frameBlur, new Size(5, 5));
		return frameBlur;
		
	}

	public static void main(String arg[]) throws InterruptedException {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// make the JFrame
		JFrame frame = new JFrame("WebCam Capture - Face detection");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GlowPanel panel = new GlowPanel();
		frame.setSize(400, 400); // give the frame some arbitrary size
		frame.setBackground(Color.BLUE);
		frame.add(panel, BorderLayout.CENTER);
		frame.setVisible(true);

		// Open and Read from the video stream
		Mat webcam_image = new Mat();
		VideoCapture webCam = new VideoCapture(0);

		if (webCam.isOpened()) {
			Thread.sleep(100); // / This one-time delay allows the Webcam to
								// initialize itself
			while (true) {
				webCam.read(webcam_image);
				if (!webcam_image.empty()) {
					frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
					webcam_image = process(webcam_image);
					// Display the image
					panel.matToBufferedImage(webcam_image);
					panel.repaint();
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