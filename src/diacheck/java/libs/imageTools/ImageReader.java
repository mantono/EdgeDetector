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

import diacheck.java.libs.analytes.Analyte;
import diacheck.java.libs.analytes.Glucose;

/**
 * 
 * @author Anton &Ouml;sterberg
 * 
 * The <code>ImageReader</code> class is responsible for managing and analyzing all the vital data that in the image. During initiation of an instance it will check that the source image
 * has a noise level within the threshold value and that the white balance is not too skewed. An unacceptable image will result in an error in form of either a <code>WhiteBalanceException</code>
 * or a <code>HighNoiseException</code>.
 */

public class ImageReader
{
	private final File imageFile;
	private final BufferedImage imageData;
	private final FieldFinder fields;
	private final WhiteBalance whiteBalance;

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
		
		ImageValidator qualityControl = new ImageValidator(imageData);
		if(qualityControl.getNoiseLevel() > 0.013)
			throw new HighNoiseException("Noise level is too high: " + qualityControl.getNoiseLevel());
		if(qualityControl.getOverexposureRatio() > 0.02)
			throw new BadExposureException(qualityControl.getOverexposureRatio());
		if(qualityControl.getUnderexposureRatio() > 0.02)
			throw new BadExposureException(qualityControl.getUnderexposureRatio());
		
		fields = new FieldFinder(imageData);
		this.whiteBalance = new WhiteBalance(analyzeWhiteBalance());
		whiteBalance.isWithinBounds();
	}
	
	private Color analyzeWhiteBalance()
	{
		try
		{
			return fields.locateField(FieldType.WHITE_BALANCE).getAverageColor();
		}
		catch(IllegalArgumentException exception)
		{
			throw new WhiteBalanceException("Could not find white balance field. This can be because of bad color balance in image");
		}
	}
	
	public Analyte createAnalyte(FieldType fieldType)
	{
		switch(fieldType)
		{
			case GLUCOSE: return new Glucose(fields.locateField(fieldType));
			case KETONES: return new Glucose(fields.locateField(fieldType));
			case PH: return new Glucose(fields.locateField(fieldType));
			case PROTEIN: return new Glucose(fields.locateField(fieldType));
			case SPECIFIC_GRAVITY: return new Glucose(fields.locateField(fieldType));
			default: return null;
		}
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
}
