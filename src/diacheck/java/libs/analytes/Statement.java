package diacheck.java.libs.analytes;

public enum Statement
{
	GOOD((byte) 1), OKEY((byte) 0), BAD((byte) -1);
	
	private final byte num;
	
	private Statement(byte num)
	{
		this.num = num;
	}
	
}
