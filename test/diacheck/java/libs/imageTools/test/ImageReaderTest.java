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
}
