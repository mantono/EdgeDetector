package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.Field;
import diacheck.java.libs.imageTools.ImageReader;

public class ImageReaderTest
{
	public static String IMAGE_PATH = "test/images/";

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testColorAnalysisOfFocuedImageWithFlashOnTealAreaAndSyntheticGreyArea()
			throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp1_with_fake_grey_area.png");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);
		
		final Point upperLeftCorner = new Point(1093, 1091);
		final Point lowerRightCorner = new Point(1212, 1212);
		Color averageColor = image.getAverageColorForCoordinates(upperLeftCorner, lowerRightCorner);

		/*
		 * Det förväntade resultatet är beräknat efter manuell uträkning på 14
		 * olika punkter i mittersta stickan för värdet på ljusblått fält
		 */
		final Color expectedResult = new Color(89, 141, 143);

		final int redDiff = Math.abs(expectedResult.getRed() - averageColor.getRed());
		final int greenDiff = Math.abs(expectedResult.getGreen() - averageColor.getGreen());
		final int blueDiff = Math.abs(expectedResult.getBlue() - averageColor.getBlue());

		assertTrue(redDiff < 2);
		assertTrue(greenDiff < 2);
		assertTrue(blueDiff < 2);
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
	public void testFindControlFields() throws IOException
	{
		final File input = new File(IMAGE_PATH + "flash_sharp2_with_fake_rotation_and_fake_control_fields.jpg");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);	
		Field[] fields = image.findControlFields();
	}

}
