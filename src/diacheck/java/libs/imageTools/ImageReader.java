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
 * 
 * The <code>ImageReader</code> class is responsible for managing and analyzing all the vital data that in the image. During initiation of an instance it will check that the source image
 * has a noise level with the threshold value and that the white balance is not too skewed. An unacceptable image will result in an error in form of either <code>WhiteBalanceException</code>
 * or a <code>HighNoiseException</code>.
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

	/**
	 * 
	 * @param file takes a <code>File</code> object as parameter. The file must have read access.
	 * @throws IOException if file cannot be read.
	 */
	public ImageReader(File file) throws IOException
	{
		this.imageFile = file;
		if(!file.canRead())
			throw new FileNotFoundException("File " + file + " can not be read");
		imageData = ImageIO.read(imageFile);
		fields = new FieldFinder(imageData);
		whiteBalance = fields.getWhiteBalance();
		checkBrightness();
		checkNoiseLevels();
	}
	
	/**
	 * 
	 * @param bufferedImage
	 */
	public ImageReader(BufferedImage bufferedImage)
	{
		this.imageFile = null;
		this.imageData = bufferedImage;
		fields = new FieldFinder(imageData);
		whiteBalance = fields.getWhiteBalance();
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
	
	/**
	 * @see diacheck.java.libs.imageTools.FieldFinder#locateField(FieldType)
	 * @param fieldType the <code>FieldType</code> that the method should search for.
	 * @return returns an instance of that field created from data located in the image.
	 */
	public Field getField(FieldType fieldType)
	{
		return fields.locateField(fieldType);
	}

	/**
	 * Converts an <code>int</code> representation of a colour to a <code>Colour</code> object that contains values for the three color channels red, green and blue.
	 * @param pixel an <code>int</code> representing the colour of a pixel.
	 * @return returns a <code>Colour</code> object.
	 */
	public static Color getColor(int pixel)
	{
		return new Color(getRed(pixel), getGreen(pixel), getBlue(pixel));
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
}
