package com.pi.tictac;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import com.pi.tictac.Player.PlayerType;

public class BoardViewer extends Canvas {
    private static final long serialVersionUID = 1L;
    private PlayerType currPlayer = PlayerType.Red;
    private Board board;
    private BufferStrategy buffer;
    private MouseListener mouseAdapter;
    private ComponentListener componentAdapter;
    private MouseMotionListener mouseMotionAdapter;
    private MouseWheelListener mouseWheelAdapter;
    private KeyListener keyAdapter;

    private int ulX = 0, ulY = 0;
    private int cacheULX = Integer.MAX_VALUE, cacheULY = Integer.MAX_VALUE;
    private float zoom = 1;
    private Point mouse = new Point(-1, -1);
    private Point dragStart = null;

    public BoardViewer() {
	board = new Board();
	setSize(1000, 1000);
	setLocation(0, 0);
	setIgnoreRepaint(true);
	setVisible(true);
	mouseAdapter = new MouseAdapter() {
	    boolean trigger = false;

	    @Override
	    public void mousePressed(MouseEvent e) {
		if (!trigger) {
		    trigger = true;
		    cacheULX = ulX;
		    cacheULY = ulY;
		}
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
		trigger = false;
		cacheULX = Integer.MAX_VALUE;
		cacheULY = Integer.MAX_VALUE;
		dragStart = null;
	    }

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
		compileBoard();
		render();
	    }
	};
	mouseWheelAdapter = new MouseWheelListener() {
	    @Override
	    public void mouseWheelMoved(MouseWheelEvent e) {
		Point cW = screenToWorld(e.getX(), e.getY());
		zoom -= (float) (e.getScrollAmount() * e.getWheelRotation())
			* .05f * zoom;
		Point nW = screenToWorld(e.getX(), e.getY());
		ulX += cW.x - nW.x;
		ulY += cW.y - nW.y;
		System.out.println(cW.toString() + "\t"
			+ screenToWorld(e.getX(), e.getY()).toString());
		compileBoard();
		render();
	    }
	};
	mouseMotionAdapter = new MouseMotionAdapter() {
	    @Override
	    public void mouseDragged(MouseEvent e) {
		if (cacheULX != Integer.MAX_VALUE
			&& cacheULY != Integer.MAX_VALUE) {
		    if (dragStart == null)
			dragStart = e.getPoint();
		    ulX = cacheULX + dragStart.x - e.getX();
		    ulY = cacheULY + dragStart.y - e.getY();
		    compileBoard();
		    render();
		}
	    }

	    @Override
	    public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	    }
	};

	keyAdapter = new KeyAdapter() {

	    @Override
	    public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'c') {
		    Point world = screenToWorld(mouse.x, mouse.y);
		    ulX = world.x - Math.round(getWidth() / 2f / zoom);
		    ulY = world.y - Math.round(getHeight() / 2f / zoom);
		    compileBoard();
		    render();
		} else if (e.getKeyChar() == 'x') {
		    ulX = 0;
		    ulY = 0;
		    compileBoard();
		    render();
		}
	    }
	};
    }

    public void compileBoard() {
	board.compile(new Rectangle(-ulX, -ulY, Math.round(getWidth() * zoom),
		Math.round(getHeight() * zoom)));
    }

    public Point screenToWorld(float x, float y) {
	return new Point(Math.round((x / zoom) + ulX), Math.round((y / zoom)
		+ ulY));
    }

    public void start() {
	createBufferStrategy(2);
	buffer = getBufferStrategy();
	compileBoard();
	addMouseListener(mouseAdapter);
	addMouseMotionListener(mouseMotionAdapter);
	addMouseWheelListener(mouseWheelAdapter);
	addComponentListener(componentAdapter);
	addKeyListener(keyAdapter);
	render();
	transferFocus();
    }

    public void stop() {
	removeMouseListener(mouseAdapter);
	removeComponentListener(componentAdapter);
	removeMouseMotionListener(mouseMotionAdapter);
	removeMouseWheelListener(mouseWheelAdapter);
	removeKeyListener(keyAdapter);
	buffer.dispose();
	buffer = null;
    }

    public void render() {
	if (buffer != null) {
	    Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
	    g.clearRect(0, 0, getWidth(), getHeight());
	    board.render(g, getBounds());
	    g.setColor(Color.BLACK);
	    g.drawString("Player: " + currPlayer.name(), 0, 10);
	    buffer.show();
	    g.dispose();
	}
    }
}
