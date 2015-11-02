package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Each FieldType describes the permitted color input for each type of field found in a sample image. It also has their position relative to the control fields within the image.
 * @author Anton &Ouml;sterberg
 *
 */
public enum FieldType
{
	WHITE_BALANCE(50, 0.53962, 0.68495),
	CONTROL(25, 0.5, 0.5),
	GLUCOSE(30, 0.75075, 0.49887),
	KETONES(40,	0.9446, 0.485714),
	PH(75, 0.67251, 0.484962),
	PROTEIN(90, 0.606241, 0.483459),
	SPECIFIC_GRAVITY(40, 0.80575, 0.48722);

	private final double xRatio;
	private final double yRatio;
	private final Set<Color> permittedValues;
	private final short threshold;

	private FieldType(int threshold, double xRatio, double yRatio, Color... permittedColors)
	{
		this.xRatio = xRatio;
		this.yRatio = yRatio;
		this.permittedValues = new HashSet<Color>(Arrays.asList(permittedColors));
		if(threshold < 0 || threshold > 255)
			throw new IllegalArgumentException("Allowed range for parameter threshold is 0 to 255");
		this.threshold = (short) threshold;
	}

	private FieldType(int threshold, double xRatio, double yRatio)
	{
		this.xRatio = xRatio;
		this.yRatio = yRatio;

		permittedValues = readPermittdFieldColorsFromFile();
		if(threshold < 0 || threshold > 255)
			throw new IllegalArgumentException("Allowed range for parameter threshold is 0 to 255");
		this.threshold = (short) threshold;
	}

	private Set<Color> readPermittdFieldColorsFromFile()
	{
		final String file = "permittedColors/" + this.toString() + ".csv";
		final Set<Color> colors = new HashSet<Color>();

		try
		{
			final URL permittedColorsFilePath = getClass().getResource(file);
			final File permittedColorsFile = new File(permittedColorsFilePath.getPath());
			if(!permittedColorsFile.canRead())
				throw new FileNotFoundException("Can't read file " + permittedColorsFile);
			BufferedReader reader = new BufferedReader(new FileReader(permittedColorsFile));
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] colorData = line.split(",");
				checkDataFormat(colorData);
				colors.add(createColor(colorData));
			}
			reader.close();
		}
		catch(NullPointerException | FileNotFoundException exception)
		{
			System.err.println("Could not find permitted colors file for FieldType " + this.toString());
			exception.printStackTrace();
		}
		catch(IOException exception)
		{
			exception.printStackTrace();
		}
		return colors;
	}

	private void checkDataFormat(String[] colorData)
	{
		if(colorData.length != 3)
			throw new IllegalInputDataException("Field should contain three values, but found " + colorData.length);
		final int red = getColorChannelFromString(colorData[0]);
		final int green = getColorChannelFromString(colorData[1]);
		final int blue = getColorChannelFromString(colorData[2]);

		final String errorMessage = " channel contains a value that is out of bounds: ";
		if(red < 0 || red > 255)
			throw new IllegalInputDataException("Red" + errorMessage + red);
		if(green < 0 || green > 255)
			throw new IllegalInputDataException("Green" + errorMessage + green);
		if(blue < 0 || blue > 255)
			throw new IllegalInputDataException("Blue" + errorMessage + blue);
	}

	private Color createColor(String[] colorData)
	{
		final int red = getColorChannelFromString(colorData[0]);
		final int green = getColorChannelFromString(colorData[1]);
		final int blue = getColorChannelFromString(colorData[2]);
		return new Color(red, green, blue);
	}
	
	private int getColorChannelFromString(String colorChannel)
	{
		return Integer.valueOf(colorChannel.trim());
	}
	
	/**
	 * Method to verify whether the input <code>Color</color> matches any of those allowed for the current <code>FieldType</code>.
	 * 
	 * @param matchingColor - color to be checked if it falls within the allowed parameters.
	 * @return returns true if it is a permitted color, else false.
	 */
	public boolean hasColor(Color matchingColor)
	{
		for(Color color : permittedValues)
			if(isWithinThreshold(matchingColor, color))
				return true;
		
		return false;
	}
	
	private boolean isWithinThreshold(Color matchingColor, Color fieldColor)
	{
		final int diffRed = Math.abs(matchingColor.getRed() - fieldColor.getRed());
		if(diffRed > threshold)
			return false;
		
		final int diffGreen = Math.abs(matchingColor.getGreen() - fieldColor.getGreen());
		if(diffGreen > threshold)
			return false;
		
		final int diffBlue = Math.abs(matchingColor.getBlue() - fieldColor.getBlue());
		if(diffBlue > threshold)
			return false;
		
		return true;
	}

	/**
	 * 
	 * @return returns the permitted colors for the particular FieldType.
	 */
	private  Set<Color> getPermittedColors()
	{
		return permittedValues;
	}

	private Color getColor()
	{
		return permittedValues.iterator().next();
	}

	/**
	 * 
	 * @return returns the threshold for how much a color is allowed to deviate from any of the permitted colors.
	 */
	private short getThreshold()
	{
		return threshold;
	}

	/**
	 * 
	 * @param imageWidth - the width of the sample image after it has been cropped.
	 * @return returns the absolute X coordinate for where the field is supposed to start. 
	 */
	public int getX(int imageWidth)
	{
		return (int) (imageWidth * xRatio);
	}

	/**
	 * 
	 * @param imageHeight - the height of the sample image after it has been cropped.
	 * @return returns the absolute Y coordinate for where the field is supposed to start. 
	 */
	public int getY(int imageHeight)
	{
		return (int) (imageHeight * yRatio);
	}
}
