package afterglow;

import org.opencv.core.Mat;

public abstract class Filter {
	
	private Filter filter;
	
	public Filter(){}
	
	public Filter(Filter filter){
		this.filter = filter;
	}
	
	public void setFilter(Filter f) {
		filter = f;
	}
	
	public void removeFilter() {
		filter = null;
	}
	
	public Mat process(Mat previous, Mat current){
		//cool filter-stuff happening!
		if(filter != null)
			return filter.process(previous, current);
		return current;
	}
	
	public static Filter makeFilter(String filter){
		switch(filter){
			case "Sort":		return new BulkSortFilter();
			case "Fade":		return new FadeFilter();
			case "Halo":		return new HaloFilter();
			case "Invert":		return new InvertFilter();
			case "Mask":		return new MaskFilter();
			case "Mirror":		return new MirrorFilter();
			case "Threshold":	return new ThresholdFilter();
			case "Trace":		return new TraceFilter();
			default: 			return new MirrorFilter();
		}
	}
}
