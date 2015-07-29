package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.FieldType;

public class FieldTypeTest
{

	@Before
	public void setUp() throws Exception
	{
	}
	
	private void checkColor(FieldType field, Color... colors)
	{
		for(Color color : colors)
			assertTrue("Missing color " + color, field.getPermittedColors().contains(color));
	}

	@Test
	public void testGlucose()
	{
		checkColor(FieldType.GLUCOSE,
				new Color(150, 137, 56),
				new Color(149, 135, 62),
				new Color(158, 144, 55));
		assertEquals(20, FieldType.GLUCOSE.getThreshold());
	}

	@Test
	public void testWhiteBalance()
	{
		checkColor(FieldType.WHITE_BALANCE,
				new Color(155, 155, 155),
				new Color(175, 175, 175),
				new Color(195, 195, 195),
				new Color(205, 205, 205),
				new Color(225, 225, 225));
		assertEquals(50, FieldType.WHITE_BALANCE.getThreshold());
	}

	@Test
	public void testControlField()
	{
		checkColor(FieldType.CONTROL, new Color(151, 255, 0));
		assertEquals(25, FieldType.CONTROL.getThreshold());
	}

	@Test
	public void testKetones()
	{
		checkColor(FieldType.KETONES,
				new Color(254, 250, 202),
				new Color(254, 221, 188),
				new Color(223, 179, 196),
				new Color(177, 87, 141),
				new Color(110, 95, 114),
				new Color(81, 0, 77));
		assertEquals(35, FieldType.KETONES.getThreshold());
	}

	@Test
	public void testPH()
	{
		checkColor(FieldType.PH, new Color(169, 91, 43));
		assertEquals(25, FieldType.PH.getThreshold());
	}

	@Test
	public void testProtein()
	{
		checkColor(FieldType.PROTEIN, new Color(114, 98, 36), new Color(95, 87, 30));
		assertEquals(35, FieldType.PROTEIN.getThreshold());
	}

	@Test
	public void testSpecificGravity()
	{
		checkColor(FieldType.SPECIFIC_GRAVITY,
				new Color(8, 9, 55),
				new Color(5, 19, 19),
				new Color(19, 72, 4),
				new Color(83, 97, 9),
				new Color(137, 104, 1),
				new Color(202, 157, 30),
				new Color(224, 148, 52));
		assertEquals(20, FieldType.SPECIFIC_GRAVITY.getThreshold());
	}

}
