package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class TraceFilter extends Filter {

	public TraceFilter(Filter filter) {
		super(filter);
	}
	
	public TraceFilter() {
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat darkened = new Mat();
		Core.multiply(oldFrame, new Scalar(0.95, 0.95, 0.95), darkened);
		Mat result = new Mat();
		Core.max(darkened, newFrame, result);
		
		return super.process(oldFrame,result);
	}

}
