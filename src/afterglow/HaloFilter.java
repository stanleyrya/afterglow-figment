package afterglow;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class HaloFilter extends Filter{
	
	public HaloFilter() {}

	public HaloFilter(Filter filter) {
		super(filter);
	}

	public Mat process(Mat oldFrame, Mat newFrame) {

		ArrayList<Mat> channels = new ArrayList<Mat>();
		Core.split(newFrame, channels);
		for (Mat channel : channels) {
			Imgproc.threshold(channel, channel, 190, 255, Imgproc.THRESH_TOZERO_INV);
			Imgproc.threshold(channel, channel, 120, 255, Imgproc.THRESH_BINARY);
		}
		Mat merged = new Mat();
		Core.merge(channels, merged);
		
		return super.process(oldFrame, merged);
	}

}