package imagetools;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import imagetools.*;

public class EdgeDetectorTest
{

	@Before
	public void setUp() throws Exception
	{
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
	public void testFindEdgesOnBlackAndWhiteWithSobelOperator() throws IOException
	{
		final File file = new File("test/images/black_and_white.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findEdges();

		assertTrue(edges.contains(new Point(200, 289)));
		assertFalse(edges.contains(new Point(100, 289)));
	}

	@Test
	public void testFindEdgesOnRealPhoto2() throws IOException
	{
		final File file = new File("test/images/horizontal_and_cropped.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findEdges();

		assertTrue(edges.contains(new Point(1851, 607)));

		assertFalse(edges.contains(new Point(1858, 638)));
		assertFalse(edges.contains(new Point(1976, 648)));
	}
	
	@Test
	public void testFindEdgesOnRealPhoto3() throws IOException
	{
		final File file = new File("test/images/edgeTest2.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		final Set<Point> edges = edgeFinder.findEdges();
		
		assertTrue(edges.contains(new Point(34, 125)));
		assertTrue(edges.contains(new Point(170, 92)));
		assertTrue(edges.contains(new Point(220, 87)));
		assertTrue(edges.contains(new Point(355, 120)));
		assertTrue(edges.contains(new Point(416, 120)));
		assertTrue(edges.contains(new Point(550, 100)));
		assertTrue(edges.contains(new Point(602, 100)));
		assertTrue(edges.contains(new Point(737, 100)));
		assertTrue(edges.contains(new Point(795, 100)));
		assertTrue(edges.contains(new Point(982, 100)));
		assertTrue(edges.contains(new Point(1116, 100)));
		assertTrue(edges.contains(new Point(1175, 100)));
		assertTrue(edges.contains(new Point(1314, 145)));
		assertTrue(edges.contains(new Point(1370, 145)));
		assertTrue(edges.contains(new Point(1507, 145)));
		assertTrue(edges.contains(new Point(1562, 145)));
		assertTrue(edges.contains(new Point(1707, 145)));
		assertTrue(edges.contains(new Point(1761, 145)));
		assertTrue(edges.contains(new Point(1761, 145)));
		assertTrue(edges.contains(new Point(1901, 145)));
		
		assertFalse(edges.contains(new Point(101, 121)));
		assertFalse(edges.contains(new Point(287, 124)));
		assertFalse(edges.contains(new Point(482, 124)));
		assertFalse(edges.contains(new Point(663, 124)));
		assertFalse(edges.contains(new Point(855, 124)));
		assertFalse(edges.contains(new Point(1051, 124)));
		assertFalse(edges.contains(new Point(1242, 124)));
		assertFalse(edges.contains(new Point(1439, 124)));
		assertFalse(edges.contains(new Point(1633, 126)));
		assertFalse(edges.contains(new Point(1827, 128)));
	}

	@Test
	public void testCalculateContrastOnFullContrast1() throws IOException
	{
		final File file = new File("test/images/black_and_white_max_noise.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(1f, edgeFinder.calculateContrast(), 0.0001);
	}

	@Test
	public void testCalculateContrastOnFullContrast2() throws IOException
	{
		final File file = new File("test/images/black_and_white.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(1f, edgeFinder.calculateContrast(), 0.0001);
	}

	@Test
	public void testCalculateContrastOnNoContrastBlack() throws IOException
	{
		final File file = new File("test/images/black_zero_noise.png");
		BufferedImage image = ImageIO.read(file);
		EdgeDetector edgeFinder = new EdgeDetector(image);
		assertEquals(0f, edgeFinder.calculateContrast(), 0.0001);
	}

	@Test
	public void testCalculateContrastOnNoContrastBlue() throws IOException
	{
		final File file = new File("test/images/blue_zero_noise.png");
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
}
