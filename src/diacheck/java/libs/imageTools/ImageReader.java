package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 
 * @author Anton &Ouml;sterberg
 */

public class ImageReader
{
	public final static int REDMASK = 0xff0000;
	public final static int GREENMASK = 0x00ff00;
	public final static int BLUEMASK = 0x0000ff;
	public final static Color CONTROL_FIELD_COLOR = new Color(151, 255, 0);
	private final File image;
	private final Color whiteBalance;
	private final BufferedImage imageData;

	public ImageReader(File file) throws IOException
	{
		this.image = file;
		if(!file.canRead())
			throw new IOException("File " + file + " can not be read");
		whiteBalance = analyzeWhiteBalance();
		imageData = ImageIO.read(image);
	}

	private Color analyzeWhiteBalance() throws IOException
	{
		Color[] whiteBalanceSamples = collectWhiteBalanceData();
		return getAverageColorForData(whiteBalanceSamples);
	}

	private Color[] collectWhiteBalanceData() throws IOException
	{
		return pickColorSamples(new Point(429, 1428), new Point(443, 1442));
	}

	public Color getAverageColorForCoordinates(Point start, Point end)
			throws IOException
	{
		Color[] colorSamples = pickColorSamples(start, end);
		Color averageColor = getAverageColorForData(colorSamples);
		return whiteBalanceCompensation(averageColor);
	}

	private Color[] pickColorSamples(Point start, Point end) throws IOException
	{
		final int size = calculateSampleSize(start, end);
		Color[] samples = new Color[size];

		int xStart, xStop, yStart, yStop;
		if(start.x < end.x)
		{
			xStart = start.x;
			xStop = end.x;
		}
		else
		{
			xStart = end.x;
			xStop = start.x;
		}
		if(start.y < end.y)
		{
			yStart = start.y;
			yStop = end.y;
		}
		else
		{
			yStart = end.y;
			yStop = start.y;
		}

		int sizeCount = 0;
		for(int y = yStart; y < yStop; y += 5)
		{
			for(int x = xStart; x < xStop; x += 5)
			{
				int pixel = imageData.getRGB(x, y);
				assert sizeCount < size : "Going out of bounds with " + x + " and " + y;
				samples[sizeCount++] = getColor(pixel);
			}
		}
		return samples;
	}

	private Color getColor(int pixel)
	{
		return new Color(getRed(pixel), getGreen(pixel), getBlue(pixel));
	}

	public static int calculateSampleSize(Point start, Point end)
	{
		int x = 1 + Math.abs(start.x - end.x) / 5;
		int y = 1 + Math.abs(start.y - end.y) / 5;
		return x * y;
	}

	private Color whiteBalanceCompensation(Color averageColor)
	{
		final int redGreenDiff = whiteBalance.getGreen() - whiteBalance.getRed();
		final int blueGreenDiff = whiteBalance.getGreen() - whiteBalance.getBlue();
		return new Color(averageColor.getRed() + redGreenDiff, averageColor.getGreen(), averageColor.getBlue() + blueGreenDiff);
	}

	public static Color getAverageColorForData(Color[] colorSamples)
	{
		int red, green, blue;
		red = green = blue = 0;
		for(Color sample : colorSamples)
		{
			red += sample.getRed();
			green += sample.getGreen();
			blue += sample.getBlue();
		}
		red /= colorSamples.length;
		green /= colorSamples.length;
		blue /= colorSamples.length;

		return new Color(red, green, blue);
	}

	private static int getRed(final int pixel)
	{
		return (pixel & REDMASK) >> 16;
	}

	private static int getGreen(final int pixel)
	{
		return (pixel & GREENMASK) >> 8;
	}

	private static int getBlue(final int pixel)
	{
		return pixel & BLUEMASK;
	}

	public Set<Point> findEdges() throws IOException
	{
		final int distance = 6;
		Set<Point> edges = new HashSet<Point>();

		for(int y = distance; y < imageData.getHeight(); y += 2)
		{
			for(int x = distance; x < imageData.getWidth(); x += 2)
			{
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

	public static boolean isEdge(int pixel1, int pixel2)
	{
		final int threshold = 45;
		final int diffRed = Math.abs(getRed(pixel1) - getRed(pixel2));
		if(diffRed > threshold)
			return true;
		
		final int diffGreen = Math.abs(getGreen(pixel1) - getGreen(pixel2)); 
		if(diffGreen > threshold)
			return true;
		
		final int diffBlue = Math.abs(getBlue(pixel1) - getBlue(pixel2)); 
		if(diffBlue > threshold)
			return true;
		
		if(diffRed + diffGreen + diffBlue > threshold*2)
			return true;
		
		return false;
	}
	
	public float readAligment()
	{
		return 0;
	}
	
	public Field[] findControlFields()
	{
		final Field[] controlFields = new Field[3];
		controlFields[0] = findField(CONTROL_FIELD_COLOR);
		return controlFields;
	}

	public Field findField(Color fieldColor)
	{
		Point start = null;
		Point end = null;
		
		List<Color> foundPixels = new ArrayList<Color>();
		
		final int imageHeight = imageData.getHeight();
		final int imageWidth = imageData.getWidth();
		
		for(int y = 0; y < imageHeight; y++)
		{
			for(int x = 0; x < imageWidth; x++)
			{
				int pixel = imageData.getRGB(x, y);
				Color color = getColor(pixel);
				if(hasColor(color, fieldColor, 15))
				{
					foundPixels.add(color);
					if(start == null)
						start = new Point(x, y);
					end = new Point(x, y);
				}
			}
		}
		
		return new Field(start, end, foundPixels);
	}
	
	public static boolean hasColor(Color color, Color fieldColor, int threshold)
	{
		final int diffRed = Math.abs(color.getRed() - fieldColor.getRed());
		if(diffRed > threshold)
			return false;
		
		final int diffGreen = Math.abs(color.getGreen() - fieldColor.getGreen());;
		if(diffGreen > threshold)
			return false;
		
		final int diffBlue = Math.abs(color.getBlue() - fieldColor.getBlue());;
		if(diffBlue > threshold)
			return false;
		
		return true;
	}

	public Field findField(Color controlFieldColor, Field fieldToSearchIn)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
