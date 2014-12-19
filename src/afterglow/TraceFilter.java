package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class TraceFilter extends Filter {

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat result = new Mat();
		Core.max(oldFrame, newFrame, result);
		
		return result;
	}
}
