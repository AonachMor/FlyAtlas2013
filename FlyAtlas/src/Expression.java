// Models Expression aspect of experiment with method to get log - can't see for the moment why Experiment object couldn't be used
// 05.09.2012

public class Expression 
{
	private int flyID;
	private double expression;
	private int signalDetected;

	public Expression(double expression, int signalDetected, int flyID)
	{
		this.expression = expression;
		this.signalDetected = signalDetected;
		this.flyID = flyID;
	}
	
	public double getLogExpression()
	{
		return Math.log(expression)/Math.log(2);
	}
	
	public double getExpression()
	{
		return expression;
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
