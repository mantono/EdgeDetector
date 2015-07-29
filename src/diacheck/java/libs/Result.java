package diacheck.java.libs;

import diacheck.java.libs.analytes.*;

public class Result
{
	private final Glucose glucose;
	private final Protein protein;
	private final Ketones ketones;
	private final PH ph;
	private final SpecificGravity specificGravity;
	
	public Result(Glucose glucose, Ketones ketones, PH ph, Protein protein, SpecificGravity specificGravity)
	{
		this.glucose = glucose;
		this.ketones = ketones;
		this.ph = ph;
		this.protein = protein;
		this.specificGravity = specificGravity;
	}
	
	public Statement getStatementForGlucose()
	{
		return glucose.statement();
	}
	
	public Statement getStatementForProtein()
	{
		return protein.statement();
	}
	
	public Statement getStatementForKetones()
	{
		return ketones.statement();
	}
	
	public Statement getStatementForPH()
	{
		return ph.statement();
	}
	
	public Statement getGeneralStatement()
	{
		return Statement.GOOD;
	}
}
