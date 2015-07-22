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

public class ControlFieldReader
{
	
	private Point leftControlField;
	private Point rightControlField;
	private Point bottomControlField;
	private final FieldFinder fields;
	
	public ControlFieldReader(BufferedImage imageData)
	{
		fields = new FieldFinder(imageData);
		findControlFields();
	}
	
	public double readAligment()
	{
		final Point[] controlFields = findControlFields();
		setControlFieldPositions(controlFields);
		Triangle triangle = new Triangle(leftControlField, rightControlField);
		return triangle.getBottomLeftAngle();
	}
	
	private void setControlFieldPositions(Point[] controlFields)
	{
		leftControlField = getTopLeftControlField(controlFields);
		rightControlField = getTopRightControlField(controlFields);
		bottomControlField = getBottomControlField(controlFields);		
	}

	private Point getBottomControlField(Point[] fields)
	{
		if(fields[0].y > fields[1].y && fields[0].y > fields[2].y)
			return fields[0];
		if(fields[1].y > fields[0].y && fields[1].y > fields[2].y)
			return fields[1];
		return fields[2];
	}

	private Point getTopRightControlField(Point[] fields)
	{
		if(fields[0].x > fields[1].x && fields[0].x > fields[2].x)
			return fields[0];
		if(fields[1].x > fields[0].x && fields[1].x > fields[2].x)
			return fields[1];
		return fields[2];
	}

	private Point getTopLeftControlField(Point[] fields)
	{
		if(fields[0].x < fields[1].x && fields[0].x < fields[2].x)
			return fields[0];
		if(fields[1].x < fields[0].x && fields[1].x < fields[2].x)
			return fields[1];
		return fields[2];
	}
	
	public Point[] findControlFields()
	{
		final Point[] controlFieldPositions = new Point[3];
		List<Point> pixelsFromFields = fields.findRandomPixelInEachField(3, FieldType.CONTROL.getColor());
		controlFieldPositions[0] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(0)); 
		controlFieldPositions[1] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(1));
		controlFieldPositions[2] = fields.findFirstPixelOfField(FieldType.CONTROL, pixelsFromFields.get(2));
		return controlFieldPositions;
	}
	
	public Point getLeftControlField()
	{
		return leftControlField;
	}
	
	public Point getRightControlField()
	{
		return rightControlField;
	}
	
	public Point getBottomControlField()
	{
		return bottomControlField;
	}
}
