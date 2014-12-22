package afterglow;

import java.awt.FontFormatException;
import java.io.IOException;

import org.opencv.core.Core;

public class Afterglow {

	public static void main(String arg[]) throws InterruptedException, FontFormatException, IOException {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// prepare menu bar
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Afterglow");

		// make the JFrame
		ControlFrame crame = new ControlFrame("Afterglow");
		GlowFrame frame = new GlowFrame("Afterglow");
		frame.start(crame);
	}

}
