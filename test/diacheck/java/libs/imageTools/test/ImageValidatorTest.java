package diacheck.java.libs.imageTools.test;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import diacheck.java.libs.imageTools.ImageValidator;

public class ImageValidatorTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testGetNoiseLevelZeroNoise() throws IOException
	{
		ImageValidator imageWhite = getImageValidatorFromString("white_zero_noise.png");
		assertEquals(0, imageWhite.getNoiseLevel(), 0.00001);
		
		ImageValidator imageBlack = getImageValidatorFromString("black_zero_noise.png");
		assertEquals(0, imageBlack.getNoiseLevel(), 0.00001);
		
		ImageValidator imageRed = getImageValidatorFromString("red_zero_noise.png");
		assertEquals(0, imageRed.getNoiseLevel(), 0.00001);
		
		ImageValidator imageBlue = getImageValidatorFromString("blue_zero_noise.png");
		assertEquals(0, imageBlue.getNoiseLevel(), 0.00001);
		
		ImageValidator imageMaxNoise = getImageValidatorFromString("black_and_white_max_noise.png");
		assertEquals(1, imageMaxNoise.getNoiseLevel(), 0.0000000000001);
	}
	
	@Test
	public void testCustomNoiseRealPhotoAsReference() throws IOException
	{
		ImageValidator imageNoise100 = getImageValidatorFromString("../SDR/ISO100_water_negative.jpg");
		assertEquals(0.009, imageNoise100.getNoiseLevel(), 0.001);
		
		ImageValidator imageNoise800 = getImageValidatorFromString("../SDR/ISO800_water_negative.jpg");
		assertEquals(0.0122, imageNoise800.getNoiseLevel(), 0.0001);
		
		ImageValidator imageNoise2000 = getImageValidatorFromString("../SDR/ISO2000_water_negative.jpg");
		assertEquals(0.015, imageNoise2000.getNoiseLevel(), 0.0001);
		
		ImageValidator imageNoise25_600 = getImageValidatorFromString("../SDR/ISO25600_water_negative.jpg");
		assertEquals(0.054, imageNoise25_600.getNoiseLevel(), 0.001);
	}

	@Test
	public void testGetOverexposureRatio() throws IOException
	{
		ImageValidator imageBlackWhite = getImageValidatorFromString("black_and_white.png");
		assertEquals(0.5, imageBlackWhite.getOverexposureRatio(), 0.001);
		
		ImageValidator imageWhite = getImageValidatorFromString("white_zero_noise.png");
		assertEquals(1, imageWhite.getOverexposureRatio(), 0.00001);
		
		ImageValidator imageBlack = getImageValidatorFromString("black_zero_noise.png");
		assertEquals(0, imageBlack.getOverexposureRatio(), 0.00001);
	}

	@Test
	public void testGetUnderexposureRatio() throws IOException
	{
		ImageValidator imageBlackWhite = getImageValidatorFromString("black_and_white.png");
		assertEquals(0.5, imageBlackWhite.getUnderexposureRatio(), 0.001);
		
		ImageValidator imageWhite = getImageValidatorFromString("white_zero_noise.png");
		assertEquals(0, imageWhite.getUnderexposureRatio(), 0.00001);
		
		ImageValidator imageBlack = getImageValidatorFromString("black_zero_noise.png");
		assertEquals(1, imageBlack.getUnderexposureRatio(), 0.00001);
	}

	@Test
	public void testGetAverageExposure() throws IOException
	{
		ImageValidator imageBlackWhite = getImageValidatorFromString("black_and_white.png");
		assertEquals(127.5, imageBlackWhite.getAverageExposure(), 0.00001);
		
		ImageValidator imageWhite = getImageValidatorFromString("white_zero_noise.png");
		assertEquals(255, imageWhite.getAverageExposure(), 0.00001);
		
		ImageValidator imageBlack = getImageValidatorFromString("black_zero_noise.png");
		assertEquals(0, imageBlack.getAverageExposure(), 0.00001);
	}
	
	private ImageValidator getImageValidatorFromString(final String imageName) throws IOException
	{
		final String path = ImageReaderTest.IMAGE_PATH + "/ImageValidator/" + imageName;
		File imageFile = new File(path);
		assertTrue("Cant read " + imageFile.toString(), imageFile.canRead());
		return new ImageValidator(ImageIO.read(imageFile));
	}
	
	@Test
	public void testGetAverageEqposure2() throws IOException
	{
		File imageFile = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped.png");
		assertTrue("Cant read " + imageFile.toString(), imageFile.canRead());
		ImageValidator original = new ImageValidator(ImageIO.read(imageFile));		
		System.out.println(original.getAverageExposure());	
		
		File imageFile2 = new File(ImageReaderTest.IMAGE_PATH + "GOOD_horizontal_and_cropped_increased_birghtness_and_contrast.png");
		assertTrue("Cant read " + imageFile2.toString(), imageFile2.canRead());
		ImageValidator enhanced = new ImageValidator(ImageIO.read(imageFile2));
		System.out.println(enhanced.getAverageExposure());
	}

}
