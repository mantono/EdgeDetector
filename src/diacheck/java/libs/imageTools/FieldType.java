package diacheck.java.libs.imageTools;

import java.awt.Color;

public enum FieldType
{	
	WHITE_BALANCE(40, new Color(155, 155, 155)),
	CONTROL(25, new Color(151, 255, 0)),
	GLUCOSE(5, new Color(90, 90, 90)),
	KETONES(5, new Color(90, 90, 90)),
	PH(5, new Color(90, 90, 90)),
	PROTEIN(5, new Color(90, 90, 90)),
	SPECIFIC_GRAVITY(5, new Color(90, 90, 90));
	
	private final Color[] permittedValues;
	private final byte threshold;
	
	private FieldType(int threshold, Color... permittedColors)
	{
		this.permittedValues = permittedColors;
		this.threshold = (byte) threshold;
	}
	
	public Color[] getPermittedColors()
	{
		return permittedValues;
	}
	
	public Color getColor()
	{
		return permittedValues[0];
	}
	
	public byte getThreshold()
	{
		return threshold;
	}
}
