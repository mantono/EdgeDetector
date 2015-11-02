package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ControlFieldReader;
import diacheck.java.libs.imageTools.FieldType;
import diacheck.java.libs.imageTools.ImageReader;

public class ControlFieldReaderTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testFindAllControlFields() throws IOException
	{
		final File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD.png");
		BufferedImage imageData = ImageIO.read(input);
		ControlFieldReader cfReader = new ControlFieldReader(imageData);
	}
	
	@Test
	public void readAligment() throws IOException
	{
		final File input = new File(ImageReaderTest.IMAGE_PATH + "GOOD_with_rotation.png");
		BufferedImage imageData = ImageIO.read(input);
		ControlFieldReader cfReader = new ControlFieldReader(imageData);
		assertEquals(0.1265731, cfReader.readAligment(), 0.01);
	}
}
