package com.pi.tictac;

import java.awt.Graphics2D;

public interface Renderable {
    public void compile(BigRectangle bigRectangle);

    public void render(Graphics2D g, BigRectangle clip);
}
