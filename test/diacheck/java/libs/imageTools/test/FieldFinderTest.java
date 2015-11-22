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
	private final static File input1 = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
	private final static File input2 = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped_increased_birghtness_and_contrast.png");
	private FieldFinder fieldFinder;

	@Before
	public void setUp() throws Exception
	{
		fieldFinder = new FieldFinder(ImageIO.read(input1));
	}

	@Test
	public void testFindRandomPixelsInControlField()
	{
		List<Point> pixelsInControlFields = fieldFinder.findRandomPixelInEachField(3, FieldType.CONTROL);
		assertEquals(3, pixelsInControlFields.size());
	}

	@Test
	public void testLocateWhiteBalanceField() throws IOException
	{
		assertTrue("Can't read file " + input1, input1.canRead());
		BufferedImage imageData = ImageIO.read(input1);
		FieldFinder fieldFinder = new FieldFinder(imageData);
		Field whiteBalanceField = fieldFinder.locateField(FieldType.WHITE_BALANCE);

		assertTrue(whiteBalanceField != null);
		assertTrue(whiteBalanceField.getAmountOfPixels() > 0);
		
		testFieldAgainstPermittedColors(FieldType.WHITE_BALANCE, input1);
	}

	@Test
	public void testLocateGlucoseField() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.GLUCOSE, input1);
	}

	@Test
	public void testLocateKetonesField() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.KETONES, input1);
	}

	@Test
	public void testLocatePHField() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.PH, input1);
	}

	@Test
	public void testLocateProteinField() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.PROTEIN, input1);
	}

	@Test
	public void testLocateSpecificGravityField() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.SPECIFIC_GRAVITY, input1);
	}
	
	@Test
	public void testLocateWhiteBalanceFieldIncreasedBrightnessAndContrast() throws IOException
	{
		assertTrue("Can't read file " + input2, input2.canRead());
		BufferedImage imageData = ImageIO.read(input2);
		FieldFinder fieldFinder = new FieldFinder(imageData);
		Field whiteBalanceField = fieldFinder.locateField(FieldType.WHITE_BALANCE);
		
		assertTrue(whiteBalanceField != null);
		assertTrue(whiteBalanceField.getAmountOfPixels() > 0);
		
		testFieldAgainstPermittedColors(FieldType.WHITE_BALANCE, input2);
	}
	
	@Test
	public void testLocateGlucoseFieldIncreasedBrightnessAndContrast() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.GLUCOSE, input2);
	}
	
	@Test
	public void testLocateKetonesFieldIncreasedBrightnessAndContrast() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.KETONES, input2);
	}
	
	@Test
	public void testLocatePHFieldIncreasedBrightnessAndContrast() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.PH, input2);
	}
	
	@Test
	public void testLocateProteinFieldIncreasedBrightnessAndContrast() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.PROTEIN, input2);
	}
	
	@Test
	public void testLocateSpecificGravityFieldIncreasedBrightnessAndContrast() throws IOException
	{
		testFieldAgainstPermittedColors(FieldType.SPECIFIC_GRAVITY, input2);
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

		final int differenceThreshold = 15;

		final String errorMessage = " channel is outside allowed threshold, difference is ";

		assertTrue("Red" + errorMessage + redDiff + " (" + expectedRed + ":" + foundRed + ")", redDiff <= differenceThreshold);
		assertTrue("Green" + errorMessage + greenDiff + " (" + expectedGreen + ":" + foundGreen + ")", greenDiff <= differenceThreshold);
		assertTrue("Blue" + errorMessage + blueDiff + " (" + expectedBlue + ":" + foundBlue + ")", blueDiff <= differenceThreshold);

	}

	public void testFieldAgainstPermittedColors(FieldType field, File image) throws IOException
	{
		assertTrue("Can't read file " + image, image.canRead());
		BufferedImage imageData = ImageIO.read(image);
		FieldFinder fieldFinder = new FieldFinder(imageData);
		Field fieldData = fieldFinder.locateField(field);

		assertTrue(fieldData != null);
		assertTrue(fieldData.getAmountOfPixels() > 500);
		
		final Color foundColor = fieldData.getAverageColor();
		assertTrue("Found color " + foundColor + " is not acceptable for this type of field.", field.hasColor(foundColor));
	}
}
