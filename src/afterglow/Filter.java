package afterglow;

import org.opencv.core.Mat;

public abstract class Filter {
	
	private Filter filter;
	
	public Filter(){
	}
	
	public Filter(Filter filter){
		this.filter = filter;
	}
	
	public Mat process(Mat previous, Mat current){
		//cool filter-stuff happening!
		if(filter != null)
			return filter.process(previous, current);
		return current;
	}
}
