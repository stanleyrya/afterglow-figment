package afterglow.filters;

import org.opencv.core.Mat;

public abstract class Filter {
	
	public Mat process(Mat previous, Mat current){
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
			case "Symbol":		return new SymbolFilter();
			default: 			return new MirrorFilter();
		}
	}
}
