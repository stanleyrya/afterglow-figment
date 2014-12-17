package afterglow;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SortFilter extends Filter {
	
	public SortFilter(Filter filter) {
		super(filter);
	}

	public SortFilter() {
		// TODO Auto-generated constructor stub
	}

	public Mat process(Mat oldFrame, Mat newFrame) {
		Mat greyscale = new Mat();
		Imgproc.cvtColor(newFrame, greyscale, Imgproc.COLOR_RGB2GRAY); //use some other conversion for other sorting
		
		// indices used for sorting since sort function doesn't allow multichannel arrays
		Mat sortedIndices = new Mat();
		Core.sortIdx(greyscale, sortedIndices, Core.SORT_EVERY_COLUMN + Core.SORT_DESCENDING); 
		
		// copy over values from newFrame using indices of sorted elements
		Mat after = new Mat(newFrame.size(), newFrame.type());
		for (int i = 0; i < newFrame.width(); i++)
			for (int j = 0; j < newFrame.height(); j++) {
				int[] index = new int[1];
				sortedIndices.get(j, i, index);
				after.put(j, i, newFrame.get(index[0], i));
			}

		return super.process(oldFrame, after);
	}
}
