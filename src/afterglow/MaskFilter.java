package afterglow;

import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MaskFilter extends Filter {
	
	public MaskFilter() {}

	public MaskFilter(Filter filter) {
		super(filter);
	}

	public Mat process(Mat oldFrame, Mat newFrame) {

		ArrayList<Mat> channels = new ArrayList<Mat>();
		Core.split(newFrame, channels);
		for (Mat channel : channels)
			Imgproc.threshold(channel, channel, 150, 255, Imgproc.THRESH_TOZERO);
		Mat merged = new Mat();
		Core.merge(channels, merged);
		
		return super.process(oldFrame, merged);
	}

}