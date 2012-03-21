package com.pi.tictac;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
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
import java.awt.geom.Point2D;
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

    private double ulX = 0, ulY = 0;
    private double cacheULX = Double.MAX_VALUE, cacheULY = Double.MAX_VALUE;
    private double zoom = 1;

    private Point mouse = new Point(-1, -1);
    private Point2D dragStart = null;

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
		cacheULX = Double.MAX_VALUE;
		cacheULY = Double.MAX_VALUE;
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
		Point2D cW = screenToWorld(e.getX(), e.getY());
		zoom -= (double) (e.getScrollAmount() * e.getWheelRotation())
			* .05d * zoom;
		Point2D nW = screenToWorld(e.getX(), e.getY());
		ulX += cW.getX() - nW.getX();
		ulY += cW.getY() - nW.getY();
		compileBoard();
		render();
	    }
	};
	mouseMotionAdapter = new MouseMotionAdapter() {
	    @Override
	    public void mouseDragged(MouseEvent e) {
		if (cacheULX != Double.MAX_VALUE
			&& cacheULY != Double.MAX_VALUE) {
		    if (dragStart == null)
			dragStart = e.getPoint();
		    ulX = cacheULX
			    + ((dragStart.getX() - ((double) e.getX())) / zoom);
		    ulY = cacheULY
			    + ((dragStart.getX() - ((double) e.getY())) / zoom);
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
		    Point2D world = screenToWorld(mouse.x, mouse.y);
		    ulX = world.getX() - (getWidth() / 2d / zoom);
		    ulY = world.getY() - (getHeight() / 2d / zoom);
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
	BigRectangle r = new BigRectangle(-ulX * zoom, -ulY * zoom,
		Math.round(Math.min(getWidth(), getHeight()) * zoom),
		Math.round(Math.min(getWidth(), getHeight()) * zoom));
	board.compile(r);
	System.out.println(r.toString());
    }

    public Point2D screenToWorld(double x, double y) {
	return new Point2D.Double(x / zoom + ulX, y / zoom + ulY);
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
	requestFocus();
	requestFocusInWindow();
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
	    board.render(g, new BigRectangle(0, 0, getWidth(), getHeight()));
	    g.setColor(Color.BLACK);
	    g.drawString("Player: " + currPlayer.name(), 0, 10);
	    buffer.show();
	    g.dispose();
	}
    }
}
