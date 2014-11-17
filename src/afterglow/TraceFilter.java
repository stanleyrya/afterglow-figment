package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class TraceFilter extends Filter {
	
	public TraceFilter() {}

	public TraceFilter(Filter filter) {
		super(filter);
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat result = new Mat();
		Core.max(oldFrame, newFrame, result);
		
		return super.process(oldFrame, result);
	}

}
