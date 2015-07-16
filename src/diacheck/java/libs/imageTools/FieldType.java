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

public enum FieldType
{
	WHITE_BALANCE(50, 0.1948, 0.6611, new Color(155, 155, 155), new Color(175, 175, 175), new Color(195, 195, 195), new Color(205, 205, 205), new Color(225, 225, 225)),
	CONTROL(25, 0.5, 0.5, new Color(151, 255, 0)),
	GLUCOSE(20, 0.75075, 0.49887, new Color(150, 137, 56), new Color(149, 135, 62), new Color(158, 144, 55)),
	KETONES(35, 0.9708214, 0.049887, new Color(254, 250, 202), new Color(254, 221, 188), new Color(223, 179, 196), new Color(177, 87, 141), new Color(81, 0, 77)),
	PH(5, 0.5, 0.5, new Color(90, 90, 90)),
	PROTEIN(5, 0.5, 0.5, new Color(90, 90, 90)),
	SPECIFIC_GRAVITY(5, 0.5, 0.5, new Color(90, 90, 90));

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

	private FieldType(int threshold, double xRatio, double yRatio) throws IOException
	{
		this.xRatio = xRatio;
		this.yRatio = yRatio;

		permittedValues = readPermittdFieldColors();
		if(threshold < 0 || threshold > 255)
			throw new IllegalArgumentException("Allowed range for parameter threshold is 0 to 255");
		this.threshold = (short) threshold;
	}

	private Set<Color> readPermittdFieldColors() throws IOException
	{
		final URL permittedColorsFilePath = getClass().getResource("permittedColors/" + this.toString() + ".csv");
		final File permittedColorsFile = new File(permittedColorsFilePath.getPath());
		final Set<Color> colors = new HashSet<Color>();

		if (permittedColorsFile.canRead())

			throw new FileNotFoundException("Can't read file " + permittedColorsFile);
		BufferedReader reader = new BufferedReader(new FileReader(permittedColorsFile));
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] colorData = line.split(",");
				checkDataFormat(colorData);
				colors.add(createColor(colorData));
			}
		}
		finally
		{
			reader.close();
		}
		return colors;
	}

	private void checkDataFormat(String[] colorData)
	{
		if (colorData.length != 3)
			throw new IllegalInputDataException("Field should contain three values, but found " + colorData.length);
		final int red = Integer.valueOf(colorData[0]);
		final int green = Integer.valueOf(colorData[1]);
		final int blue = Integer.valueOf(colorData[2]);

		final String errorMessage = " channel contains a value that is out of bounds: ";
		if (red < 0 || red > 255)
			throw new IllegalInputDataException("Red" + errorMessage + red);
		if (green < 0 || green > 255)
			throw new IllegalInputDataException("Green" + errorMessage + green);
		if (blue < 0 || blue > 255)
			throw new IllegalInputDataException("Blue" + errorMessage + blue);
	}

	private Color createColor(String[] colorData)
	{
		final int red = Integer.valueOf(colorData[0]);
		final int green = Integer.valueOf(colorData[1]);
		final int blue = Integer.valueOf(colorData[2]);
		return new Color(red, green, blue);
	}

	public Set<Color> getPermittedColors()
	{
		return permittedValues;
	}

	public Color getColor()
	{
		return permittedValues.iterator().next();
	}

	public short getThreshold()
	{
		return threshold;
	}

	public int getX(int imageWidth)
	{
		return (int) (imageWidth * xRatio);
	}

	public int getY(int imageHeight)
	{
		return (int) (imageHeight * yRatio);
	}
}
