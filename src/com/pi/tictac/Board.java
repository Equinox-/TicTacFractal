package com.pi.tictac;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.pi.tictac.Player.PlayerType;

public class Board implements Renderable {
    public static final int BOARD_SIZE = 3;

    private Renderable[][] board = new Renderable[BOARD_SIZE][BOARD_SIZE];

    private int x, y, width, height, tileWidth, tileHeight;

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

    public Rectangle getTileBounds(int x, int y) {
	return new Rectangle(this.x + (tileWidth * x), this.y
		+ (tileHeight * y), tileWidth, tileHeight);

    }

    @Override
    public void compile(Rectangle r) {
	this.x = r.x;
	this.y = r.y;
	width = r.width;
	height = r.height;
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
    public void render(Graphics2D g) {
	renderGamingGraphics(g);
	if (getWinState()) {
	    renderWinningGraphics(g);
	}
    }

    public void renderGamingGraphics(Graphics2D g) {
	for (int x = 0; x < BOARD_SIZE; x++) {
	    int tileX = this.x + (tileWidth * x);
	    if (x > 0) {
		g.setColor(Color.BLACK);
		g.drawLine(tileX, this.y, tileX, this.y + this.height);
	    }
	    for (int y = 0; y < BOARD_SIZE; y++) {
		int tileY = this.y + (tileHeight * y);
		if (y > 0) {
		    g.setColor(Color.BLACK);
		    g.drawLine(this.x, tileY, this.x + this.width, tileY);
		}
		if (board[x][y] != null) {
		    board[x][y].render(g);
		}
	    }
	}
    }

    public void renderWinningGraphics(Graphics2D g) {
	g.setColor(Color.BLACK);
	Stroke s = g.getStroke();
	g.setStroke(new BasicStroke((float) (Math.log((width / 10) + 1) + 5)));
	if (verticalWin) {
	    g.drawLine(this.x + (tileWidth * winX) + (tileWidth / 2), this.y
		    + (tileHeight / 2), this.x + (tileWidth * winX)
		    + (tileWidth / 2), this.y + this.height - (tileHeight / 2));
	}
	if (horizontalWin) {
	    g.drawLine(this.x + (tileWidth / 2), this.y + (tileHeight * winY)
		    + (tileHeight / 2), this.x + this.width - (tileWidth / 2),
		    this.y + (tileHeight * winY) + (tileHeight / 2));
	}
	if (upLeftDiagWin) {
	    g.drawLine(this.x + (tileWidth / 2), this.y + (tileHeight / 2),
		    this.x + this.width - (tileWidth / 2), this.y + this.height
			    - (tileHeight / 2));
	}
	if (upRightDiagWin) {
	    g.drawLine(this.x + this.width - (tileWidth / 2), this.y
		    + (tileHeight / 2), this.x + (tileWidth / 2), this.y
		    + this.height - (tileHeight / 2));
	}
	g.setStroke(s);
	Color c = getWinner().color();
	g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
	g.fillOval(this.x + 2, this.y + 2, this.width - 4, this.height - 4);
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
		    Rectangle bounds = getTileBounds(x, y);
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
    
    public void clear(){
	upLeftDiagWin = false;
	upRightDiagWin = false;
	verticalWin = false;
	horizontalWin = false;
	winner = null;
	for (int x =0; x<BOARD_SIZE; x++){
	    for (int y = 0;y< BOARD_SIZE; y++){
		board[x][y] = null;
	    }
	}
    }
}
