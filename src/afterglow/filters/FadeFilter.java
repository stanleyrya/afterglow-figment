package afterglow.filters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class FadeFilter extends Filter {

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat darkened = new Mat();
		Core.multiply(newFrame, new Scalar(0.96, 0.96, 0.96), darkened);
		
		return darkened;
	}

}
