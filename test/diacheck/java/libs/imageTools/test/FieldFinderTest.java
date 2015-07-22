package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.FieldFinder;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;

public class FieldFinderTest
{
	private final static File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
	private FieldFinder fieldFinder;

	@Before
	public void setUp() throws Exception
	{
		fieldFinder = new FieldFinder(ImageIO.read(input));
	}

	@Test
	public void test()
	{
		fail("Not yet implemented");
	}
	
	@Test
	public void testFindFirstPixelOfFieldForWhiteBalance() throws IOException
	{
		final Point foundPixel = fieldFinder.findFirstPixelOfField(FieldType.WHITE_BALANCE);
		assertEquals(new Point(498, 862), foundPixel);
	}
	
	@Test
	public void testFindRandomPixelsInControlField()
	{
		List<Point> pixelsInControlFields = fieldFinder.findRandomPixelInEachField(3, FieldType.CONTROL.getColor());
		assertEquals(3, pixelsInControlFields.size());
	}
	
	@Test
	public void testLocateWhiteBalanceField() throws IOException
	{
		final File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
		assertTrue("Can't read file " + input, input.canRead());
		BufferedImage imageData = ImageIO.read(input);
		FieldFinder fieldFinder = new FieldFinder(imageData);
		Field whiteBalanceField = fieldFinder.locateField(FieldType.WHITE_BALANCE);
		
		assertTrue(whiteBalanceField != null);
		assertTrue(whiteBalanceField.getAmountOfPixels() > 0);
		assertEquals(new Color(146, 146, 146), whiteBalanceField.getAverageColor());
	}
	
	@Test
	public void testLocateGlucoseField() throws IOException
	{
		testField(FieldType.GLUCOSE, new Color(155, 140, 49));
	}
	
	@Test
	public void testLocateKetonesField() throws IOException
	{
		testField(FieldType.KETONES, new Color(122, 107, 126));
	}
	
	@Test
	public void testLocatePHField() throws IOException
	{
		testField(FieldType.PH, new Color(0, 0, 0));
	}
	
	@Test
	public void testLocateProteinField() throws IOException
	{
		testField(FieldType.PROTEIN, new Color(0, 0, 0));
	}
	
	@Test
	public void testLocateSpecificGravityField() throws IOException
	{
		testField(FieldType.SPECIFIC_GRAVITY, new Color(0, 0, 0));
	}
	
	public void testField(FieldType field, Color expectedColor) throws IOException
	{
		final File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
		assertTrue("Can't read file " + input, input.canRead());
		BufferedImage imageData = ImageIO.read(input);
		FieldFinder fieldFinder = new FieldFinder(imageData);
		Field fieldData = fieldFinder.locateField(field);
		
		assertTrue(fieldData != null);
		assertTrue(fieldData.getAmountOfPixels() > 0);
		//assertEquals(expectedColor, whiteBalanceField.getAverageColor());
		final Color foundColor = fieldData.getAverageColor();
		
		final int expectedRed = expectedColor.getRed();
		final int expectedGreen = expectedColor.getGreen();
		final int expectedBlue = expectedColor.getBlue();
		
		final int foundRed = foundColor.getRed();
		final int foundGreen = foundColor.getGreen();
		final int foundBlue = foundColor.getBlue();
		
		final int redDiff = Math.abs(foundRed - expectedRed);
		final int greenDiff = Math.abs(foundGreen - expectedGreen);
		final int blueDiff = Math.abs(foundBlue - expectedBlue);
		
		final int differenceThreshold = 5;
		
		final String errorMessage = " channel is outside allowed threshold, difference is ";
		
		assertTrue("Red" + errorMessage + redDiff + " (" + expectedRed + ":" + foundRed +")", redDiff <= differenceThreshold);
		assertTrue("Green" + errorMessage + greenDiff + " (" + expectedGreen + ":" + foundGreen +")", greenDiff <= differenceThreshold);
		assertTrue("Blue" + errorMessage + blueDiff + " (" + expectedBlue + ":" + foundBlue +")", blueDiff <= differenceThreshold);
		
	}

}
