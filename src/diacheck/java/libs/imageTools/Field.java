package diacheck.java.libs.imageTools;


import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Field
{
	private final List<Color> pixels;
	private final FieldType fieldtype;
	
	public Field(FieldType type, final List<Color> pixels)
	{
		this.pixels = pixels;
		this.fieldtype = type;
		if(pixels.size() == 0)
			throw new IllegalArgumentException("The amount of pixels must be at least one!");
	}
	
	@SuppressWarnings("unchecked")
	public Field(FieldType type, final Color[] pixels)
	{
		this(type, (List<Color>) Arrays.asList(pixels));
	}
	
	public int getAmountOfPixels()
	{
		return pixels.size();
	}
	
	public FieldType getFieldType()
	{
		return this.fieldtype;
	}

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
