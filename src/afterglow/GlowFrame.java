package afterglow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.opencv.core.Core;

public class GlowFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private GlowPanel displayPanel;
	
	public GlowFrame(String title) {
		super(title);
		
		JMenuBar menubar = new JMenuBar();
		ImageIcon icon = new ImageIcon("exit.png");

		JMenu window = new JMenu("Window");
		window.setMnemonic(KeyEvent.VK_F);

		JMenuItem fMenuItem = new JMenuItem("Full screen", icon);
		fMenuItem.setMnemonic(KeyEvent.VK_F);
		fMenuItem.setToolTipText("Switch to full screen presentation mode");
		final GlowFrame self = this;
		fMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
				setUndecorated(true);
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(self);
				setVisible(false);
				setVisible(true);
			}
		});
		
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

		window.add(fMenuItem);
		menubar.add(window);

		setJMenuBar(menubar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		displayPanel = new GlowPanel();
		setBackground(Color.BLACK);
		add(displayPanel, BorderLayout.CENTER);

		setSize(1280, 720);
		setVisible(true);

	}
	
	public void start(Filter filter) {
		displayPanel.setFilter(filter);
		displayPanel.run();
	}


	public static void main(String arg[]) throws InterruptedException {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// prepare menu bar
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Afterglow");

		// make the JFrame
		GlowFrame frame = new GlowFrame("Afterglow");
		frame.start(new MirrorFilter(new FadeFilter(new TraceFilter())));
	}
}
