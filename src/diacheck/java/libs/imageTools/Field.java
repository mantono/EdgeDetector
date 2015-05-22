package diacheck.java.libs.imageTools;


import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Field
{
	private final Point startPosition;
	private final Point endPosition;
	private final List<Color> pixels;
	
	public Field(final Point start, final Point end, final List<Color> pixels)
	{
		this.startPosition = start;
		this.endPosition = end;
		this.pixels = pixels;
		if(getSize() < pixels.size())
			throw new IllegalArgumentException("The amount of pixels (" + pixels.size() + ") are greater than the given area allows ("+ getSize() +")");
	}

	public Field(final Point start, final Point end)
	{
		this.startPosition = start;
		this.endPosition = end;
		this.pixels = new ArrayList<Color>(getSize());
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
		Color[] pixelArray = new Color[pixels.size()];
		pixelArray = pixels.toArray(pixelArray);
		return ImageReader.getAverageColorForData(pixelArray);
	}
}
