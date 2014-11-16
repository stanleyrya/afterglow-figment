package afterglow;

import org.opencv.core.Mat;

public interface Filter {
	public Mat process(Mat previous, Mat current);
}
