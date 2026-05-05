// Stores experimental details about the experimental results (Abundance objects) and correlation values for an affymetrix probeset
// Note not in camelCase for the name as it is the single unit
// 29.08.2012

public class Probeset 
{
	private String probesetID;				// Affimetrix ID of probeset (as in DB)
	private Abundance[] abundanceList;		// Array of expression objects corresponding to probe - i.e. one for ea tissue/stage combination
	private final int LIST_LENGTH = 100;	// length of array
	private int listSize = 0;				// occupancy of array
	private double pStat; 					// P statistic for correlation significance
	private double rStat;					// r statistic for correlation

	public Probeset(String probesetID)
	{
		this.probesetID = probesetID;
		abundanceList = new Abundance [LIST_LENGTH];
	}

	public void addAbundance(Abundance abund)
	{
		abundanceList[listSize] = abund;
		listSize++;
	}
	
	public String getProbesetID()
	{
		return probesetID;
	}
	
	// gets an Abundance object by its flyID (originally just based on position in array hack)
	public Abundance getAbundance(int id)
	{
		for(int i=0; i<listSize; i++)
		{
			Abundance exp = abundanceList[i];
			if(exp.getID() == id)
			{
				return abundanceList[i];
			}
		}
		return null;
	}
	
	public void setRstat(double rStat)
	{
		this.rStat = rStat;
	}
	
	public double getRstat()
	{
		return rStat;
	}
	
	public void setPstat(double pStat)
	{
		this.pStat = pStat;
	}
	
	public double getPstat()
	{
		return pStat;
	}
}
