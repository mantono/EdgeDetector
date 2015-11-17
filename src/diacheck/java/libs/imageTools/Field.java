package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.util.List;
import java.util.Arrays;

/**
 * Represents a field from the sample image, containing pixel data for entire
 * field.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class Field
{
	// private final BufferedImage imageData;
	private final Rectangle fieldBounds;
	//private final WhiteBalance whiteBalance;
	// private final Raster raster;
	private final int imageWidth;
	private final byte pixelByteSize;
	private final byte[] pixels;

	/**
	 * Public constructor for <code>Field</code>
	 * 
	 * @param type
	 *            describes which type of field the instance represents.
	 * @param raster
	 *            contains the color data for the fields pixels.
	 */
	public Field(BufferedImage imageData, Rectangle fieldBounds)
	{
		// this.imageData = imageData;
		this.fieldBounds = fieldBounds;
		//this.whiteBalance = whiteBalance;
		this.imageWidth = fieldBounds.width;
		// this.raster = imageData.getData(fieldBounds);
		if(imageData.getAlphaRaster() != null)
			pixelByteSize = 4;
		else
			pixelByteSize = 3;
		pixels = ((DataBufferByte) imageData.getData(fieldBounds).getDataBuffer()).getData();
		final int size = getAmountOfPixels();
		if(size == 0)
			throw new IllegalArgumentException("The amount of pixels must be at least one!");
	}

	/**
	 * 
	 * @return returns the size of the field measured in pixels.
	 */
	public int getAmountOfPixels()
	{
		return pixels.length/pixelByteSize;
	}
	
	/**
	 * 
	 * @return returns a <code>Color</code> object, representing the average
	 *         color of all pixels in the field.
	 */
	public Color getAverageColor()
	{
		

		int red, green, blue;
		red = green = blue = 0;
		final int lastX = (int) fieldBounds.getWidth();
		final int lastY = (int) fieldBounds.getHeight();
		for(int y = 0; y < lastY; y++)
		{
			for(int x = 0; x < lastX; x++)
			{
				final int pixel = getPixel(x, y); 
				red += ColorConverter.getRed(pixel);
				green += ColorConverter.getGreen(pixel);
				blue += ColorConverter.getBlue(pixel);
			}
		}
		red /= pixels.length;
		green /= pixels.length;
		blue /= pixels.length;

		return new Color(red, green, blue);
	}
	
	public int getPixel(final int x, final int y)
	{
		int index = (y * pixelByteSize * imageWidth) + (x * pixelByteSize);
		int pixel = 0xff_00_00_00;
		if(pixelByteSize == 4)
			pixel = ((int) pixels[index++] & 0xff) << 24;
		pixel += ((int) pixels[index++] & 0xff) << 16;
		pixel += ((int) pixels[index++] & 0xff) << 8;
		pixel += ((int) pixels[index] & 0xff);
		
		return pixel;
	}
	
	public int getPixel(final int i)
	{
		int index = (i * pixelByteSize);
		int pixel = 0xff_00_00_00;
		if(pixelByteSize == 4)
			pixel = ((int) pixels[index++] & 0xff) << 24;
		pixel += ((int) pixels[index++] & 0xff) << 16;
		pixel += ((int) pixels[index++] & 0xff) << 8;
		pixel += ((int) pixels[index] & 0xff);
		
		return pixel;
	}
}
