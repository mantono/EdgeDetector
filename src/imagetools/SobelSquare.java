package imagetools;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class SobelSquare extends Point
{
	private final static int UP = -1;
	private final static int LEFT = -1;
	private final static int RIGHT = 1;
	private final static int DOWN = 1;
	
	private final BufferedImage imageData;
	private final Point[] points;
	//private final int distance;

	public SobelSquare(BufferedImage imageData, int x, int y, final int distance)
	{
		this(imageData, new Point(x, y), distance);
	}

	public SobelSquare(BufferedImage imageData, Point point, int distance)
	{
		super(point);
		this.imageData = imageData;
		this.points = new Point[8];
		points[0] = new Point(x + LEFT*distance, y + UP*distance);
		points[1] = new Point(x, y + UP*distance);
		points[2] = new Point(x + RIGHT*distance, y + UP*distance);
		points[3] = new Point(x + RIGHT*distance, y);
		points[4] = new Point(x + RIGHT*distance, y + DOWN*distance);
		points[5] = new Point(x, y + DOWN*distance);
		points[6] = new Point(x + LEFT*distance, y + DOWN*distance);
		points[7] = new Point(x + LEFT*distance, y);
		//this.distance = distance;
		checkForOutOfBounds();
	}

	public Point[] getAdjacentPoints()
	{
		return points;
	}

	public int getVerticalSobelValue()
	{
		int sum = 0;
		sum += -1 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[0].x, points[0].y));
		sum += -2 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[7].x, points[7].y));
		sum += -1 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[6].x, points[6].y));
		sum += ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[4].x, points[4].y));
		sum += 2 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[3].x, points[3].y));
		sum += ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[2].x, points[2].y));
		return sum;
	}

	public int getHorizontalSobelValue()
	{
		int sum = 0;
		sum += -1 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[0].x, points[0].y));
		sum += -2 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[1].x, points[1].y));
		sum += -1 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[2].x, points[2].y));
		sum += ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[6].x, points[6].y));
		sum += 2 * ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[5].x, points[5].y));
		sum += ColorConverter.getAverageBrightnessForPixel(imageData.getRGB(points[4].x, points[4].y));
		return sum;
	}
	
	public double getSobelValue()
	{
		final double squareOfVerticalSobelValue = Math.pow(getVerticalSobelValue(), 2); 
		final double squareOfHorizontalSobelValue = Math.pow(getHorizontalSobelValue(), 2); 
		return Math.sqrt(squareOfVerticalSobelValue + squareOfHorizontalSobelValue);
	}
	
	private void checkForOutOfBounds()
	{
		final int width = imageData.getWidth();
		final int height = imageData.getHeight();
		for(int i = 0; i < points.length; i++)
			if(points[i].x < 0 || points[i].y < 0 || points[i].x >= width || points[i].y >= height)
				points[i] = new Point(this);		
	}

	public double getAngle()
	{
		final double ySobel = getVerticalSobelValue() + 0.0001;
		final double xSobel = getHorizontalSobelValue() + 0.0001;
		return Math.atan2(ySobel, xSobel) + Math.PI;
	}

}
