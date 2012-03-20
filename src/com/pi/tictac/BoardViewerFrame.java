package com.pi.tictac;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class BoardViewerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private BoardViewer viewer;

    public BoardViewerFrame() {
	super("Tic Tac Fractal");
	setSize(500,500);
	setLocation(0, 0);
	setVisible(true);
	viewer = new BoardViewer();
	add(viewer);
	viewer.setLocation(50, 50);
	viewer.setSize(getWidth() - 75, getHeight() - 75);
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		viewer.setLocation(50, 50);
		viewer.setSize(getWidth() - 75, getHeight() - 75);
	    }
	});
	viewer.start();
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void dispose() {
	viewer.stop();
	super.dispose();
    }

    public static void main(String[] args) {
	new BoardViewerFrame();
    }
}
