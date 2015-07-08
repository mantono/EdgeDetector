package diacheck.java.libs.imageTools;

import java.awt.Color;

public enum FieldType
{	
	WHITE_BALANCE(40, 0.1948, 0.6611, new Color(155, 155, 155)),
	CONTROL(25, 0.5, 0.5, new Color(151, 255, 0)),
	GLUCOSE(5, 0.5, 0.5, new Color(90, 90, 90)),
	KETONES(5, 0.5, 0.5, new Color(90, 90, 90)),
	PH(5, 0.5, 0.5, new Color(90, 90, 90)),
	PROTEIN(5, 0.5, 0.5, new Color(90, 90, 90)),
	SPECIFIC_GRAVITY(5, 0.5, 0.5, new Color(90, 90, 90));
	
	private final double xRatio;
	private final double yRatio;
	private final Color[] permittedValues;
	private final byte threshold;
	
	private FieldType(int threshold, double xRatio, double yRatio, Color... permittedColors)
	{
		this.xRatio = xRatio;
		this.yRatio = yRatio;
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
	
	public int getX(int imageWidth)
	{
		return (int) (imageWidth * xRatio);
	}
	
	public int getY(int imageHeight)
	{
		return (int) (imageHeight * yRatio);
	}
}
