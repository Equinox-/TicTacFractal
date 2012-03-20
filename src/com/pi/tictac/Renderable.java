package com.pi.tictac;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface Renderable {
    public void compile(Rectangle r);

    public void render(Graphics2D g, Rectangle clip);
}
