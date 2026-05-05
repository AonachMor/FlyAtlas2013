// Stores the data for one tissue - models the expt table except that: 
// does not include ProbesetID (in the ExperimentSet)
// gets stage and tissue from flyID
// 14.09.2012
import java.sql.*;

public class Experiment
{
	private int flyID;				// FlyID from DB (determines stage and tissue)	
	private String stage;			// Adult or Larval
	private String tissue;			// Name of tissue
	private String signifChange;	// Whether is change from enrichment of 1 is + or - (significant) or 0 (not sig diff)
	private double abundance;		// Expression field (used for Abundance here)		
	private double abundanceSE;		// SE of Expression 
	private double enrichment;		// Enrichment field of DB
	private int signalDetected;		// No. of replicate arrays giving signal (0-4)

	public Experiment(String signifChange, double abundance, double abundanceSE, int signalDetected, 
			double enrichment, int flyID)
	{
		this.flyID = flyID;
		this.signifChange = signifChange;
		this.abundance = abundance;
		this.abundanceSE = abundanceSE;
		this.signalDetected = signalDetected;
		this.enrichment = enrichment;	
		setTissueDetails();
	}
	
	// get stage and tissue from FlyID by query
	private void setTissueDetails()
	{
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();	
		ParamQuery parQ = DBQuery.getParamQuery("STAGE_TISSUE");
		try 
		{
			parQ.setPrepStatement(conn);
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setInt(1, flyID);
			ResultSet resSet = prepStat.executeQuery();	
			if(resSet.next())
			{
				stage = resSet.getString("Stage");
				tissue = resSet.getString("Tissue");
			}
			if(resSet != null)
			{
				resSet.close();
			}
		}
		catch (SQLException e) 
		{
			System.out.println("problem reading result set");
			System.out.println(e.toString());
		}
		finally // close the connection
		{
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}
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
	
	public String getStage()
	{
		return stage;
	}
	
	public String getTissue()
	{
		return tissue;
	}

	public int getFlyID()
	{
		return flyID;
	}
	
	// replaces default as tab-sep text suitable for output
	public String toString()
	{
		return stage + "\t" + tissue + "\t" + abundance  + "\t" + abundanceSE   + "\t" + enrichment  + "\t" + signifChange   + "\t" + signalDetected;
	}
}
