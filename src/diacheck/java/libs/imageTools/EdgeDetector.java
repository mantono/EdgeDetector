package diacheck.java.libs.imageTools;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EdgeDetector
{
	private final BufferedImage imageData;
	
	public EdgeDetector(BufferedImage image)
	{
		this.imageData = image;
	}
	
	
	/**
	 * This method locates all pixel coordinates which makes up the "edges" in the image. It compares to pixel at a time with the {@link #isEdge(int, int) isEdge}-method.
	 * @return a set containing the Point for each pixel that is considered to be an edge in the image.
	 * @throws IOException
	 */
	public Set<Point> findEdges() throws IOException
	{
		final int distance = 4;
		Set<Point> edges = new HashSet<Point>();
		final int width = imageData.getWidth() - 2;
		final int height = imageData.getHeight() - 2;
		for(int y = distance; y < height; y += 2)
		{
			for(int x = distance; x < width; x += 2)
			{
				//TODO x and y may be out of bounds after increment?
				int currentPixel = imageData.getRGB(x, y);
				int previousPixel = imageData.getRGB(x-distance,  y);
				if(isEdge(currentPixel, previousPixel))
					edges.add(new Point(x-distance/2, y));
				previousPixel = imageData.getRGB(x, y-distance);
				if(isEdge(currentPixel, previousPixel))
					edges.add(new Point(x, y-distance/2));
			}
		}
		fillGaps(edges);
		return edges;
	}

	private void fillGaps(Set<Point> edges)
	{
		Set<Point> gaps = new HashSet<Point>();
		for(Point pixel : edges)
		{
			if(edges.contains(new Point(pixel.x, pixel.y-2)))
				gaps.add(new Point(pixel.x, pixel.y-1));
			if(edges.contains(new Point(pixel.x-2, pixel.y)))
				gaps.add(new Point(pixel.x-1, pixel.y));
			if(edges.contains(new Point(pixel.x-2, pixel.y-2)))
				gaps.add(new Point(pixel.x-1, pixel.y-1));		
		}
		edges.addAll(gaps);
	}

	/**
	 * Compares the individual color channels for each pixels color and the sum for of all differences for all three color channels.  
	 * @param pixel1 first pixel
	 * @param pixel2 second pixel
	 * @return true if the difference one a single color channels is greater than 35, or if the sum of all color channels differences is greater than 70.
	 * If not, it will return false.
	 */
	public static boolean isEdge(int pixel1, int pixel2)
	{
		final int threshold = 35;
		final int diffRed = Math.abs(ColorConverter.getRed(pixel1) - ColorConverter.getRed(pixel2));
		if(diffRed > threshold)
			return true;
		
		final int diffGreen = Math.abs(ColorConverter.getGreen(pixel1) - ColorConverter.getGreen(pixel2)); 
		if(diffGreen > threshold)
			return true;
		
		final int diffBlue = Math.abs(ColorConverter.getBlue(pixel1) - ColorConverter.getBlue(pixel2)); 
		if(diffBlue > threshold)
			return true;
		
		if(diffRed + diffGreen + diffBlue > threshold*2)
			return true;
		
		return false;
	}	
}
