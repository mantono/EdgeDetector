package diacheck.java.libs.imageTools;

/**
 * 
 * This exception indicates that the source image contains too much noise and therefore renders the image unusable for color analysis.
 * @author Anton &Ouml;sterberg
 *
 */
public class HighNoiseException extends RuntimeException
{
	private static final long serialVersionUID = 1685378872648020677L;
	
	HighNoiseException()
	{
		super();
	}

	HighNoiseException(String message)
	{
		super(message);
	}

	public HighNoiseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
