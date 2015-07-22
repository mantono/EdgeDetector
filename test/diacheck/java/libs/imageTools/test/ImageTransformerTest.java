package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.fest.util.Files;
import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ControlFieldReader;
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
			if(output.canRead())
				Files.delete(output);
			assertFalse(output.canRead());
			ControlFieldReader image = new ControlFieldReader(ImageIO.read(input));
			double requiredRotation = image.readAligment();
			ImageTransformer imageTransformed = new ImageTransformer(input);
			Point startOfFirstControlField = image.getLeftControlField();
			imageTransformed.rotate(startOfFirstControlField, requiredRotation);
			imageTransformed.saveToFile(output);
			assertTrue(output.canRead());
			assertTrue(output.length() > 0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRotateAndCrop() throws IOException
	{
		File input, output;
		try
		{
			input = new File(IMAGE_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.jpg");
			output = new File(IMAGE_OUTPUT_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.png");
			ControlFieldReader image = new ControlFieldReader(ImageIO.read(input));
			double requiredRotation = image.readAligment();
			ImageTransformer imageTransformed = new ImageTransformer(input);
			Point startOfFirstControlField = image.getLeftControlField();
			imageTransformed.rotate(startOfFirstControlField, requiredRotation);
			imageTransformed.saveToFile(output);
			
			imageTransformed.removePixelsOutsideControlFields();
			imageTransformed.saveToFile(output);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
