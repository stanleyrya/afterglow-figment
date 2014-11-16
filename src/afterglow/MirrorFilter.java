package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class MirrorFilter extends Filter {
	
	public MirrorFilter(Filter filter) {
		super(filter);
	}

	public MirrorFilter() {
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat after = new Mat();
		Core.flip(newFrame, after, 1);
		return super.process(oldFrame, after);
	}

}
