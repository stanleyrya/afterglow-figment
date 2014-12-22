package afterglow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

public class ControlFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private ControlPanel displayPanel;
	
	public ControlFrame(String title) throws FontFormatException, IOException {
		super(title);
		
		JMenuBar menubar = new JMenuBar();

		getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
		getRootPane().getActionMap().put("close", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				System.out.println("escaped");
				dispose();
				setUndecorated(false);
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
				setVisible(true);
			}
		});

		setJMenuBar(menubar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		displayPanel = new ControlPanel();
		setBackground(Color.BLACK);
		add(displayPanel, BorderLayout.CENTER);
		
		enableOSXFullscreen(this);

		setSize(1280, 720);
		setVisible(true);
		displayPanel.start();
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
    public static void enableOSXFullscreen(Window window) {
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

	public void setCanvas(GlowPanel panel) {
		displayPanel.setCanvas(panel);
	}

}
