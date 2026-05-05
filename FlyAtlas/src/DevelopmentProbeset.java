// Hold data from developmental TOP search - DevelopmentProbePair would be better name
// 11.09.2012

public class DevelopmentProbeset 
{
	private String probesetID;			// probe set id
	private double[] expressionList;	// array to hold values of expression (or enrichment?) in Adult and Larval
	final private int MAX = 2;			// two conditions - adult and larval
	private double valueDifference;		// difference in two expression or enrichment values

	public DevelopmentProbeset(){}
	
	// Constructor takes first value of expression/enrichment for first tissue
	public DevelopmentProbeset(String probesetID, double value, int signalDetected)
	{
		this.probesetID = probesetID;
		
		expressionList = new double [MAX];
		if(signalDetected==0)				// set to zero if signalDetected zero
		{
			expressionList[0] = 0;
		}
		else
		{
			expressionList[0] = value;
		}
	}

	// setter used for second tissue
	public void addExpression(double value, int signalDetected)
	{
		if(signalDetected==0)				// set to zero if signalDetected zero
		{
			expressionList[1] = 0;
		}
		else
		{
			expressionList[1] = value;
		}
	}

	public String getProbesetID()
	{
		return probesetID;
	}

	// used by comparator, so calculate difference has had to be called first
	public double getValueDifference()
	{
		return valueDifference;
	}
	
	// simpler - but doesn't set
	public double getValueDifference(boolean AminusL)
	{
		if(AminusL)
		{
			valueDifference = (expressionList[0] - expressionList[1]);	// need to set it
			return valueDifference;
		}
		else
		{
			valueDifference = (expressionList[1] - expressionList[0]);
			return valueDifference;
		}
	}
	
}
