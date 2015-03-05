package afterglow.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SymbolFilter extends Filter {
	
	boolean grayscale = false;

	public Mat process(Mat oldFrame, Mat newFrame) {
		if(grayscale){
			Imgproc.cvtColor(newFrame, newFrame, Imgproc.COLOR_RGB2GRAY);
			Imgproc.cvtColor(newFrame, newFrame, Imgproc.COLOR_GRAY2RGB);
		}
		BufferedImage src = mat2Img(newFrame);
		orderedDither(src,src);
		Mat after = img2Mat(src);
		after.convertTo(after, newFrame.type());
		return after;
	}

	private BufferedImage mat2Img(Mat in){
		int width = in.width();
		int height = in.height();
		BufferedImage out;
		byte[] data = new byte[width * height * (int)in.elemSize()];
		int type;
		in.get(0, 0, data);

		if(in.channels() == 1){
			type = BufferedImage.TYPE_BYTE_GRAY;
		}
		else
			type = BufferedImage.TYPE_3BYTE_BGR;

		out = new BufferedImage(width, height, type);

		out.getRaster().setDataElements(0, 0, width, height, data);
		return out;
	} 

	public static Mat img2Mat(BufferedImage in){
		Mat out;
		byte[] data;
		int r, g, b;
		int height = in.getHeight();
		int width = in.getWidth();
		if(in.getType() == BufferedImage.TYPE_3BYTE_BGR)
		{
			out = new Mat(height, width, CvType.CV_8UC3);
			data = new byte[height * width * (int)out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
			for(int i = 0; i < dataBuff.length; i++)
			{
				data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
				data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
				data[i*3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
			}
		}
		else
		{
			out = new Mat(height, width, CvType.CV_8UC1);
			data = new byte[height * width * (int)out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
			for(int i = 0; i < dataBuff.length; i++)
			{
				r = (byte) ((dataBuff[i] >> 16) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
			}
//			Imgproc.cvtColor(out, out, Imgproc.COLOR_GRAY2RGB);
//			System.out.println(out.channels());
		}
		out.put(0, 0, data);
		return out;
	}

	private void orderedDither(BufferedImage src, BufferedImage dst) {
		final float[][] Bayers = {{15/16.f,  7/16.f,  13/16.f,   5/16.f},
				{3/16.f,  11/16.f,   1/16.f,   9/16.f},
				{12/16.f,  4/16.f,  14/16.f,   6/16.f},
				{ 0,      8/16.f,    2/16.f,  10/16.f} };

		/* === YOUR WORK HERE === */
				int width = src.getWidth();
				int height = src.getHeight();

				int[] pixels = new int[width * height];

				src.getRGB(0, 0, width, height, pixels, 0, width);

				int i;
				int j, k;
				int a, r, g, b;
				float e, L;
				for(i = 0; i < width * height; i++) {
					Color rgb = new Color(pixels[i]);

					a = rgb.getAlpha();
					r = rgb.getRed();
					g = rgb.getGreen();
					b = rgb.getBlue();

					j = (i/4) % width;
					k = (int) ((i/4) / width);
					e = Bayers[j%4][k%4]*255;

					L = (float) ((0.3*r) + (0.59*g) + (0.11*b));

					if(L > e){
						L = 255;
					}
					else{
						L = 0;
					}

					r = (int) L;
					g = (int) L;
					b = (int) L;

					pixels[i] = new Color(r, g, b, a).getRGB();
				}

				dst.setRGB(0, 0, width, height, pixels, 0, width);

	}

}
