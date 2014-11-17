package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class FadeFilter extends Filter {
	
	public FadeFilter() {}

	public FadeFilter(Filter filter) {
		super(filter);
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat darkened = new Mat();
		Core.multiply(oldFrame, new Scalar(0.95, 0.95, 0.95), darkened);
		
		return super.process(darkened, newFrame);
	}

}
