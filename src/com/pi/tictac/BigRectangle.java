package com.pi.tictac;

import java.awt.Point;

public class BigRectangle {
    public double x;
    public double y;
    public double width;
    public double height;

    public BigRectangle(double x, double y, double width, double height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }

    public BigRectangle() {

    }

    public boolean contains(Point p) {
	return p.x >= x && p.y >= y && p.x <= x + width && p.y <= y + height;
    }

    @Override
    public String toString() {
	return "[" + x + "," + y + "," + width + "," + height + "]";
    }
}
