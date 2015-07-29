package diacheck.java.libs.analytes;

import diacheck.java.libs.imageTools.Field;

public class Ketones implements Analyte
{

	private final Field colorData;

	public Ketones(final Field data)
	{
		this.colorData = data;
	}

	@Override
	public double result()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte zone()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Statement statement()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
