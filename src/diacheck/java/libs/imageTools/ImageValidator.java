package diacheck.java.libs.imageTools;

import java.awt.image.BufferedImage;

/**
 * This class purpose is to check the quality and validity of an image sample, concerning the image quality without regard to medical data.
 * The variables being checked are noise and exposure level.
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
	 * @return returns a double value between 0 and 1, where 0 indicateS no noise at all and 1 the maximum amount of possible noise. 
	 */
	public double getNoiseLevel()
	{
		return 0;
	}
	
	/**
	 * 
	 * @return returns a <code>double</code> value between 0 and 1, where 0 indicates no overexposed pixels and 1 that all pixels in the image are overexposed.
	 */
	public double getOverexposureRatio()
	{
		return 0;
	}
	
	/**
	 * 
	 * @return returns a <code>double</code> value between 0 and 1, where 0 indicates no underexposed pixels and 1 that all pixels in the image are underexposed.
	 */
	public double getUnderexposureRatio()
	{
		return 0;
	}
	
	/**
	 * 
	 * @return returns the average exposure for the pixels in the image, ranging from 0 to 255.
	 */
	public float getAverageExposure()
	{
		return 0;
	}
}
