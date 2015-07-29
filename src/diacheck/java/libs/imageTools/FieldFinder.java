package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;

public class FieldFinder
{
	private final BufferedImage imageData;
	private final Color whiteBalance;
	public final static int RIGTH = 1;
	public final static int LEFT = -1;
	
	public FieldFinder(BufferedImage image)
	{
		this.imageData = image;
		this.whiteBalance = analyzeWhiteBalance();
	}
	
	private Color analyzeWhiteBalance()
	{
		try
		{
			return locateField(FieldType.WHITE_BALANCE).getAverageColor();
		}
		catch(IllegalArgumentException exception)
		{
			throw new WhiteBalanceException("Could not find white balance field. This can be because of bad color balance in image");
		}
	}
	
	public Color getWhiteBalance()
	{
		return whiteBalance;
	}
	
	public Field locateField(FieldType fieldType)
	{
		final int x = fieldType.getX(imageData.getWidth());
		final int y = fieldType.getY(imageData.getHeight());
		final Point fieldCenter = new Point(x, y);
		return findRestOfField(fieldType, fieldCenter);
	}
	
	private Field findRestOfField(FieldType fieldType, Point point)
	{
		List<Color> foundColors = new ArrayList<Color>();
		
		Point start = findFirstPixelOfField(fieldType, point);
		
		int y = start.y;
		int x = start.x;
		int direction = RIGTH;
		int searchedPixelsWithNoMatch = 0;
	
		while(searchedPixelsWithNoMatch < 5)
		{		
			Color colorForCurrentPixel = ImageReader.getColor(imageData.getRGB(x, y));
			if(hasColor(colorForCurrentPixel, fieldType.getPermittedColors(), fieldType.getThreshold()))
			{
				searchedPixelsWithNoMatch = 0;
				foundColors.add(colorForCurrentPixel);
			}
			else
			{
				direction /= -1;
				y++;
				searchedPixelsWithNoMatch++;
			}
			x += direction;
		}
		foundColors = whiteBalanceCompensation(foundColors);
		return new Field(fieldType, foundColors);
	}
	
	public List<Color> whiteBalanceCompensation(List<Color> foundColors)
	{
		List<Color> newColors = new ArrayList<Color>(foundColors.size());
		for(Color color : foundColors)
			newColors.add(whiteBalanceCompensation(color));
		return newColors;
	}
	
	public Color whiteBalanceCompensation(Color averageColor)
	{
		final int redGreenDiff = whiteBalance.getGreen() - whiteBalance.getRed();
		final int blueGreenDiff = whiteBalance.getGreen() - whiteBalance.getBlue();
		return new Color(averageColor.getRed() + redGreenDiff, averageColor.getGreen(), averageColor.getBlue() + blueGreenDiff);
	}

	public Point findFirstPixelOfField(FieldType fieldType)
	{
		final int x = fieldType.getX(imageData.getWidth());
		final int y = fieldType.getY(imageData.getHeight());
		final Point fieldCenter = new Point(x, y);
		return  findFirstPixelOfField(fieldType, fieldCenter);
	}
	
	public Point findFirstPixelOfField(FieldType fieldType, Point point)
	{
		Point current = point;
		Point left = new Point(current.x-1, current.y);
		Point up = new Point(current.x, current.y-1);
		
		Color leftColor = ImageReader.getColor(imageData.getRGB(left.x, left.y));
		Color upColor = ImageReader.getColor(imageData.getRGB(up.x, up.y));
		
		final short colorThreshold = fieldType.getThreshold();
		
		while(hasColor(leftColor, fieldType.getPermittedColors(), colorThreshold) || hasColor(upColor, fieldType.getPermittedColors(), colorThreshold))
		{
			if(hasColor(leftColor, fieldType.getPermittedColors(), colorThreshold))
				current = left;
			else
				current = up;
			
			if(hasReachedEdgeOfImage(current))
				throw new IllegalStateException("Reached end of image when looking for first pixel of field for " + fieldType);
			
			left = new Point(current.x-1, current.y);
			up = new Point(current.x, current.y-1);
			leftColor = ImageReader.getColor(imageData.getRGB(left.x, left.y));
			upColor = ImageReader.getColor(imageData.getRGB(up.x, up.y));
		}
		
		return current;
	}
	
	public List<Point> findRandomPixelInEachField(int numberOfFields, Color fieldColor)
	{
		Set<Point> checkedPixels = new HashSet<Point>();
		List<Point> fields = new ArrayList<Point>(numberOfFields);
		
		final int width = imageData.getWidth();
		final int height = imageData.getHeight();
		final int imageResolution = width*height;
		final int expectedFieldSize = imageResolution/50000;
		
		int x = 0;
		int y = 0;
		int increment = imageResolution/500;
		
		//TODO gör om denna för concurrent?
		
		while(fields.size() < numberOfFields && checkedPixels.size() < imageResolution)
		{
			Point currentPixel = new Point(x, y);
			checkedPixels.add(currentPixel);
			Color pixelColor = ImageReader.getColor(imageData.getRGB(x, y));
			if(FieldFinder.hasColor(pixelColor, fieldColor, 25))
			{
				boolean pixelBelongsToAlreadyFoundField = false;
				for(Point pixel : fields)
					if(Point.distance(currentPixel.x, currentPixel.y, pixel.x, pixel.y) < expectedFieldSize)
						pixelBelongsToAlreadyFoundField = true;
				if(!pixelBelongsToAlreadyFoundField)
					fields.add(currentPixel);
			}
			
			x += increment;
			if(x >= width)
			{
				x %= width;
				y++;
			}
			if(y >= height)
			{
				y = 0;
				increment--;
			}
		}
		
		return fields;
	}

	private boolean hasReachedEdgeOfImage(Point current)
	{
		return current.x == 0 || current.y == 0;
	}
	
	public static boolean hasColor(Color matchingColor, Color fieldColor, int threshold)
	{
		final int diffRed = Math.abs(matchingColor.getRed() - fieldColor.getRed());
		if(diffRed > threshold)
			return false;
		
		final int diffGreen = Math.abs(matchingColor.getGreen() - fieldColor.getGreen());;
		if(diffGreen > threshold)
			return false;
		
		final int diffBlue = Math.abs(matchingColor.getBlue() - fieldColor.getBlue());;
		if(diffBlue > threshold)
			return false;
		
		return true;
	}
	
	public static boolean hasColor(Color matchingColor, Set<Color> fieldColors, int threshold)
	{
		for(Color color : fieldColors)
		{
			if(hasColor(matchingColor, color, threshold))
				return true;
		}
		
		return false;
	}
}
