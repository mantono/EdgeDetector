package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;
import diacheck.java.libs.imageTools.HighNoiseException;

public class ImageReaderTest
{
	public static String IMAGE_PATH = "test/images/";

	@Before
	public void setUp() throws Exception
	{
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testNonExistingFile() throws IOException
	{
		new ImageReader(new File("not here"));
	}

		
	@Test(expected=HighNoiseException.class)
	public void testHighNoiseException() throws IOException
	{
		new ImageReader(new File(IMAGE_PATH + "no_flash_grey_card_high_noise.jpg"));
	}

	@Test
	public void testCalculateSampleSizeTest()
	{
		Point start = new Point(0, 0);
		Point end = new Point(50, 50);
		int size = ImageReader.calculateSampleSize(start, end);
		assertEquals(121, size);

		start = new Point(100, 100);
		end = new Point(120, 120);
		size = ImageReader.calculateSampleSize(start, end);
		assertEquals(25, size);
	}
	
	@Test
	public void testFindEdges() throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp1_with_fake_grey_area.png");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);
		
		final Point edgePixel = new Point(423, 1450);
		Set<Point> edges = image.findEdges();
		assertTrue(edges.contains(edgePixel));
		
		makeEdgesBlue(new File(IMAGE_PATH + "flash_sharp1_with_fake_grey_area.png"), edges);
	}
	
	private void makeEdgesBlue(File image, Set<Point> edges) throws IOException
	{
		BufferedImage imageData = ImageIO.read(image);
		for(Point edge:edges)
		{
			imageData.setRGB(edge.x, edge.y, ImageReader.BLUEMASK);
		}
		File blueImage = new File(image.getAbsoluteFile() + "blue.png");
		ImageIO.write(imageData, "png", blueImage);
	}

	@Test
	public void testIsEdge()
	{
		final int red = 0xff0000;
		final int green = 0x00ff00;
		final int blue = 0x0000ff;
		assertTrue(ImageReader.isEdge(red, green));
		assertTrue(ImageReader.isEdge(red, blue));
		assertTrue(ImageReader.isEdge(blue, green));
		assertFalse(ImageReader.isEdge(blue, blue));
		
		final int blue1 = 0x88CAFA; // 136 202 250
		final int blue2 = 0x88CACD; // 136 202 205
		final int blue3 = 0x88CACC; // 136 202 204
		final int blueGrey = 0xC5CAD7; // 197 202 215
		assertFalse(ImageReader.isEdge(blue1, blue2)); // diff == 45
		assertTrue(ImageReader.isEdge(blue1, blue3)); // diff == 46
		assertTrue(ImageReader.isEdge(blue1, blueGrey));
	}
	
	@Test
	public void testFindAllControlFields() throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.jpg");
		ImageReader image = new ImageReader(input);
		List<Point> pixelsInControlFields = image.findRandomPixelInEachField(3, FieldType.CONTROL.getColor());
		assertEquals(3, pixelsInControlFields.size());
		
	}
	
	@Test
	public void readAligment() throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.jpg");
		ImageReader image = new ImageReader(input);
		assertEquals(0.1884033450, image.readAligment(), 0.01);
	}
	
	@Test
	public void testCheckFiletype()
	{
		File file = new File("/home/test/long/path/with.dots/test.jpg");
		assertEquals("jpg", ImageReader.fileType(file));
		file = new File("TEST.PNG");
		assertEquals("png", ImageReader.fileType(file));
	}
	
	@Test
	public void testLocateField() throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp2_with_no_rotation_and_fake_control_fields_cropped.png");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);
		Field whiteBalanceField = image.locateField(FieldType.WHITE_BALANCE, 0.1948, 0.6611);
		
		assertTrue(whiteBalanceField != null);
		assertTrue(whiteBalanceField.getAmountOfPixels() > 0);
	}

}
