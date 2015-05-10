package diacheck.java.libs.imageTools;

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
