package diacheck.java.libs.imageTools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author Anton &Ouml;sterberg
 */

public class ImageTransformer
{
	private BufferedImage imageData;
	private final File image;
	
	public ImageTransformer(File file) throws IOException
	{
		this.image = file;
		if(!file.canRead())
			throw new FileNotFoundException("File " + file + " can not be read");
		imageData = ImageIO.read(image);
	}
	
	public void rotate(final Point center, double radians) throws IOException
	{
		Graphics2D graphicsData = imageData.createGraphics();
		graphicsData.translate(-center.x+200, -center.y);
		//graphicsData.rotate(radians, imageData.getWidth(), imageData.getHeight());
		graphicsData.rotate(radians);
		//graphicsData.translate(center.x, center.y);
		graphicsData.drawImage(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), null);
		graphicsData.dispose();
	}
	
	public void removePixelsOutsideControlFields() throws IOException
	{
		ImageReader reader = new ImageReader(imageData);
		reader.readAligment();
		Point start = reader.getLeftControlField().getStart();
		Point upperRight = reader.getRightControlField().getEnd();
		final int width = upperRight.x - start.x;
		final int height = reader.getBottomControlField().getEnd().y - start.y;
		System.out.println(width);
		System.out.println(reader.getBottomControlField().getEnd().y);
		imageData = imageData.getSubimage(start.x, start.y, 2695, 1349);
	}
	
	
	
	public boolean saveToFile(final File filePath) throws IOException
	{
		if(!ImageReader.fileType(filePath).equals("png"))
			throw new IllegalArgumentException("File type must be \"png\" for file, but was " + ImageReader.fileType(filePath));
		return ImageIO.write(imageData, "PNG", filePath);
	}
	
	public boolean saveToFile() throws IOException
	{
		File newFile = new File(image.getAbsolutePath());
		if(!ImageReader.fileType(newFile).equals("png"))
			newFile = changeFileEndingToPNG(newFile);
		return ImageIO.write(imageData, "PNG", newFile);
	}

	private File changeFileEndingToPNG(File filePath)
	{
		final String filename = filePath.getPath();
		final String[] filenameSplitted = filename.split("\\.");
		final int fileTypeIndex = filenameSplitted.length - 1;
		filenameSplitted[fileTypeIndex] = "png";
		StringBuilder newFileName = new StringBuilder();
		for(String content : filenameSplitted)
			newFileName.append(content);
		return new File(newFileName.toString());
	}
}
