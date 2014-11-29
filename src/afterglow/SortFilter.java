package afterglow;

import java.awt.Color;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;

public class SortFilter extends Filter {
	public Mat process(Mat oldFrame, Mat newFrame) {
		newFrame.convertTo(newFrame, CvType.CV_64F);
		Mat after = new Mat(newFrame.size(), CvType.CV_64F);
		//Core.sort(newFrame, after, Core.SORT_EVERY_COLUMN + Core.SORT_ASCENDING);
		for(int i=0; i<newFrame.width(); i++){
			//sort(new Mat(newFrame, new Range(0,newFrame.rows()), new Range(i,i)),after,i);
			sort(newFrame,after,i);
		}
		return super.process(oldFrame, after);
	}
	//i is amount of column, j is amount of row

	private void sort(Mat newFrame, Mat after, int i) {
		Color[] colors = new Color[newFrame.height()];
		for(int j=0; j<colors.length; j++){
			double[] data = new double[3];
			newFrame.get(j, i, data);
			colors[j] = new Color((int)data[0], (int)data[1], (int)data[2]);
		}
		
		actuallySort(colors);
		
		for(int j=0; j<colors.length; j++){
			double[] data = {colors[j].getRed(), colors[j].getGreen(), colors[j].getBlue()};
			after.put(j, i, data);
		}
	}

	private void actuallySort(Color[] color) {
	}
}
