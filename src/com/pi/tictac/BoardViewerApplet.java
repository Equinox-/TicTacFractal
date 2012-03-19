package com.pi.tictac;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BoardViewerApplet extends java.applet.Applet {
    private static final long serialVersionUID = 1L;
    BoardViewer viewer;

    public BoardViewerApplet() {
	viewer = new BoardViewer();
	add(viewer);
	viewer.setLocation(50, 50);
	viewer.setSize(getWidth() - 75, getHeight() - 75);
	setIgnoreRepaint(true);
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		viewer.setLocation(50, 50);
		viewer.setSize(getWidth() - 75, getHeight() - 75);
	    }
	});
    }

    @Override
    public void start() {
	viewer.start();
    }

    @Override
    public void stop() {
	viewer.stop();
    }
}
