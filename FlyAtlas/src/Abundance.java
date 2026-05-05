// Models Abundance aspect of experiment with method to get log 
// can't see for the moment why Experiment object couldn't be modified and used
// Need to look carefully at ProbeSetList which uses this
// 05.09.2012

public class Abundance
{
	private double abundance;			// Expression from Experiment table in DB
	private int signalDetected;			// SignalDetected from Experiment table in DB
	private int flyID;					// FlyID from Experiment table in DB

	public Abundance(double abundance, int signalDetected, int flyID)
	{
		this.abundance = abundance;
		this.signalDetected = signalDetected;
		this.flyID = flyID;
	}
	
	public double getLogAbundance()
	{
		return Math.log(abundance)/Math.log(2);
	}
	
	public double getAbundance()
	{
		return abundance;
	}

	public int getSignalCount()
	{
		return signalDetected;
	}
	
	public int getID()
	{
		return flyID;
	}
}
