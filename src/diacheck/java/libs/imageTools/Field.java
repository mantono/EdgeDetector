package diacheck.java.libs.imageTools;


import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Field
{
	private final Point startPosition;
	private final Point endPosition;
	private final Set<Color> pixels;
	
	public Field(final Point start, final Point end, final Set<Color> pixels)
	{
		this.startPosition = start;
		this.endPosition = end;
		this.pixels = pixels;
		if(getSize() < pixels.size())
			throw new IllegalArgumentException("The amount of pixels (" + pixels.size() + ") are greater than the given area allows ("+ getSize() +")");
	}

	public Field(final Point start, final Point end)
	{
		this(start, end, new HashSet<Color>());
	}
	
	public int getSize()
	{
		final int height = endPosition.y - startPosition.y;
		final int width = endPosition.x - startPosition.x;
		return width * height;
	}
	
	public int getAmountOfPixels()
	{
		return pixels.size();
	}
	
	public Color getAverageColor()
	{
		return ImageReader.getAverageColorForData(pixels);
	}
}
