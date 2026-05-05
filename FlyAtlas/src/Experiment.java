// Stores the data for one tissue - models the expt table except does not include ProbesetID. (Formerly ExperimentRow)
// (ProbesetID is in the ExperimentSet)
// 05.09.2012

public class Experiment
{
	private int flyID;				// FlyID from DB (determines stage and tissue)
	private String signifChange;	// Whether is change from enrichment of 1 is + or - (significant) or 0 (not sig diff)
	private double abundance;		// Expression field (used for Abundance here)		
	private double abundanceSE;		// SE of Expression 
	private double enrichment;		// Enrichment field of DB
	private int signalDetected;		// No of replicate arrays giving signal (0-4)

	public Experiment(String signifChange, double abundance, double abundanceSE, int signalDetected, 
			double enrichment, int flyID)
	{
		this.flyID = flyID;
		this.signifChange = signifChange;
		this.abundance = abundance;
		this.abundanceSE = abundanceSE;
		this.signalDetected = signalDetected;
		this.enrichment = enrichment;
	}

	public String getSignifChange()
	{
		return signifChange;
	}

	public double getAbundance()
	{
		return abundance;
	}

	public double getAbundanceSE()
	{
		return abundanceSE;
	}

	public int getSignalCount()
	{
		return signalDetected;
	}

	public double getEnrichment()
	{
		return enrichment;
	}

	public int getFlyID()
	{
		return flyID;
	}
}
