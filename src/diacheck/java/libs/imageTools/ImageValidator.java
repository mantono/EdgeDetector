package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This class purpose is to check the quality and validity of an image sample,
 * concerning the image quality without regard to medical data. The variables
 * being checked are noise and exposure level.
 * 
 * @author Anton &Ouml;sterberg
 */
public class ImageValidator
{
	private final BufferedImage sample;

	public ImageValidator(final BufferedImage imageData)
	{
		this.sample = imageData;
	}

	/**
	 * 
	 * @return a double value between 0 and 1, where 0 indicates no noise at all
	 *         and 1 the maximum amount of possible noise.
	 */
	public double getNoiseLevel()
	{
		final int width = sample.getWidth();
		final int height = sample.getHeight();
		double noise = 0;
		int checkedPixels = 0;

		for(int y = 1; y < width; y += 5)
		{
			for(int x = y % 4 + 1; x < height; x += 5)
			{
				if(!outOfBounds(x + 1, y + 1))
				{

					noise += getNoiseLevelForPixelCluster(x, y);
					checkedPixels++;
				}
			}
		}

		return ((double) noise) / checkedPixels;
	}

	private double getNoiseLevelForPixelCluster(int x, int y)
	{
		double diffSum = 0;
		int[] cluster = new int[9];
		cluster[0] = sample.getRGB(x - 1, y - 1);
		cluster[1] = sample.getRGB(x, y - 1);
		cluster[2] = sample.getRGB(x + 1, y - 1);
		cluster[3] = sample.getRGB(x + 1, y);
		cluster[4] = sample.getRGB(x, y);
		cluster[5] = sample.getRGB(x - 1, y);
		cluster[6] = sample.getRGB(x - 1, y + 1);
		cluster[7] = sample.getRGB(x, y + 1);
		cluster[8] = sample.getRGB(x + 1, y + 1);
		Color previousPixel = ColorConverter.getColor(cluster[0]);
		for(int i = 1; i < 9; i++)
		{
			// TODO this may be optimized by using bit manipulation and not
			// class Color.
			Color currentPixel = ColorConverter.getColor(cluster[i]);

			diffSum += getChannelsDifference(currentPixel, previousPixel) / (255 * 3);
			previousPixel = currentPixel;
		}

		return diffSum / 8;
	}

	private float getChannelsDifference(Color currentPixel, Color previousPixel)
	{
		final int currentRed = currentPixel.getRed();
		final int currentGreen = currentPixel.getGreen();
		final int currentBlue = currentPixel.getBlue();

		final int previousRed = previousPixel.getRed();
		final int previousGreen = previousPixel.getGreen();
		final int previousBlue = previousPixel.getBlue();

		final int diffRed = Math.abs(currentRed - previousRed);
		final int diffGreen = Math.abs(currentGreen - previousGreen);
		final int diffBlue = Math.abs(currentBlue - previousBlue);

		return diffRed + diffGreen + diffBlue;
	}

	/**
	 * 
	 * @return a <code>double</code> value between 0 and 1, where 0 indicates no
	 *         overexposed pixels and 1 that all pixels in the image are
	 *         overexposed.
	 */
	public double getOverexposureRatio()
	{
		final short overexposedThreshold = 253;

		final int width = sample.getWidth();
		final int height = sample.getHeight();
		int overexposedPixels = 0;
		int checkedPixels = 0;

		for(int y = 0; y < height; y++)
		{
			for(int x = y % 4; x < width; x += 4)
			{
				int currentPixel = 0;
				try
				{
				if(outOfBounds(x, y))
					currentPixel = sample.getRGB(width-1, y);
				else
					currentPixel = sample.getRGB(x, y);
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					throw new ArrayIndexOutOfBoundsException(e.getMessage() + "\nx: " + x + ", image width: " + width + "\ny: " + y + ", image height: " + height);
				}

				if(getExposure(currentPixel) >= overexposedThreshold)
					overexposedPixels++;
				checkedPixels++;
			}
		}

		return ((double) overexposedPixels) / checkedPixels;
	}

	/**
	 * 
	 * @return a <code>double</code> value between 0 and 1, where 0 indicates no
	 *         underexposed pixels and 1 that all pixels in the image are
	 *         underexposed.
	 */
	public double getUnderexposureRatio()
	{
		final short underexposedThreshold = 6;

		final int width = sample.getWidth();
		final int height = sample.getHeight();
		int underexposedPixels = 0;
		int checkedPixels = 0;

		for(int y = 0; y < height; y++)
		{
			for(int x = y % 4; x < width; x += 4)
			{
				int currentPixel = 0;
				if(outOfBounds(x, y))
					currentPixel = sample.getRGB(width, y);
				else
					currentPixel = sample.getRGB(x, y);

				if(getExposure(currentPixel) <= underexposedThreshold)
					underexposedPixels++;
				checkedPixels++;
			}
		}

		return ((double) underexposedPixels) / checkedPixels;
	}

	/**
	 * 
	 * @return the average exposure for the pixels in the image, ranging from 0
	 *         to 255.
	 */
	public float getAverageExposure()
	{
		final int width = sample.getWidth();
		final int height = sample.getHeight();
		double totalExposure = 0;
		int checkedPixels = 0;

		for(int y = 0; y < height; y++)
		{
			for(int x = y % 4; x < width; x += 4)
			{
				final int currentPixel = getCurrentPixel(x, y);

				totalExposure += getExposure(currentPixel);
				checkedPixels++;
			}
		}

		return (float) (totalExposure / checkedPixels);
	}

	private int getCurrentPixel(int x, int y)
	{
		try
		{
			if(outOfBounds(x, y))
				return sample.getRGB(sample.getWidth() - 1, y);
			return sample.getRGB(x, y);
		}
		catch(ArrayIndexOutOfBoundsException exception)
		{
			throw new ArrayIndexOutOfBoundsException(exception.getMessage() + " (" + x + ", " + y + ")");
		}
	}

	private float getExposure(int currentPixel)
	{
		Color color = ColorConverter.getColor(currentPixel);
		int pixelExposure = color.getRed();
		pixelExposure += color.getGreen();
		pixelExposure += color.getBlue();
		return pixelExposure / 3;
	}

	private boolean outOfBounds(int x, int y)
	{
		return x >= sample.getWidth() || y >= sample.getHeight();
	}
}
