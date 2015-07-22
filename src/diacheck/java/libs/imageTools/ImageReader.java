package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
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
	private final File imageFile;
	private final Color whiteBalance;
	private final BufferedImage imageData;
	private final FieldFinder fields;

	public ImageReader(File file) throws IOException
	{
		this.imageFile = file;
		if(!file.canRead())
			throw new FileNotFoundException("File " + file + " can not be read");
		imageData = ImageIO.read(imageFile);
		fields = new FieldFinder(imageData);
		whiteBalance = analyzeWhiteBalance();
		checkBrightness();
		checkNoiseLevels();
	}
	
	public ImageReader(BufferedImage bufferedImage) throws IOException
	{
		this.imageFile = null;
		this.imageData = bufferedImage;
		fields = new FieldFinder(imageData);
		whiteBalance = analyzeWhiteBalance();
		checkNoiseLevels();
	}
	
	private void checkBrightness()
	{
		final int red = whiteBalance.getRed();
		final int green = whiteBalance.getGreen();
		final int blue = whiteBalance.getBlue();
		final int valueOfAllChannels = red + green + blue;
		
		final short min = 120;
		final short max = 230;
		
		if(valueOfAllChannels > 3*max)
			throw new WhiteBalanceException("Image too bright: Value of all color channels combined is over" + 3*max + ": " + valueOfAllChannels);
		if(valueOfAllChannels < 2*min)
			throw new WhiteBalanceException("Image too dark: Value of all color channels combined is less than " + 2*min + ": " + valueOfAllChannels);
		
		if(red < min || red > max)
			throw new WhiteBalanceException("Red channel is unbalanced: " + red);
		if(green < min || green > max)
			throw new WhiteBalanceException("Green channel is unbalanced: " + green);
		if(blue < min || blue > max)
			throw new WhiteBalanceException("Blue channel is unbalanced: " + blue);
		
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
		try
		{
			Field whiteBalance = fields.locateField(FieldType.WHITE_BALANCE);
			return whiteBalance.getAverageColor();
		}
		catch(IllegalArgumentException exception)
		{
			throw new WhiteBalanceException("Could not find white balance field. This can be because of bad color balance in image");
		}
	}

	public static Color getColor(int pixel)
	{
		return new Color(getRed(pixel), getGreen(pixel), getBlue(pixel));
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

	public Set<Point> findEdges() throws IOException
	{
		final int distance = 4;
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
		final int threshold = 35;
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
}
