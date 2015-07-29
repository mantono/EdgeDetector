package diacheck.java.libs.imageTools;

import java.awt.Graphics2D;
import java.awt.Point;
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
		graphicsData.rotate(radians, imageData.getWidth(), imageData.getHeight()/2);
		graphicsData.drawImage(imageData, 0, 0, imageData.getWidth(), imageData.getHeight(), null);
		graphicsData.dispose();
	}

	public void removePixelsOutsideControlFields() throws IOException
	{
		ControlFieldReader reader = new ControlFieldReader(imageData);
		reader.readAligment();
		
		Point start = reader.getLeftControlField();
		Point upperRight = reader.getRightControlField();
		
		final int width = upperRight.x - start.x;
		final int height = reader.getBottomControlField().y - start.y;
		imageData = imageData.getSubimage(start.x, start.y, width, height);
	}
	
	
	
	public boolean saveToFile(final File filePath) throws IOException
	{
		if(!fileType(filePath).equals("png"))
			throw new IllegalArgumentException("File type must be \"png\" for file, but was " + fileType(filePath));
		return ImageIO.write(imageData, "PNG", filePath);
	}
	
	public boolean saveToFile() throws IOException
	{
		File newFile = new File(image.getAbsolutePath());
		if(!fileType(newFile).equals("png"))
			newFile = changeFileEndingToPNG(newFile);
		return ImageIO.write(imageData, "PNG", newFile);
	}
	
	public static String fileType(File filePath)
	{
		final String filename = filePath.getPath();
		final String[] filenameSplitted = filename.split("\\.");
		final int fileTypePosition = filenameSplitted.length - 1;
		return filenameSplitted[fileTypePosition].toLowerCase();
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
