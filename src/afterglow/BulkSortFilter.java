package afterglow;

import java.awt.Color;
import java.util.Arrays;

import org.opencv.core.Mat;

public class BulkSortFilter extends Filter {
	
	public BulkSortFilter(Filter filter) {
		super(filter);
	}

	public BulkSortFilter() {
		// TODO Auto-generated constructor stub
	}

	public Mat process(Mat oldFrame, Mat newFrame) {		
		for (int i = 0; i < newFrame.width(); i++) {
			arraySort(newFrame, i);
		}
		return super.process(oldFrame, newFrame);
	}

	// i is amount of column, j is amount of row

	private void arraySort(Mat newFrame, int i) {
		//retrieve data
		Color2[] colors = new Color2[newFrame.height()];
		for (int j = 0; j < newFrame.height(); j++) {
			double[] data = new double[3];
			data = newFrame.get(j, i);
			colors[j]= new Color2((int) data[0], (int) data[1], (int) data[2]);
		}
		
		int start = 0;
		int end = newFrame.height()-1;
		
		//choose starting point
		for(int j=0; j<newFrame.height(); j++){
			if(colors[j].compareTo(colors[start]) > 0) start=j;
		}
		
		//choose ending point
//		for(int j=0; j<newFrame.height(); j++){
//			if(colors[j].compareTo(colors[end]) < 0) end=j;
//		}
		
		if(start>end){
			int temp = start;
			start = end;
			end = temp;
		}
		
		//sort
		Arrays.sort(colors, start, end);

		//replace old data
		Color2 temp;
		for (int j = start; j < end; j++) {
			temp = colors[j];
			double[] data = { temp.getRed(), temp.getGreen(), temp.getBlue() }; // {255,255,0};
//			double[] data = {255,255,0};
			newFrame.put(j, i, data);
		}
	}
}

class Color2 extends Color implements Comparable<Color2> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Color2(int r, int g, int b) {
		super(r, g, b);
	}

	public int compareTo(Color2 o) {
		return (o.getRed() + o.getGreen() + o.getBlue())
				- (this.getRed() + this.getGreen() + this.getBlue());
	}
}

//legacy
/*private void quickSort(Mat newFrame, Mat after, int i) {
	List<Color2> colors = new ArrayList<Color2>(newFrame.height());
	for (int j = 0; j < newFrame.height(); j++) {
		double[] data = new double[3];
		data = newFrame.get(j, i);
		colors.add(new Color2((int) data[0], (int) data[1], (int) data[2]));
	}

	colors = quickSort(colors);

	Color2 temp;
	for (int j = 0; j < newFrame.height(); j++) {
		temp = colors.get(j);
		double[] data = { temp.getRed(), temp.getGreen(), temp.getBlue() }; // {255,255,0};
		after.put(j, i, data);
	}
}*/

// rosetta code
/*public static <E extends Comparable<? super E>> List<E> quickSort(
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

}*/
