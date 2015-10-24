package diacheck.java.libs.imageTools;

public class BadExposureException extends RuntimeException
{
	/**
	 * This exception will be thrown when an image is a bad exposure, etiher overexposured or other exposured.
	 * @author Anton &Ouml;sterberg
	 *
	 */
	private static final long serialVersionUID = 8652388192095083363L;

	BadExposureException()
	{
		super();
	}

	BadExposureException(String message)
	{
		super(message);
	}
	
	BadExposureException(double ratio)
	{
		super("Bad exposure ratio is too high: " + ratio);
	}

	public BadExposureException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
