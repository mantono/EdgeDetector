package diacheck.java.libs.imageTools;

public class IllegalInputDataException extends RuntimeException
{
	private static final long serialVersionUID = -1894549554073680863L;

	IllegalInputDataException()
	{
		super();
	}

	IllegalInputDataException(String message)
	{
		super(message);
	}

	public IllegalInputDataException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
