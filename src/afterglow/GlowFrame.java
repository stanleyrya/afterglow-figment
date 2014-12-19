package afterglow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class GlowFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private GlowPanel displayPanel;

	public GlowFrame(String title) throws IOException {
		super(title);

		JMenuBar menubar = new JMenuBar();

		setJMenuBar(menubar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		displayPanel = new GlowPanel();
		setBackground(Color.BLACK);
		add(displayPanel, BorderLayout.CENTER);

		enableOSXFullscreen(this);

		setSize(1280, 720);
		setVisible(true);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void enableOSXFullscreen(Window window) {
		try {
			Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
			Class params[] = new Class[] { Window.class, Boolean.TYPE };
			Method method = util.getMethod("setWindowCanFullScreen", params);
			method.invoke(util, window, true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void start(ControlFrame controller) {
		// displayPanel.setFilter(new MirrorFilter(new FadeFilter(new HaloFilter(new TraceFilter()))));
		controller.setCanvas(displayPanel);
		displayPanel.run();
	}
}
