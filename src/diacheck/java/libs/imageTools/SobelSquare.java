package diacheck.java.libs.imageTools;

import java.awt.Point;

public class SobelSquare
{
	private final Point center;
	private final Point[] adjacentPoints;

	public SobelSquare(int x, int y)
	{
		this(new Point(x, y));
	}

	public SobelSquare(Point point)
	{
		this.center = point;
		this.adjacentPoints = createAdjacentPoints();
	}

	public Point[] getAdjacentPoints()
	{
		return adjacentPoints;
	}

	private Point[] createAdjacentPoints()
	{
		final Point[] points = new Point[8];
		points[0] = new Point(center.x - 1, center.y - 1);
		points[1] = new Point(center.x, center.y - 1);
		points[2] = new Point(center.x + 1, center.y - 1);
		points[3] = new Point(center.x + 1, center.y);
		points[4] = new Point(center.x + 1, center.y + 1);
		points[5] = new Point(center.x, center.y + 1);
		points[6] = new Point(center.x - 1, center.y + 1);
		points[7] = new Point(center.x - 1, center.y);

		return points;
	}

}
