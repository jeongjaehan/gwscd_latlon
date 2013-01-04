package com.kthcorp.geo.giscore.coord;

public class DPoint
{
    public double x;
    public double y;

    public DPoint(){;}

    public DPoint(double x, double y)
    {
    	this.x = x;
    	this.y = y;
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
