package com.pi.tictac;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import com.pi.tictac.Player.PlayerType;

public class Board implements Renderable {
    public static final int BOARD_SIZE = 3;

    private Renderable[][] board = new Renderable[BOARD_SIZE][BOARD_SIZE];

    private BigRectangle bounds = new BigRectangle();
    private double tileWidth, tileHeight;

    private boolean verticalWin = false;
    private boolean horizontalWin = false;
    private boolean upLeftDiagWin = false;
    private boolean upRightDiagWin = false;
    private int winX, winY;
    private PlayerType winner = null;

    public boolean getWinState() {
	return verticalWin || horizontalWin || upLeftDiagWin || upRightDiagWin;
    }

    public PlayerType getWinner() {
	return getWinState() ? winner : null;
    }

    public BigRectangle getTileBounds(long x, long y) {
	return new BigRectangle(bounds.x + (tileWidth * x), bounds.y
		+ (tileHeight * y), tileWidth, tileHeight);

    }

    @Override
    public void compile(BigRectangle r) {
	bounds.x = r.x;
	bounds.y = r.y;
	bounds.width = r.width;
	bounds.height = r.height;
	tileWidth = r.width / BOARD_SIZE;
	tileHeight = r.height / BOARD_SIZE;
	for (int x = 0; x < BOARD_SIZE; x++) {
	    for (int y = 0; y < BOARD_SIZE; y++) {
		if (board[x][y] != null) {
		    board[x][y].compile(getTileBounds(x, y));
		}
	    }
	}
    }

    public PlayerType getPlayerAt(int x, int y) {
	if (board[x][y] != null) {
	    if (board[x][y] instanceof Board) {
		return ((Board) board[x][y]).getWinner();
	    } else if (board[x][y] instanceof Player) {
		return ((Player) board[x][y]).getType();
	    }
	}
	return null;
    }

    @Override
    public void render(Graphics2D g, BigRectangle clip) {
	renderGamingGraphics(g, clip);
	if (getWinState()) {
	    renderWinningGraphics(g);
	}
    }

    public void renderGamingGraphics(Graphics2D g, BigRectangle clip) {
	for (int x = 0; x < BOARD_SIZE; x++) {
	    double tileC = bounds.x + (tileWidth * x);
	    if (x > 0 && tileC >= clip.x && tileC <= clip.x + clip.width) {
		g.setColor(Color.BLACK);
		g.drawLine(
			(int) tileC,
			(int) Math.max(clip.y, bounds.y),
			(int) tileC,
			(int) Math.min(bounds.y + bounds.height, clip.y
				+ clip.height));
	    }
	    tileC = bounds.y + (tileHeight * x);
	    if (x > 0 && tileC >= clip.y && tileC <= clip.y + clip.height) {
		g.setColor(Color.BLACK);
		g.drawLine(
			(int) Math.max(clip.x, bounds.x),
			(int) tileC,
			(int) Math.min(bounds.x + bounds.width, clip.x
				+ clip.width), (int) tileC);
	    }
	    for (int y = 0; y < BOARD_SIZE; y++) {
		BigRectangle tile = getTileBounds(x, y);
		if (board[x][y] != null) {
		    if (board[x][y] != null && rectIntersects(clip, tile)// tile.intersects(clip)
			    && tile.width > 3 && tile.height > 3) {
			board[x][y].render(g, clip);
		    }
		}
	    }
	}
    }

    private static boolean rectIntersects(BigRectangle a, BigRectangle b) {
	double ax2 = a.x + a.width;
	double ay2 = a.y + a.height;
	double bx2 = b.x + b.width;
	double by2 = b.y + b.height;
	return !(b.x > ax2 || bx2 < a.x || b.y > ay2 || by2 < a.y);
    }

    public void renderWinningGraphics(Graphics2D g) {
	g.setColor(Color.BLACK);
	Stroke s = g.getStroke();
	g.setStroke(new BasicStroke(
		(float) (Math.log((bounds.width / 10) + 1) + 5)));
	if (verticalWin) {
	    g.drawLine((int) (bounds.x + (tileWidth * winX) + (tileWidth / 2)),
		    (int) (bounds.y + (tileHeight / 2)), (int) (bounds.x
			    + (tileWidth * winX) + (tileWidth / 2)),
		    (int) (bounds.y + bounds.height - (tileHeight / 2)));
	}
	if (horizontalWin) {
	    g.drawLine((int) (bounds.x + (tileWidth / 2)), (int) (bounds.y
		    + (tileHeight * winY) + (tileHeight / 2)), (int) (bounds.x
		    + bounds.width - (tileWidth / 2)), (int) (bounds.y
		    + (tileHeight * winY) + (tileHeight / 2)));
	}
	if (upLeftDiagWin) {
	    g.drawLine((int) (bounds.x + (tileWidth / 2)),
		    (int) (bounds.y + (tileHeight / 2)), (int) (bounds.x
			    + bounds.width - (tileWidth / 2)), (int) (bounds.y
			    + bounds.height - (tileHeight / 2)));
	}
	if (upRightDiagWin) {
	    g.drawLine((int) (bounds.x + bounds.width - (tileWidth / 2)),
		    (int) (bounds.y + (tileHeight / 2)),
		    (int) (bounds.x + (tileWidth / 2)), (int) (bounds.y
			    + bounds.height - (tileHeight / 2)));
	}
	g.setStroke(s);
	Color c = getWinner().color();
	g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
	g.fillOval((int) bounds.x + 2, (int) bounds.y + 2,
		(int) bounds.width - 4, (int) bounds.height - 4);
    }

    public void updateWin(int tX, int tY, PlayerType t) {
	verticalWin = false;
	horizontalWin = false;
	upLeftDiagWin = false;
	upRightDiagWin = false;
	for (int off = 0; off < BOARD_SIZE; off++) {
	    verticalWin = (off == 0 || verticalWin)
		    && getPlayerAt(tX, off) == t;
	    horizontalWin = (off == 0 || horizontalWin)
		    && getPlayerAt(off, tY) == t;
	}
	if (((tX + tY) & 0x0001) == 0) {
	    for (int off = 0; off < BOARD_SIZE; off++) {
		upLeftDiagWin = (off == 0 || upLeftDiagWin)
			&& getPlayerAt(off, off) == t;
		upRightDiagWin = (off == 0 || upRightDiagWin)
			&& getPlayerAt(BOARD_SIZE - 1 - off, off) == t;
	    }
	}
	if (getWinState()) {
	    winX = tX;
	    winY = tY;
	    winner = t;
	}
    }

    public boolean tileClicked(Point p, PlayerType player, boolean left) {
	if (!getWinState()) {
	    for (int x = 0; x < BOARD_SIZE; x++) {
		for (int y = 0; y < BOARD_SIZE; y++) {
		    BigRectangle bounds = getTileBounds(x, y);
		    if (bounds.contains(p)) {
			if (board[x][y] == null) {
			    if (left) {
				board[x][y] = new Player(player);
				updateWin(x, y, player);
			    } else {
				board[x][y] = new Board();
			    }
			    board[x][y].compile(bounds);
			    return true;
			} else if (board[x][y] instanceof Board) {
			    if (((Board) board[x][y]).tileClicked(p, player,
				    left)) {
				if (((Board) board[x][y]).getWinState()) {
				    // board[x][y] = new Player(player);
				    // board[x][y].compile(bounds);
				    updateWin(x, y,
					    ((Board) board[x][y]).getWinner());
				}
				return true;
			    }
			}
			break;
		    }
		}
	    }
	}
	return false;
    }

    public void clear() {
	upLeftDiagWin = false;
	upRightDiagWin = false;
	verticalWin = false;
	horizontalWin = false;
	winner = null;
	for (int x = 0; x < BOARD_SIZE; x++) {
	    for (int y = 0; y < BOARD_SIZE; y++) {
		board[x][y] = null;
	    }
	}
    }
}
