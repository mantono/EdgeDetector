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
	private final File imageFile;
	private final Color whiteBalance;
	private final BufferedImage imageData;
	private Point leftControlField;
	private Point rightControlField;
	private Point bottomControlField;

	public ImageReader(File file) throws IOException
	{
		this.imageFile = file;
		if(!file.canRead())
			throw new FileNotFoundException("File " + file + " can not be read");
		imageData = ImageIO.read(imageFile);
		whiteBalance = analyzeWhiteBalance();
		checkNoiseLevels();
	}
	
	public ImageReader(BufferedImage bufferedImage) throws IOException
	{
		this.imageFile = null;
		this.imageData = bufferedImage;
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
		Field whiteBalance = locateField(FieldType.WHITE_BALANCE);
		return whiteBalance.getAverageColor();
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

	//FIXME behövs verkligen denna metod? Den anropas aldrig (ännu).
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
	
	public double readAligment()
	{
		final Point[] controlFields = findControlFields();
		setControlFieldPositions(controlFields);
		Triangle triangle = new Triangle(leftControlField, rightControlField);
		return triangle.getBottomLeftAngle();
	}
	
	private void setControlFieldPositions(Point[] controlFields)
	{
		leftControlField = getTopLeftControlField(controlFields);
		rightControlField = getTopRightControlField(controlFields);
		bottomControlField = getBottomControlField(controlFields);		
	}

	private Point getBottomControlField(Point[] fields)
	{
		if(fields[0].y > fields[1].y && fields[0].y > fields[2].y)
			return fields[0];
		if(fields[1].y > fields[0].y && fields[1].y > fields[2].y)
			return fields[1];
		return fields[2];
	}

	private Point getTopRightControlField(Point[] fields)
	{
		if(fields[0].x > fields[1].x && fields[0].x > fields[2].x)
			return fields[0];
		if(fields[1].x > fields[0].x && fields[1].x > fields[2].x)
			return fields[1];
		return fields[2];
	}

	private Point getTopLeftControlField(Point[] fields)
	{
		if(fields[0].x < fields[1].x && fields[0].x < fields[2].x)
			return fields[0];
		if(fields[1].x < fields[0].x && fields[1].x < fields[2].x)
			return fields[1];
		return fields[2];
	}
	
	public Point[] findControlFields()
	{
		final Point[] controlFieldPositions = new Point[3];
		List<Point> pixelsFromFields = findRandomPixelInEachField(3, FieldType.CONTROL.getColor());
		controlFieldPositions[0] = findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(0)); 
		controlFieldPositions[1] = findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(1));
		controlFieldPositions[2] = findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(2));
		return controlFieldPositions;
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
			Color pixelColor = getColor(imageData.getRGB(x, y));
			if(hasColor(pixelColor, fieldColor, 25))
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
	
	public Field locateField(FieldType fieldType, double distanceRatioX, double distanceRatioY)
	{
		final int x = (int) (imageData.getWidth()*distanceRatioX);
		final int y = (int) (imageData.getHeight()*distanceRatioY);
		final Point fieldCenter = new Point(x, y);
		return findRestOfField(fieldType, fieldCenter);
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
			Color colorForCurrentPixel = getColor(imageData.getRGB(x, y));
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
		return new Field(fieldType, foundColors);
	}
	
	private Point findFirstPixelOfField(FieldType fieldType, Point point)
	{
		Point current = point;
		Point left = new Point(current.x-1, current.y);
		Point up = new Point(current.x, current.y-1);
		
		Color leftColor = getColor(imageData.getRGB(left.x, left.y));
		Color upColor = getColor(imageData.getRGB(up.x, up.y));
		
		final byte colorThreshold = fieldType.getThreshold();
		
		while(hasColor(leftColor, fieldType.getPermittedColors(), colorThreshold) || hasColor(upColor, fieldType.getPermittedColors(), colorThreshold))
		{
			if(hasColor(leftColor, fieldType.getPermittedColors(), colorThreshold))
				current = left;
			else
				current = up;
			
			left = new Point(current.x-1, current.y);
			up = new Point(current.x, current.y-1);
			leftColor = getColor(imageData.getRGB(left.x, left.y));
			upColor = getColor(imageData.getRGB(up.x, up.y));
		}

		return current;
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
	
	public static boolean hasColor(Color matchingColor, Color[] fieldColors, int threshold)
	{
		for(Color color : fieldColors)
		{
			if(hasColor(matchingColor, color, threshold))
				return true;
		}
		
		return false;
	}

	public Point getLeftControlField()
	{
		return leftControlField;
	}
	
	public Point getRightControlField()
	{
		return rightControlField;
	}
	
	public Point getBottomControlField()
	{
		return bottomControlField;
	}
}
