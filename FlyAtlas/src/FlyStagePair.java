// Models adult/larval FlyTissue pair with same uniTissue
// DPL 26.08.2012

public class FlyStagePair
{
	private String uniTissue;		// adult/larval unified tissue name (for matching on table layout)
	private FlyTissue adultTissue;	// adult FlyTissue object (if exists)
	private FlyTissue larvalTissue;	// larval FlyTissue object (if exists)
	private int listPosition;		// position in which the pair should be listed in a table
	
	public FlyStagePair(String uniTissue, FlyTissue adultTissue, FlyTissue larvalTissue, int listPosition)
	{
		this.uniTissue = uniTissue;
		this.adultTissue = adultTissue;
		this.larvalTissue = larvalTissue;
		this.listPosition = listPosition;
	}
	
	// Accessor methods	
	
	public String getUniTissue()
	{
		return uniTissue;
	}

	public FlyTissue getAdultTissue()
	{
		return adultTissue;
	}
	
	public FlyTissue getLarvalTissue()
	{
		return larvalTissue;
	}
	
	public int getListPosition()
	{
		return listPosition;
	}
	
	public boolean hasBothStages()
	{
		if(adultTissue == null || larvalTissue == null)
		{
			return false;		
		}
		else
		{
			return true;
		}
	}
	
	public String toString()
	{
		return("UniTissue: " + uniTissue + " Adult tissue: " + adultTissue + " Larval tissue: " + larvalTissue);
	}
}


