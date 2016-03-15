package imagetools;

import java.awt.Color;

public class ColorConverter
{
	public final static int REDMASK = 0xff0000;
	public final static int GREENMASK = 0x00ff00;
	public final static int BLUEMASK = 0x0000ff;
	
	/**
	 * Converts an <code>int</code> representation of a colour to a
	 * <code>Colour</code> object that contains values for the three color
	 * channels red, green and blue.
	 * 
	 * @param pixel
	 *            an <code>int</code> representing the colour of a pixel.
	 * @return returns a <code>Colour</code> object.
	 */
	public static Color getColor(int pixel)
	{
		return new Color(getRed(pixel), getGreen(pixel), getBlue(pixel));
	}

	public static int getRed(final int pixel)
	{
		return (pixel & REDMASK) >> 16;
	}

	public static int getGreen(final int pixel)
	{
		return (pixel & GREENMASK) >> 8;
	}

	public static int getBlue(final int pixel)
	{
		return pixel & BLUEMASK;
	}
	
	public static short getAverageBrightnessForPixel(final int color)
	{
		int maxValue = getRed(color);
			maxValue += getGreen(color);
			maxValue += getBlue(color);
		
		return (short) (maxValue/3);
	}
}
