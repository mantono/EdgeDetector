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
import diacheck.java.libs.imageTools.ImageTransformer;

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
	public void testFindEdges() throws IOException
	{
		final File input = new File(IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
		assertTrue("Can't read file " + input, input.canRead());
		ImageReader image = new ImageReader(input);
		
		Set<Point> edges = image.findEdges();
		makeEdgesBlue(input, edges);
		
		assertTrue(edges.contains(new Point(1518, 895)));
		assertTrue(edges.contains(new Point(1549, 932)));
		assertTrue(edges.contains(new Point(1301, 707)));
		assertTrue(edges.contains(new Point(1964, 580)));
		assertFalse(edges.contains(new Point(1537, 912)));
		assertFalse(edges.contains(new Point(1498, 653)));
		assertFalse(edges.contains(new Point(1891, 644)));
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
}
