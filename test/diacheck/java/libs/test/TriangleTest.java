package diacheck.java.libs.test;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.Triangle;

public class TriangleTest
{
	private Triangle triangle;

	@Before
	public void setUp() throws Exception
	{
		Point a = new Point(0, 10);
		Point b = new Point(5, 0);
		triangle = new Triangle(a, b);
	}

	@Test
	public void testGetBottomLeftAngle()
	{
		assertEquals(63.43495, triangle.getBottomLeftAngle(), 0.001); 
	}

	@Test
	public void testGetTopRightAngle()
	{
		assertEquals(26.56505, triangle.getTopRightAngle(), 0.001);
	}
	
	@Test
	public void testGetCorrectSum()
	{
		final double sumOfAllAngles = triangle.getBottomLeftAngle() + triangle.getTopRightAngle() + 90;
		assertEquals(180, sumOfAllAngles, 0.01);
	}

	@Test
	public void testGetHypotenuse()
	{
		assertEquals(11.180334, triangle.getHypotenuse(), 0.00001);
	}

	@Test
	public void testGetRightSide()
	{
		assertEquals(10, triangle.getRightSide(), 0.000001);
	}

	@Test
	public void testGetBottomSide()
	{
		assertEquals(5, triangle.getBottomSide(), 0.00001);
	}

}
