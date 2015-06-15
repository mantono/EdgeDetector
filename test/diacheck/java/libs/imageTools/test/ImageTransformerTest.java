package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ImageReader;
import diacheck.java.libs.imageTools.ImageTransformer;

public class ImageTransformerTest
{
	public static String IMAGE_PATH = ImageReaderTest.IMAGE_PATH;
	public static String IMAGE_OUTPUT_PATH = "/tmp/";

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testImageTransformer()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRotate() throws IOException
	{
		File input, output;
		try
		{
			input = new File(IMAGE_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.jpg");
			output = new File(IMAGE_OUTPUT_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.png");
			ImageReader image = new ImageReader(input);
			float requiredRotation = image.readAligment();
			assertEquals(10.0f, requiredRotation, 0.8);
			ImageTransformer imageTransformed = new ImageTransformer(input);
			Point startOfFirstControlField = image.getStartOfFirstControlField();
			imageTransformed.rotate(startOfFirstControlField, requiredRotation);
			imageTransformed.saveToFile(output);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
