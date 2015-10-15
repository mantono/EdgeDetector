package diacheck.java.libs.imageTools;


import java.awt.Color;
import java.util.List;
import java.util.Arrays;

/**
 * Represents a field from the sample image, containing pixel data for entire field. 
 * @author Anton &Ouml;sterberg
 *
 */
public class Field
{
	private final List<Color> pixels;
	private final FieldType fieldtype;
	
	/**
	 * Public constructor for <code>Field</code>
	 * 
	 * @param type describes which type of field the instance represents.
	 * @param pixels contains the color data for the fields pixels.
	 */
	public Field(FieldType type, final List<Color> pixels)
	{
		this.pixels = pixels;
		this.fieldtype = type;
		if(pixels.size() == 0)
			throw new IllegalArgumentException("The amount of pixels must be at least one!");
	}
	
	/**
	 * Public constructor for <code>Field</code>
	 * 
	 * @param type describes which type of field the instance represents.
	 * @param pixels contains the color data for the fields pixels.
	 */
	public Field(FieldType type, final Color[] pixels)
	{
		this(type, (List<Color>) Arrays.asList(pixels));
	}
	
	/**
	 * 
	 * @return returns the size of the field measured in pixels.
	 */
	public int getAmountOfPixels()
	{
		return pixels.size();
	}
	
	/**
	 * 
	 * @return returns the <code>FieldType</code> representing the instance of the field.
	 */
	public FieldType getFieldType()
	{
		return this.fieldtype;
	}

	/**
	 * 
	 * @return returns a <code>Color</code> object, representing the average color of all pixels in the field.
	 */
	public Color getAverageColor()
	{
		int red, green, blue;
		red = green = blue = 0;
		for(Color sample : pixels)
		{
			red += sample.getRed();
			green += sample.getGreen();
			blue += sample.getBlue();
		}
		red /= pixels.size();
		green /= pixels.size();
		blue /= pixels.size();

		return new Color(red, green, blue);
	}
}
