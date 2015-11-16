package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ColorConverter;
import diacheck.java.libs.imageTools.EdgeDetector;
import diacheck.java.libs.imageTools.ImageReader;

public class EdgeDetectorTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testFindEdgesOnRealPhoto() throws IOException
	{
		final File file = new File("test/images/edgeTest.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findEdges();
		makeEdgesBlue(file, edges);

		assertTrue(edges.contains(new Point(212, 107)));
		assertTrue(edges.contains(new Point(211, 95)));
		assertTrue(edges.contains(new Point(160, 99)));
		assertTrue(edges.contains(new Point(777, 140)));

		assertFalse(edges.contains(new Point(645, 125)));
		assertFalse(edges.contains(new Point(617, 126)));
		assertFalse(edges.contains(new Point(284, 116)));
	}
	
	@Test
	public void testCalculateContrastOnFullContrast1() throws IOException
	{
		final File file = new File("test/images/ImageValidator/black_and_white_max_noise.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(1f, edgeFinder.calculateContrast(), 0.0001);
	}
	
	@Test
	public void testCalculateContrastOnFullContrast2() throws IOException
	{
		final File file = new File("test/images/ImageValidator/black_and_white.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(1f, edgeFinder.calculateContrast(), 0.0001);
	}
	
	@Test
	public void testCalculateContrastOnNoContrastBlack() throws IOException
	{
		final File file = new File("test/images/ImageValidator/black_zero_noise.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(0f, edgeFinder.calculateContrast(), 0.0001);
	}
	
	@Test
	public void testCalculateContrastOnNoContrastBlue() throws IOException
	{
		final File file = new File("test/images/ImageValidator/blue_zero_noise.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(0f, edgeFinder.calculateContrast(), 0.0001);
	}

	private void makeEdgesBlue(File image, Set<Point> edges) throws IOException
	{
		BufferedImage imageData = ImageIO.read(image);
		for(Point edge : edges)
			imageData.setRGB(edge.x, edge.y, ColorConverter.REDMASK);
		File blueImage = new File("/tmp/" + image.getName() + "blue.png");
		ImageIO.write(imageData, "png", blueImage);
	}

	@Test
	public void testIsEdge()
	{
		fail("Not yet implemented");
	}

}
