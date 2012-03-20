package com.pi.tictac;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Player implements Renderable {
    public static enum PlayerType {
	Red(Color.RED), Blue(Color.BLUE);
	private final Color color;

	private PlayerType(Color color) {
	    this.color = color;
	}

	public Color color() {
	    return color;
	}
    }

    private PlayerType player;
    private int x, y, width, height;

    public Player(PlayerType type) {
	this.player = type;
    }

    public PlayerType getType() {
	return player;
    }

    @Override
    public void compile(Rectangle r) {
	x = r.x + 2;
	y = r.y + 2;
	width = r.width - 4;
	height = r.height - 4;
    }

    @Override
    public void render(Graphics2D g, Rectangle clip) {
	g.setColor(player.color());
	g.fillOval(x, y, width, height);
    }
}
