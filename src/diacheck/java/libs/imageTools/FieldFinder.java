package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;

/**
 * Fieldfinder is a class for locating specific data fields within an image. Image data is passed to the constructor as a <code>BufferedImage</code> object, and the search
 * for the field is initiated through the method <code>locateField()</code>
 * 
 * @author Anton &Ouml;sterberg
 * 
 */

public class FieldFinder
{
	private final BufferedImage imageData;
	private final Set<Point> edges;
	public final static int RIGHT = 1;
	public final static int DOWN = 1;
	public final static int LEFT = -1;
	public final static int UP = -1;
	
	public FieldFinder(BufferedImage image) throws IOException
	{
		this.imageData = image;
		this.edges = new EdgeDetector(image).findEdges();
	}
	
	
	
	public Field locateField(FieldType fieldType)
	{
		final int x = fieldType.getX(imageData.getWidth());
		final int y = fieldType.getY(imageData.getHeight());
		final Point fieldCenter = new Point(x, y);
		return findRestOfField(fieldType, fieldCenter);
	}
	
	private Field findRestOfField(FieldType fieldType, Point point)
	{
		Point start = findEdgeOfField(point, UP);
		Point end = findEdgeOfField(point, DOWN);
		
		Dimension dimension = new Dimension(end.x - start.x, end.y - start.y);
		Rectangle fieldArea = new Rectangle(start, dimension);
	
		return new Field(imageData, fieldArea);
	}

	public Point findEdgeOfField(Point start, final int direction)
	{
		Point current, previous;
		current = previous = start;
		
		while(current != null)
		{	
			if(hasReachedEdgeOfImage(current))
				throw new IllegalStateException("Reached end of image when looking for first pixel of field.");
			
			previous = current;
			current = nextPixel(current, direction);
		}
		
		return previous;
	}
	
	private Point nextPixel(Point current, final int direction)
	{
		Point[] pixels = new Point[7];
		
		if(direction == LEFT)
			pixels[0] = new Point(current.x+LEFT, current.y);
		else
			pixels[0] = new Point(current.x+RIGHT, current.y);
		pixels[1] = new Point(current.x+LEFT, current.y+direction);
		pixels[2] = new Point(current.x, current.y+direction);
		pixels[3] = new Point(current.x+RIGHT, current.y+direction);
		pixels[4] = new Point(current.x+LEFT, current.y+direction*2);
		pixels[5] = new Point(current.x, current.y+direction*2);
		pixels[6] = new Point(current.x+RIGHT, current.y+direction*2);

		for(int i = 0; i < pixels.length; i++)
			if(!edges.contains(pixels[i]))
				return pixels[i];
		
		return null;
	}

	public List<Point> findRandomPixelInEachField(int numberOfFields, FieldType fieldType)
	{
		Set<Point> checkedPixels = new HashSet<Point>();
		List<Point> fields = new ArrayList<Point>(numberOfFields);
		
		final int width = imageData.getWidth();
		final int height = imageData.getHeight();
		final int imageResolution = width*height;
		final int expectedFieldSize = imageResolution/50000;
		
		int x = 0;
		int y = 0;
		int increment = imageResolution/500;
		
		//TODO gör om denna för concurrent?
		
		while(fields.size() < numberOfFields && checkedPixels.size() < imageResolution)
		{
			Point currentPixel = new Point(x, y);
			checkedPixels.add(currentPixel);
			Color pixelColor = ColorConverter.getColor(imageData.getRGB(x, y));
			if(fieldType.hasColor(pixelColor))
			{
				boolean pixelBelongsToAlreadyFoundField = false;
				for(Point pixel : fields)
					if(Point.distance(currentPixel.x, currentPixel.y, pixel.x, pixel.y) < expectedFieldSize)
						pixelBelongsToAlreadyFoundField = true;
				if(!pixelBelongsToAlreadyFoundField)
					fields.add(currentPixel);
			}
			
			x += increment;
			if(x >= width)
			{
				x %= width;
				y++;
			}
			if(y >= height)
			{
				y = 0;
				increment--;
			}
		}
		
		return fields;
	}

	private boolean hasReachedEdgeOfImage(Point current)
	{
		return current.x == 0 || current.y == 0;
	}
}
