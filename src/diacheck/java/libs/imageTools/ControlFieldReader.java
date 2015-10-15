package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import diacheck.java.libs.imageTools.FieldFinder;
import diacheck.java.libs.Triangle;
/**
 * ControlFieldReader is created solely for the purpose of finding control fields in a sample image and read the position and orientation of those fields.
 * @author Anton &Ouml;sterberg
 *
 */
public class ControlFieldReader
{
	
	private Point leftControlField;
	private Point rightControlField;
	private Point bottomControlField;
	private final FieldFinder fields;
	
	/**
	 * 
	 * @param imageData Takes a <code>BufferedImage</code> instance of the sample image.
	 */
	public ControlFieldReader(BufferedImage imageData)
	{
		fields = new FieldFinder(imageData);
		findControlFields();
	}
	
	/**
	 * Reads the alignment of the image.
	 * @return returns the alignment of the image in radians, where a positive value indicates a clockwise rotation and negative value counter clockwise rotation.
	 */
	public double readAligment()
	{
		final Point[] controlFields = findControlFields();
		setControlFieldPositions(controlFields);
		Triangle triangle = new Triangle(leftControlField, rightControlField);
		return triangle.getBottomLeftAngle();
	}
	
	/**
	 * Sets the position of each of the three control fields, represented by a <code>Point</code> for each field. 
	 * @param controlFields - an array containing the three control fields, but in an unknown order.
	 */
	private void setControlFieldPositions(Point[] controlFields)
	{
		leftControlField = getTopLeftControlField(controlFields);
		rightControlField = getTopRightControlField(controlFields);
		bottomControlField = getBottomControlField(controlFields);		
	}

	/**
	 * 
	 * @param fields - an array containing the three control fields, but in an unknown order.
	 * @return returns the bottom control field from the array.
	 */
	private Point getBottomControlField(Point[] fields)
	{
		if(fields[0].y > fields[1].y && fields[0].y > fields[2].y)
			return fields[0];
		if(fields[1].y > fields[0].y && fields[1].y > fields[2].y)
			return fields[1];
		return fields[2];
	}

	/**
	 * 
	 * @param fields - an array containing the three control fields, but in an unknown order.
	 * @return returns the right control field from the array.
	 */
	private Point getTopRightControlField(Point[] fields)
	{
		if(fields[0].x > fields[1].x && fields[0].x > fields[2].x)
			return fields[0];
		if(fields[1].x > fields[0].x && fields[1].x > fields[2].x)
			return fields[1];
		return fields[2];
	}

	/**
	 * 
	 * @param fields - an array containing the three control fields, but in an unknown order.
	 * @return returns the left control field from the array.
	 */
	private Point getTopLeftControlField(Point[] fields)
	{
		if(fields[0].x < fields[1].x && fields[0].x < fields[2].x)
			return fields[0];
		if(fields[1].x < fields[0].x && fields[1].x < fields[2].x)
			return fields[1];
		return fields[2];
	}
	
	/**
	 * Finds all three control fields, but without knowing which field is which.
	 * @return returns an array of three <code>Point</code> objects, each one representing the position of a control field.
	 */
	public Point[] findControlFields()
	{
		final Point[] controlFieldPositions = new Point[3];
		List<Point> pixelsFromFields = fields.findRandomPixelInEachField(3, FieldType.CONTROL);
		controlFieldPositions[0] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(0)); 
		controlFieldPositions[1] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(1));
		controlFieldPositions[2] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(2));
		return controlFieldPositions;
	}
	
	/**
	 * 
	 * @return returns the position of the left control field.
	 */
	public Point getLeftControlField()
	{
		return leftControlField;
	}
	
	/**
	 * 
	 * @return returns the position of the right control field.
	 */
	public Point getRightControlField()
	{
		return rightControlField;
	}
	
	/**
	 * 
	 * @return returns the position of the bottom control field.
	 */
	public Point getBottomControlField()
	{
		return bottomControlField;
	}
}
