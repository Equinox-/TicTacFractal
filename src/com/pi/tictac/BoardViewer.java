package com.pi.tictac;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import com.pi.tictac.Player.PlayerType;

public class BoardViewer extends Canvas {
    private static final long serialVersionUID = 1L;
    private PlayerType currPlayer = PlayerType.Red;
    private Board board;
    private BufferStrategy buffer;
    private MouseAdapter mouseAdapter;
    private ComponentAdapter componentAdapter;

    public BoardViewer() {
	board = new Board();
	setSize(1000, 1000);
	setLocation(0, 0);
	setIgnoreRepaint(true);
	setVisible(true);
	mouseAdapter = new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (board.getWinState()) {
		    board.clear();
		    render();
		} else if (board.tileClicked(e.getPoint(), currPlayer,
			e.getButton() == MouseEvent.BUTTON1)) {
		    currPlayer = currPlayer == PlayerType.Red ? PlayerType.Blue
			    : PlayerType.Red;
		    render();
		}
	    }
	};
	componentAdapter = new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		board.compile(new Rectangle(0, 0, getWidth(), getHeight()));
		render();
	    }
	};
    }

    public void start() {
	createBufferStrategy(2);
	buffer = getBufferStrategy();
	board.compile(new Rectangle(0, 0, getWidth(), getHeight()));
	addMouseListener(mouseAdapter);
	addComponentListener(componentAdapter);
	render();
    }

    public void stop() {
	removeMouseListener(mouseAdapter);
	removeComponentListener(componentAdapter);
	buffer.dispose();
	buffer = null;
    }

    public void render() {
	if (buffer != null) {
	    Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
	    g.clearRect(0, 0, getWidth(), getHeight());
	    board.render(g);
	    g.setColor(Color.BLACK);
	    g.drawString("Player: " + currPlayer.name(), 0, 10);
	    buffer.show();
	    g.dispose();
	}
    }
}
