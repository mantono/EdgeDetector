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
	public void justCrunchAllImagesForComparison() throws IOException
	{
		findEdgesWithDifferentAlgorithmForFile(new File("test/images/edgeTest.png"));
		findEdgesWithDifferentAlgorithmForFile(new File("test/images/edgeTest.png"));
		findEdgesWithDifferentAlgorithmForFile(new File("test/images/GOOD_horizontal_and_cropped.png"));
	}

	private void findEdgesWithDifferentAlgorithmForFile(final File file) throws IOException
	{
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		Set<Point> edges1 = edgeFinder.findEdges();
		Set<Point> edges2 = edgeFinder.findSobelEdges();
		colorEdgesWithOverlay(file, edges1, edges2);
	}

	@Test
	public void testFindEdgesOnRealPhoto1() throws IOException
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
	public void testFindEdgesOnRealPhoto1WithSobelOperator() throws IOException
	{
		final File file = new File("test/images/edgeTest.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findSobelEdges();
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
	public void testFindEdgesOnBlackAndWhiteWithSobelOperator() throws IOException
	{
		final File file = new File("test/images/ImageValidator/black_and_white.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findSobelEdges();

		assertTrue(edges.contains(new Point(200, 289)));
		assertFalse(edges.contains(new Point(100, 289)));
	}

	@Test
	public void testFindEdgesOnRealPhoto2() throws IOException
	{
		final File file = new File("test/images/GOOD_horizontal_and_cropped.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findEdges();

		assertTrue(edges.contains(new Point(1851, 607)));

		assertFalse(edges.contains(new Point(284, 116)));
		assertFalse(edges.contains(new Point(1858, 638)));
		assertFalse(edges.contains(new Point(1976, 648)));
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
			imageData.setRGB(edge.x, edge.y, ColorConverter.BLUEMASK);
		File blueImage = new File("/tmp/" + image.getName() + "_blue.png");
		ImageIO.write(imageData, "png", blueImage);
	}

	private void colorEdgesWithOverlay(File image, Set<Point> edges1, Set<Point> edges2) throws IOException
	{
		BufferedImage imageData = ImageIO.read(image);
		for(Point edge : edges1)
			imageData.setRGB(edge.x, edge.y, ColorConverter.REDMASK);
		for(Point edge : edges2)
		{
			if(edges1.contains(edge))
				imageData.setRGB(edge.x, edge.y, ColorConverter.GREENMASK);
			else
				imageData.setRGB(edge.x, edge.y, ColorConverter.BLUEMASK);
		}
		File blueImage = new File("/tmp/" + image.getName() + "_combined.png");
		ImageIO.write(imageData, "png", blueImage);
	}

	@Test
	public void testIsEdge()
	{
		fail("Not yet implemented");
	}

}
