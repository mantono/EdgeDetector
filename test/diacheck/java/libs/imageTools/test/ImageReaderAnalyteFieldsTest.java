package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;

public class ImageReaderAnalyteFieldsTest
{

	@Before
	public void setUp() throws Exception
	{
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
		final File input = new File(ImageReaderTest.IMAGE_PATH + "flash_sharp2_with_no_rotation_and_fake_control_fields_cropped_with_increased_brightness_and_contrast.png");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);
		Field fieldData = image.locateField(field);
		
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
