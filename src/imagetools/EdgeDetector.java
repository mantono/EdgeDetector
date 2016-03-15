package imagetools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class purpose is to identify edges in a {@link BufferedImage}.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class EdgeDetector
{
	private final BufferedImage imageData;
	private final short threshold;

	public EdgeDetector(BufferedImage image)
	{
		this.imageData = image;
		float contrastFactor = calculateContrast();
		this.threshold = (short) (110 * contrastFactor);
	}

	public EdgeDetector(BufferedImage image, final int threshold)
	{
		this.imageData = image;
		this.threshold = (short) threshold;
	}

	public float calculateContrast()
	{
		final int width = imageData.getWidth() - 2;
		final int height = imageData.getHeight() - 2;

		final short averageBrightness = getAverageBrightness();
		int diffFromAverage = 0;
		int checkedPixels = 0;

		for(int y = 0; y < height; y += 2)
		{
			for(int x = 0; x < width; x += 2)
			{
				checkedPixels++;
				diffFromAverage += Math.abs(averageBrightness - getAverageBrightnessForPixel(x, y));
			}
		}

		final float contrast = diffFromAverage / checkedPixels;

		return contrast / 127;
	}

	private short getAverageBrightness()
	{
		final int width = imageData.getWidth() - 1;
		final int height = imageData.getHeight() - 1;
		int brightness = 0;

		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				brightness += getAverageBrightnessForPixel(x, y);

		return (short) (brightness / (width * height));
	}

	protected short getMaxBrightnessForPixel(int x, int y)
	{
		final Color color = ColorConverter.getColor(imageData.getRGB(x, y));
		int maxValue = 0;
		if(color.getRed() > maxValue)
			maxValue = color.getRed();
		if(color.getGreen() > maxValue)
			maxValue = color.getGreen();
		if(color.getBlue() > maxValue)
			maxValue = color.getBlue();

		return (short) maxValue;
	}

	private short getAverageBrightnessForPixel(int x, int y)
	{
		final Color color = ColorConverter.getColor(imageData.getRGB(x, y));
		int maxValue = color.getRed();
		maxValue += color.getGreen();
		maxValue += color.getBlue();

		return (short) (maxValue / 3);
	}

	/**
	 * This method locates all pixel coordinates which makes up the "edges" in
	 * the image. It compares to pixel at a time with the
	 * {@link #isEdge(int, int) isEdge}-method.
	 * 
	 * @return a set containing the Point for each pixel that is considered to
	 * be an edge in the image.
	 */

	public Set<Point> findEdges()
	{
		Set<Point> edges = new HashSet<Point>();
		final int width = imageData.getWidth();
		final int height = imageData.getHeight();
		final int resolution = width * height;
		final byte distance = (byte) (Math.log10(resolution));
		final double[] sobelValues = new double[resolution];
		final double[] sobelAngles = new double[resolution];

		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				final int index = y * width + x;
				final Point point = new Point(x, y);
				SobelSquare square = new SobelSquare(imageData, point, distance);
				sobelValues[index] = square.getSobelValue();
				sobelAngles[index] = square.getAngle();
			}
		}

		final double maxValue = getMaxValue(sobelValues);
		normalize(sobelValues, maxValue);

		for(int y = 0; y < height - distance - 1; y++)
		{
			for(int x = 0; x < width - distance - 1; x++)
			{
				final int index = y * width + x;
				if(sobelValues[index] > 0.18 && isSharpEdge(sobelAngles[index]))
					edges.add(new Point(x, y));
			}
		}

		fillGaps(edges);
		smoothEdges(edges, 4);
		smoothEdges(edges, 3);
		smoothEdges(edges, 2);
		smoothEdges(edges, 2);

		return edges;
	}

	private void normalize(double[] sobelValues, double maxValue)
	{
		for(int i = 0; i < sobelValues.length; i++)
			sobelValues[i] /= maxValue;
	}

	private double getMaxValue(double[] sobelValues)
	{
		double max = 0;
		for(double d : sobelValues)
			if(d > max)
				max = d;
		return max;
	}

	private boolean isSharpEdge(double angle)
	{
		final double angleThreshold = Math.PI / 4;
		final double divider = Math.PI / 2;
		final double orientation = angle % divider;
		final double orientationDiff = Math.abs(orientation - divider);
		return orientation < angleThreshold || orientationDiff < angleThreshold;
	}

	private void smoothEdges(Set<Point> edges, final int limit)
	{
		Iterator<Point> iterator = edges.iterator();

		while(iterator.hasNext())
		{
			final Point pixel = iterator.next();
			if(numberOfAdjacentEdgePixels(edges, pixel) < limit)
				iterator.remove();
		}
	}

	private int numberOfAdjacentEdgePixels(Set<Point> edges, Point pixel)
	{
		Point[] pixels = new SobelSquare(imageData, pixel, 1).getAdjacentPoints();
		int matches = 0;
		for(Point p : pixels)
			if(edges.contains(p))
				matches++;
		return matches;
	}

	private void fillGaps(Set<Point> edges)
	{
		Set<Point> gaps = new HashSet<Point>();
		for(Point pixel : edges)
		{
			if(edges.contains(new Point(pixel.x, pixel.y - 2)))
				gaps.add(new Point(pixel.x, pixel.y - 1));
			if(edges.contains(new Point(pixel.x - 2, pixel.y)))
				gaps.add(new Point(pixel.x - 1, pixel.y));
			if(edges.contains(new Point(pixel.x - 2, pixel.y - 2)))
				gaps.add(new Point(pixel.x - 1, pixel.y - 1));
		}
		edges.addAll(gaps);
	}

	/**
	 * Compares the individual color channels for each pixels color and the sum
	 * for of all differences for all three color channels.
	 * 
	 * @param pixel1 first pixel
	 * @param pixel2 second pixel
	 * @return true if the difference one a single color channels is greater
	 * than the threshold value, or if the sum of all color channels differences
	 * is greater than two times the threshold value. If not, it will return
	 * false.
	 */
	public boolean isEdge(int pixel1, int pixel2)
	{
		final int diffRed = Math.abs(ColorConverter.getRed(pixel1) - ColorConverter.getRed(pixel2));
		if(diffRed > threshold)
			return true;

		final int diffGreen = Math.abs(ColorConverter.getGreen(pixel1) - ColorConverter.getGreen(pixel2));
		if(diffGreen > threshold)
			return true;

		final int diffBlue = Math.abs(ColorConverter.getBlue(pixel1) - ColorConverter.getBlue(pixel2));
		if(diffBlue > threshold)
			return true;

		if(diffRed + diffGreen + diffBlue > threshold * 2)
			return true;

		return false;
	}
}
