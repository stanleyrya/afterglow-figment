package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class MirrorFilter implements Filter {

	@Override
	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat after = new Mat();
		Core.flip(newFrame, after, 1);
		return after;
	}

}
