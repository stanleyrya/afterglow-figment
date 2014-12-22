package afterglow;

import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ThresholdFilter extends Filter {

	public Mat process(Mat oldFrame, Mat newFrame) {

		Mat mask = new Mat();
		Imgproc.cvtColor(newFrame, mask, Imgproc.COLOR_RGB2GRAY);
		Core.divide(mask, new Scalar(255, 255, 255), mask);
		Mat newMask = new Mat();
		Imgproc.threshold(mask, newMask, .9, 1, Imgproc.THRESH_BINARY);
		Mat mergedNewMask = new Mat();
		Core.merge(Arrays.asList(newMask, newMask, newMask), mergedNewMask);
		Mat maskedNew = new Mat();
		Core.multiply(newFrame, mergedNewMask, maskedNew);

		Mat oldMask = new Mat();
		Imgproc.threshold(mask, oldMask, .1, 1, Imgproc.THRESH_BINARY_INV);
		Mat mergedOldMask = new Mat();
		Core.merge(Arrays.asList(oldMask, oldMask, oldMask), mergedOldMask);
		Mat maskedOld = new Mat();
		Core.multiply(oldFrame, mergedOldMask, maskedOld);
		
		Mat result = new Mat();
		Core.add(maskedOld, maskedNew, result);
		
		return result;
	}
}