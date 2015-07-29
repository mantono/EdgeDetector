package diacheck.java.libs;

import java.awt.Point;

public class Triangle
{
	private final Point a, b, c;
	
	public Triangle(final Point a, final Point b)
	{
		this.a = a;
		this.b = b;
		this.c = new Point(b.x, a.y);
	}
	
	public double getBottomLeftAngle()
	{
		final double hypotenuse = getHypotenuse();
		final double bottom = getBottomSide();
		return Math.acos(bottom/hypotenuse);
	}
	
	public double getTopRightAngle()
	{
		final double hypotenuse = getHypotenuse();
		final double right = getRightSide();
		return Math.acos(right/hypotenuse);
	}
	
	public double getHypotenuse()
	{
		return distanceBetween(a, b);
	}

	public double getRightSide()
	{
		if(a.x == b.x)
			return distanceBetween(a, b);
		if(a.x == c.x)
			return distanceBetween(a, c);
		return distanceBetween(b, c);
	}

	public double getBottomSide()
	{
		final double distAB = distanceBetween(a, b);
		final double distAC = distanceBetween(a, c);
		if(a.y == b.y)
			return distanceBetween(a, b);
		if(a.y == c.y)
			return distanceBetween(a, c);
		return distanceBetween(b, c);
	}
	
	public static double distanceBetween(Point point1, Point point2)
	{
		return Point.distance(point1.x, point1.y, point2.x, point2.y);
	}
}
