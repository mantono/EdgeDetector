package diacheck.java.libs;

import java.io.File;
import java.io.Serializable;

import com.mantono.www.Date;

public class SampleRequest implements Serializable, Comparable<SampleRequest>
{
	
	private static final long serialVersionUID = 0;
	private final long clientID;
	private final File image;
	private final Date timestamp = new Date();
	
	public SampleRequest(final long id, final File image)
	{
		this.clientID = id;
		this.image = image;
	}

	@Override
	public int compareTo(SampleRequest other)
	{
		return this.timestamp.compareTo(other.timestamp);
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		if(otherObject == null)
			return false;
		if(otherObject.getClass() != this.getClass())
			return false;
		SampleRequest other = (SampleRequest) otherObject;
		
		return this.clientID == other.clientID && this.image.equals(other.image) && this.timestamp.equals(other.timestamp); 
	}
	
	@Override
	public int hashCode()
	{
		return (int) (Integer.MAX_VALUE % (clientID * 11 + timestamp.getTime()));
	}
}