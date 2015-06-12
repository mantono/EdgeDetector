package diacheck.java.libs.imageTools;

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
