package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ControlFieldReader;
import diacheck.java.libs.imageTools.IllegalInputDataException;
import diacheck.java.libs.imageTools.ImageReader;
import diacheck.java.libs.imageTools.ImageTransformer;

public class IntergrationTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void test() throws IOException
	{
		final File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD_with_rotation.png");
		final File rotated = new File("/tmp/rotated.png");
		assertTrue("Can't read file " + input, input.canRead());
		
		BufferedImage imageData = ImageIO.read(input);
		ControlFieldReader cfReader = new ControlFieldReader(imageData);
		assertEquals(0.1221730476, cfReader.readAligment(), 0.01);
		
		final Point leftFirst = cfReader.getLeftControlField();
		final Point rightFirst = cfReader.getRightControlField();
		final Point bottomFirst = cfReader.getBottomControlField();

		
		ImageTransformer imageTransformer = new ImageTransformer(input);
		imageTransformer.rotate(leftFirst, cfReader.readAligment());
		imageTransformer.removePixelsOutsideControlFields();
		imageTransformer.saveToFile(rotated);
		
		imageData = ImageIO.read(rotated);

		//checkControlFieldPositions(imageData, left, right, bottom);
		
		ImageReader image = new ImageReader(rotated);
	}
	
	private void checkControlFieldPositions(BufferedImage imageData, Point leftControlField, Point rightControlField, Point bottomControlField)
	{
		final int imageWidth = imageData.getWidth();
		final int imageHeight = imageData.getHeight();

		final double leftPercentX = ((double)leftControlField.x)/imageWidth;
		final double leftPercentY = ((double)leftControlField.y)/imageHeight;
		
		assertFalse("Left control field is to far from the upper left corner of the image.", leftPercentX > 0.05 || leftPercentY > 0.05);
		
		final double rightPercentX = ((double)rightControlField.x)/imageWidth;
		final double rightPercentY = ((double)rightControlField.y)/imageHeight;
		
		assertFalse("Right control field is to far from the upper right corner of the image.", rightPercentX < 0.95 || rightPercentY > 0.05);

		final double bottomPercentX = ((double)bottomControlField.x)/imageWidth;
		final double bottomPercentY = ((double)bottomControlField.y)/imageHeight;
		
		assertFalse("Bottom control field is to far from the center of the bottom edge of the image.", bottomPercentX < 0.45 || bottomPercentX > 0.55 || bottomPercentY < 0.95);
	}

}
