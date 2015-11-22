package diacheck.java.libs.imageTools;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class WhiteBalance
{
	private final Color whiteBalance;

	public WhiteBalance(final Color color)
	{
		this.whiteBalance = color;
	}
	
	public boolean isWithinBounds()
	{
		final int red = whiteBalance.getRed();
		final int green = whiteBalance.getGreen();
		final int blue = whiteBalance.getBlue();
		
		final short min = 110;
		final short max = 240;
		
		if(red < min || red > max)
			throw new WhiteBalanceException("Red channel is unbalanced: " + red);
		if(green < min || green > max)
			throw new WhiteBalanceException("Green channel is unbalanced: " + green);
		if(blue < min || blue > max)
			throw new WhiteBalanceException("Blue channel is unbalanced: " + blue);
		
		return true;
	}

	public Color getWhiteBalance()
	{
		return whiteBalance;
	}

	public List<Color> whiteBalanceCompensation(List<Color> foundColors)
	{
		List<Color> newColors = new ArrayList<Color>(foundColors.size());
		for(Color color : foundColors)
			newColors.add(whiteBalanceCompensation(color));
		return newColors;
	}

	public Color whiteBalanceCompensation(Color averageColor)
	{
		final int redGreenDiff = whiteBalance.getGreen() - whiteBalance.getRed();
		final int blueGreenDiff = whiteBalance.getGreen() - whiteBalance.getBlue();
		return new Color(averageColor.getRed() + redGreenDiff, averageColor.getGreen(), averageColor.getBlue() + blueGreenDiff);
	}
}
