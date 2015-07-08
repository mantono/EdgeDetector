package diacheck.java.libs.analytes;

import diacheck.java.libs.imageTools.Field;

public class Glucose implements Analyte
{
	public final static double FIELD_ANGLE = 40;
	public final static int DISTANCE = 1200;
	
	private final Field colorData;
	
	Glucose(final Field data)
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
