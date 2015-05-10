package diacheck.libs;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Bitmap;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ImageReader
{
	private final File image;
	private final int whiteBalance;

	public ImageReader(File file) throws IOException
	{
		this.image = file;
		whiteBalance = analyzeWhiteBalance();
	}

	private int analyzeWhiteBalance() throws IOException
	{
		int[] whiteBalanceSamples = collectWhiteBalanceData();
		return getAverageColorForData(whiteBalanceSamples);
	}

	private int[] collectWhiteBalanceData() throws IOException
	{
		return pickColorSamples(new Point(429, 1428), new Point(443, 1442));
	}

	public int getAverageColorForCoordinates(Point start, Point end)
			throws IOException
	{
		int[] colorSamples = pickColorSamples(start, end);
		int averageColor = getAverageColorForData(colorSamples);
		return whiteBalanceCompensation(averageColor);
	}

	private int[] pickColorSamples(Point start, Point end) throws IOException
	{
		final int size = calculateSampleSize(start, end);
		int[] samples = new int[size];
		BitmapFactory.Options imageOptions = new BitmapFactory.Options();
		imageOptions.outWidth = 200;
		imageOptions.outHeight = 200;
        Bitmap imageData = BitmapFactory.decodeFile(image.getAbsolutePath(), imageOptions);
        assert imageOptions.outWidth == 200;

		int xStart, xStop, yStart, yStop;
		if(start.x < end.x)
		{
			xStart = start.x;
			xStop = end.x;
		}
		else
		{
			xStart = end.x;
			xStop = start.x;
		}
		if(start.y < end.y)
		{
			yStart = start.y;
			yStop = end.y;
		}
		else
		{
			yStart = end.y;
			yStop = start.y;
		}

		int sizeCount = 0;
		for(int y = yStart; y < yStop; y += 5)
		{
			for(int x = xStart; x < xStop; x += 5)
			{
				try
				{
					int pixel = imageData.getPixel(x, y);
					assert sizeCount < size : "Going out of bounds with " + x + " and " + y;
					samples[sizeCount++] = pixel;
				}
				catch(IllegalArgumentException exception)
				{
					throw new IllegalArgumentException("x: " + x + " (size " + imageData.getWidth() + ")\ny: " + y + " (size " + imageData.getHeight() + ")");
				}
			}
		}
		return samples;
	}

	public static int calculateSampleSize(Point start, Point end)
	{
		int x = 1 + Math.abs(start.x - end.x) / 5;
		int y = 1 + Math.abs(start.y - end.y) / 5;
		return x * y;
	}

	private int whiteBalanceCompensation(int averageColor)
	{
		final int redGreenDiff = Color.green(whiteBalance) - Color.red(whiteBalance);
		final int blueGreenDiff = Color.green(whiteBalance) - Color.blue(whiteBalance);

		return Color.rgb(Color.red(averageColor) + redGreenDiff, Color.green(averageColor), Color.blue(averageColor) + blueGreenDiff);
	}

	public int getAverageColorForData(int[] colorSamples)
	{
		int red, green, blue;
		red = green = blue = 0;
		for(Integer sample : colorSamples)
		{
			red += Color.red(sample);
			green += Color.green(sample);
			blue += Color.blue(sample);
		}
		red /= colorSamples.length;
		green /= colorSamples.length;
		blue /= colorSamples.length;

		return Color.rgb(red, green, blue);
	}

	public Set<Point> findEdges() throws IOException
	{
		final int distance = 6;
		Set<Point> edges = new HashSet<Point>();
        Bitmap imageData = BitmapFactory.decodeFile(image.getAbsolutePath());
		for(int y = distance; y < imageData.getHeight(); y += 2)
		{
			for(int x = distance; x < imageData.getWidth(); x += 2)
			{
				int currentPixel = imageData.getPixel(x, y);
				int previousPixel = imageData.getPixel(x-distance,  y);
				if(isEdge(currentPixel, previousPixel))
					edges.add(new Point(x-distance/2, y));
				previousPixel = imageData.getPixel(x, y-distance);
				if(isEdge(currentPixel, previousPixel))
					edges.add(new Point(x, y-distance/2));
			}
		}
		fillGaps(edges);
		return edges;
	}

	private void fillGaps(Set<Point> edges)
	{
		Set<Point> gaps = new HashSet<Point>();
		for(Point pixel : edges)
		{
			if(edges.contains(new Point(pixel.x, pixel.y-2)))
				gaps.add(new Point(pixel.x, pixel.y-1));
			if(edges.contains(new Point(pixel.x-2, pixel.y)))
				gaps.add(new Point(pixel.x-1, pixel.y));
			if(edges.contains(new Point(pixel.x-2, pixel.y-2)))
				gaps.add(new Point(pixel.x-1, pixel.y-1));		
		}
		edges.addAll(gaps);
	}

	public static boolean isEdge(int pixel1, int pixel2)
	{
		final int threshold = 45;
		final int diffRed = Math.abs(Color.red(pixel1) - Color.red(pixel2));
		if(diffRed > threshold)
			return true;
		
		final int diffGreen = Math.abs(Color.green(pixel1) - Color.green(pixel2));
		if(diffGreen > threshold)
			return true;
		
		final int diffBlue = Math.abs(Color.blue(pixel1) - Color.blue(pixel2));
		if(diffBlue > threshold)
			return true;
		
		if(diffRed + diffGreen + diffBlue > threshold*2)
			return true;
		
		return false;
	}
}
