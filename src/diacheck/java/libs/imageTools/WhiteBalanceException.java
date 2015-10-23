package diacheck.java.libs.imageTools;

/**
 * An exception indicating that the white balance for the image is to unbalanced and no color data can be safely read.
 * @author Anton &Ouml;sterberg
 *
 */
class WhiteBalanceException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	
	WhiteBalanceException()
	{
		super();
	}

	WhiteBalanceException(String message)
	{
		super(message);
	}

	public WhiteBalanceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
