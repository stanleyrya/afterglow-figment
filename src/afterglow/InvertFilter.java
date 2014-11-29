package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class InvertFilter extends Filter {
	
	public InvertFilter() {}

	public InvertFilter(Filter filter) {
		super(filter);
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat after = new Mat();//newFrame.size(), CvType.CV_64F);
		Mat invertcolormatrix= new Mat(newFrame.rows(),newFrame.cols(), newFrame.type(), new Scalar(255,255,255));
		Core.subtract(invertcolormatrix, newFrame, after);
		return super.process(oldFrame, after);
	}

}
