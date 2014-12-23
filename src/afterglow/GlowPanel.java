package afterglow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import afterglow.filters.Filter;
import afterglow.filters.MirrorFilter;

public class GlowPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Mat current;
	private Mat old;
	private ArrayList<Filter> filters;
	private IMediaWriter videoWriter;
	private long startTime;
	boolean recording = false;

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

	public void update(Mat webcam_image) {
		old = current;
		if (current == null)
			old = webcam_image;
		current = webcam_image;
		
		Filter[] a = new Filter[filters.size()];
		for (Filter filter : filters.toArray(a))
			current = filter.process(old, current);
		this.matToBufferedImage(current);
		this.repaint();

		if (recording)
			videoWriter.encodeVideo(0, image, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
	}

	public void run() {

		// open and read from the video stream
		Mat webcam_image = new Mat();
		VideoCapture webCam = new VideoCapture(0);
//		webCam.set(3, 1024.0); //small size in case of lag
//		webCam.set(4, 576.0);
		webCam.set(3, 1280.0);
		webCam.set(4, 720.0);

		if (webCam.isOpened()) {
			while (true) {
				webCam.read(webcam_image);
				if (!webcam_image.empty()) {
					update(webcam_image);
				} else {
					System.out
							.println(" --(!) No captured frame from webcam !");
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
	
	public void screenshot() {
		int imageCount = 0;
		File dir = new File("../images");
		dir.mkdirs();
		Pattern pattern = Pattern.compile("^afterglow(\\d+)\\.jpg$");
		for (String file : dir.list()) {
			Matcher matcher = pattern.matcher(file);
		    if (matcher.find())
		    	imageCount = Math.max(imageCount, Integer.parseInt(matcher.group(1)));
		}
		
		imageCount++;
		Highgui.imwrite("../images/afterglow" + imageCount + ".jpg", old, new MatOfInt(Highgui.CV_IMWRITE_JPEG_QUALITY, 85));
	}
	
	public void startRecording() {
		int videoCount = 0;
		File dir = new File("../videos");
		dir.mkdirs();
		Pattern pattern = Pattern.compile("^afterglow(\\d+)\\.mp4$");
		for (String file : dir.list()) {
			Matcher matcher = pattern.matcher(file);
		    if (matcher.find())
		    	videoCount = Math.max(videoCount, Integer.parseInt(matcher.group(1)));
		}
		
		videoCount++;
		videoWriter = ToolFactory.makeWriter("../videos/afterglow" + videoCount + ".mp4");
		videoWriter.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, current.width()/2, current.height()/2);
		startTime = System.nanoTime();
		recording = true;
	}
	
	public void stopRecording() {
		if (recording) {
			recording = false;
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			videoWriter.close();
		}
	}
}