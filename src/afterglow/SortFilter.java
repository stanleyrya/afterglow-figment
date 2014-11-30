package afterglow;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SortFilter extends Filter {
	
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

//		for (int i = 0; i < newFrame.width(); i++) {
//			sort(newFrame, after, i);
//		}

		return super.process(oldFrame, after);
	}

	// i is amount of column, j is amount of row

	// private void sort(Mat newFrame, Mat after, int i) {
	// Color[] colors = new Color[newFrame.height()];
	// for(int j=0; j<colors.length; j++){
	// double[] data = new double[3];
	// newFrame.get(j, i, data);
	// colors[j] = new Color((int)data[0], (int)data[1], (int)data[2]);
	// }
	//
	// //quickSort(colors);
	//
	// for(int j=0; j<colors.length; j++){
	// double[] data = {colors[j].getRed(), colors[j].getGreen(),
	// colors[j].getBlue()};
	// after.put(j, i, data);
	// }
	// }

	private void sort(Mat newFrame, Mat after, int i) {
		Color2[] colors = new Color2[newFrame.height()];
		for (int j = 0; j < newFrame.height(); j++) {
			double[] data = new double[3];
			newFrame.get(j, i, data);
			colors[j]= new Color2((int) data[0], (int) data[1], (int) data[2]);
		}
		Arrays.sort(colors);

		Color2 temp;
		for (int j = 0; j < newFrame.height(); j++) {
			temp = colors[j];
			double[] data = { temp.getRed(), temp.getGreen(), temp.getBlue() }; // {255,255,0};
			after.put(j, i, data);
		}
	}

	// rosetta code
	public static <E extends Comparable<? super E>> List<E> quickSort(
			List<E> arr) {
		if (!arr.isEmpty()) {
			E pivot = arr.get(0); // This pivot can change to get faster results

			List<E> less = new LinkedList<E>();
			List<E> pivotList = new LinkedList<E>();
			List<E> more = new LinkedList<E>();

			// Partition
			for (E i : arr) {
				if (i.compareTo(pivot) < 0)
					less.add(i);
				else if (i.compareTo(pivot) > 0)
					more.add(i);
				else
					pivotList.add(i);
			}

			// Recursively sort sublists
			less = quickSort(less);
			more = quickSort(more);

			// Concatenate results
			less.addAll(pivotList);
			less.addAll(more);
			return less;
		}
		return arr;

	}
}

class Color2 extends Color implements Comparable<Color2> {

	public Color2(int r, int g, int b) {
		super(r, g, b);
	}

	public int compareTo(Color2 o) {
		return (o.getRed() + o.getGreen() + o.getBlue())
				- (this.getRed() + this.getGreen() + this.getBlue());
	}
}
