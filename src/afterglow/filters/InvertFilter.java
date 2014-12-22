package afterglow.filters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class InvertFilter extends Filter {

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat after = new Mat();//newFrame.size(), CvType.CV_64F);
		Mat invertcolormatrix= new Mat(newFrame.rows(),newFrame.cols(), newFrame.type(), new Scalar(255,255,255));
		Core.subtract(invertcolormatrix, newFrame, after);
		return after;
	}

}
