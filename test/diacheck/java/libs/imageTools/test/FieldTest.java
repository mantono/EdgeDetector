package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.Field;

public class FieldTest
{
	private Field field;
	private final static Point START = new Point(4, 10);
	private final static Point END = new Point(12, 18);

	@Before
	public void setUp() throws Exception
	{
		Set<Color> pixels = new HashSet<Color>();
		final Color first = new Color(12, 0, 0);
		final Color second = new Color(0, 12, 0);
		final Color third = new Color(0, 0, 12);
		pixels.add(first);
		pixels.add(second);
		pixels.add(third);
		this.field = new Field(START, END);
	}

	@Test
	public void testFieldPointPointSetOfColor()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testFieldPointPoint()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetSize()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetAverageColor()
	{
		final Color predictedAverageColor = new Color(4, 4, 4);
		assertEquals(predictedAverageColor, field.getAverageColor());
	}

}
