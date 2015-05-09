package diacheck.libs.test;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import diacheck.libs.ImageReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ImageReaderTest
{

	public void setUp() throws Exception
	{
	}

	@Test
	public void testColorAnalysisOfFocuedImageWithFlashOnTealAreaAndSyntheticGreyArea()
			throws IOException
	{		
		File input = new File("images/flash_sharp1_with_fake_grey_area.png");
		ImageReader image = new ImageReader(input);
		
		final Point upperLeftCorner = new Point(1093, 1091);
		final Point lowerRightCorner = new Point(1212, 1212);

		int averageColor = image.getAverageColorForCoordinates(upperLeftCorner, lowerRightCorner);

		/*
		 * Det förväntade resultatet är beräknat efter manuell uträkning på 14
		 * olika punkter i mittersta stickan för värdet på ljusblått fält
		 */
		final int expectedResult = Color.rgb(89, 141, 143);

		final int redDiff = Math.abs(Color.red(expectedResult) - Color.red(averageColor));
		final int greenDiff = Math.abs(Color.green(expectedResult) - Color.green(averageColor));
		final int blueDiff = Math.abs(Color.blue(expectedResult) - Color.blue(averageColor));

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
		final Point edgePixel = new Point(423, 1450);
		ImageReader image = new ImageReader(new File("images/flash_sharp1_with_fake_grey_area.png"));
		Set<Point> edges = image.findEdges();
		assertTrue(edges.contains(edgePixel));
		
		makeEdgesBlue(new File("images/flash_sharp1_with_fake_grey_area.png"), edges);
	}
	
	private void makeEdgesBlue(File image, Set<Point> edges) throws IOException
	{
		Bitmap imageData = BitmapFactory.decodeFile(image.getAbsolutePath());
		for(Point edge:edges)
		{
			imageData.setPixel(edge.x, edge.y, 0x0000ff);
		}
		File blueImage = new File(image.getAbsoluteFile() + "blue.png");
        imageData.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(blueImage));
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
