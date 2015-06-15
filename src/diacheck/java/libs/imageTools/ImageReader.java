package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import diacheck.java.libs.Triangle;

/**
 * 
 * @author Anton &Ouml;sterberg
 */

public class ImageReader
{
	public final static int REDMASK = 0xff0000;
	public final static int GREENMASK = 0x00ff00;
	public final static int BLUEMASK = 0x0000ff;
	public final static int RIGTH = 1;
	public final static int LEFT = -1;
	public final static Color CONTROL_FIELD_COLOR = new Color(151, 255, 0);
	private final File image;
	private final Color whiteBalance;
	private final BufferedImage imageData;
	private Point startOfFirstControlField;

	public ImageReader(File file) throws IOException
	{
		this.image = file;
		if(!file.canRead())
			throw new FileNotFoundException("File " + file + " can not be read");
		imageData = ImageIO.read(image);
		whiteBalance = analyzeWhiteBalance();
		checkNoiseLevels();
	}

	private void checkNoiseLevels()
	{
		if(whiteBalance.getRed() - whiteBalance.getBlue() > 25)
			throw new HighNoiseException("Noise is above allowed threshold, red: " + whiteBalance.getRed() + " - blue: " + whiteBalance.getBlue());
		if(whiteBalance.getRed() - whiteBalance.getGreen() > 25)
			throw new HighNoiseException("Noise is above allowed threshold, red: " + whiteBalance.getRed() + " - green: " + whiteBalance.getGreen());
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
		imageData.getRGB(1, 1);

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

	public static String fileType(File filePath)
	{
		final String filename = filePath.getPath();
		final String[] filenameSplitted = filename.split("\\.");
		final int fileTypePosition = filenameSplitted.length - 1;
		return filenameSplitted[fileTypePosition].toLowerCase();
	}
	
	public float readAligment()
	{
		final Field[] controlFields = findControlFields();
		Field topLeft, topRight;
		topLeft = getTopLeftControlField(controlFields);
		topRight = getTopRightControlField(controlFields);
		Triangle triangle = new Triangle(topLeft.getStart(), topRight.getStart());
		startOfFirstControlField = topLeft.getStart();
		return triangle.getBottomLeftAngle();
	}
	


	private Field getBottomControlField(Field[] fields)
	{
		if(fields[0].getStart().y > fields[1].getStart().y && fields[0].getStart().y > fields[2].getStart().y)
			return fields[0];
		if(fields[1].getStart().y > fields[0].getStart().y && fields[1].getStart().y > fields[2].getStart().y)
			return fields[1];
		return fields[2];
	}

	private Field getTopRightControlField(Field[] fields)
	{
		if(fields[0].getStart().x > fields[1].getStart().x && fields[0].getStart().x > fields[2].getStart().x)
			return fields[0];
		if(fields[1].getStart().x > fields[0].getStart().x && fields[1].getStart().x > fields[2].getStart().x)
			return fields[1];
		return fields[2];
	}

	private Field getTopLeftControlField(Field[] fields)
	{
		if(fields[0].getStart().x < fields[1].getStart().x && fields[0].getStart().x < fields[2].getStart().x)
			return fields[0];
		if(fields[1].getStart().x < fields[0].getStart().x && fields[1].getStart().x < fields[2].getStart().x)
			return fields[1];
		return fields[2];
	}
	
	public Field[] findControlFields()
	{
		final Field[] controlFields = new Field[3];
		controlFields[0] = findField(CONTROL_FIELD_COLOR);
		final Point startForSecondField = new Point(controlFields[0].getEnd().x + 1, controlFields[0].getEnd().y); 
		controlFields[1] = findField(CONTROL_FIELD_COLOR, startForSecondField);
		final Point startForThirdField = new Point(controlFields[1].getEnd().x + 1, controlFields[1].getEnd().y);
		controlFields[2] = findField(CONTROL_FIELD_COLOR, startForThirdField);
		return controlFields;
	}
	
	public Field findField(Color fieldColor)
	{
		return findField(fieldColor, new Point(0, 0));
	}

	public Field findField(Color fieldColor, final Point startingPoint)
	{
		List<Color> foundColors = new ArrayList<Color>();
		
		final int imageHeight = imageData.getHeight();
		final int imageWidth = imageData.getWidth();
		
		Point start = findFirstPixelOfField(fieldColor, startingPoint);
		Point end = findFirstPixelOfField(fieldColor, startingPoint);
		
		int y = start.y;
		int x = start.x;
		int direction = RIGTH;
		int searchedPixelsWithNoMatch = 0;
		int searchArea = calculateSearchAreaSize(start, end);
		
		while(searchedPixelsWithNoMatch < searchArea)
		{
			Color colorForCurrentPixel = getColor(imageData.getRGB(x, y));
			if(hasColor(colorForCurrentPixel, fieldColor, 25))
			{
				searchedPixelsWithNoMatch = 0;
				foundColors.add(colorForCurrentPixel);
				if(x > end.x)
				{
					end = new Point(x, y);
					searchArea = calculateSearchAreaSize(start, end);
				}
				if(x < start.x)
				{
					start = new Point(x, y);
					searchArea = calculateSearchAreaSize(start, end);
				}
			}
			else
			{
				if(x < start.x)
				{
					direction = RIGTH;
					y++;
				}
				else if(x > end.x)
				{
					direction = LEFT;
					y++;
				}
				searchedPixelsWithNoMatch++;
			}
			x += direction;
		}
		
		end = new Point(end.x, y);

		return new Field(start, end, foundColors);
	}
	
	private int calculateSearchAreaSize(Point start, Point end)
	{
		return Math.abs(start.x - end.x)*2 + 25;
	}

	private Point findFirstPixelOfField(Color fieldColor, Point startingPoint)
	{
		final int imageHeight = imageData.getHeight();
		final int imageWidth = imageData.getWidth();
		int y = startingPoint.y;
		int x = startingPoint.x;
		
		for(; y < imageHeight; y++)
		{
			for(; x < imageWidth ; x++)
			{
				int pixel = imageData.getRGB(x, y);
				Color color = getColor(pixel);
				if(hasColor(color, fieldColor, 25))
					return new Point(x, y);
			}
			x = 0;
		}
		throw new IllegalStateException("Could not find a pixel with specified color (" + fieldColor + ") within threshold");
	}

	private boolean fieldIsFinished(int x, int y, Point end)
	{
		return end != null && x - end.x > 5 && y - end.y > 5;
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

	public Point getStartOfFirstControlField()
	{
		return startOfFirstControlField;
	}
}
