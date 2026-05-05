// Searches database for genes with a high difference in tissue between adult and larval
// 11.09.2012

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DevelopmentalSearch
{
	private ExperimentSet [] exptSetList;		// Array of Experiment Set objects
	private final int MAX = 16000;				// ~ Number of Rows in oligo table (act 15100) - better set from a query?
	private int setListSize = 0;				// Count of all found genes

	public DevelopmentalSearch(String uniTissue, boolean adultMinusLarval, boolean byEnrichment, int maxDisplayed, FlyTissueList ftList)
	{		
		// use uniTissue to find FlyIDs for Adult and Larval pair
		FlyTissue ftAdult = ftList.getFlyTissueByUni(uniTissue, "Adult");

		int flyIDadult = ftAdult.getID();
		FlyTissue ftLarval = ftList.getFlyTissueByUni(uniTissue, "Larval");
		int flyIDlarval = ftLarval.getID();

		exptSetList = new ExperimentSet[MAX];
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		// Param Query for First query proper
		ParamQuery parQy1 = null;	
		if(byEnrichment)
		{
			parQy1 = DBQuery.getParamQuery("PROBESET_ENRICH");
		}
		else
		{
			parQy1 = DBQuery.getParamQuery("PROBESET_ABUND");
		}
		
		try 
		{
			parQy1.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}
		
		// Param Query for Second query proper
		ParamQuery parQy2 = DBQuery.getParamQuery("GENE_BY_PROBESET");
		try 
		{
			parQy2.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			// PRELIMINARY QUERY for averages
			double avgDiff = 0;			// average difference in expression/enrichment between the adult and larval
			
			ParamQuery parQy0 = null;		
			if(byEnrichment)
			{
				parQy0 = DBQuery.getParamQuery("AVE_DEVEL_ENRICH_DIFF");
			}
			else
			{
				parQy0 = DBQuery.getParamQuery("AVE_DEVEL_ABUND_DIFF");			
			}
			parQy0.setPrepStatement(conn);			
			PreparedStatement prepStat0 = parQy0.getPrepStatement();
			
			prepStat0.setInt(1, flyIDadult);	
			prepStat0.setInt(2, flyIDlarval);
			ResultSet resSet0 = prepStat0.executeQuery();

			if(resSet0.first())
			{
				resSet0.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet0.next())		// moves to next row while rows remain
				{
					avgDiff = resSet0.getDouble(1);
				}
			}
					
			// FIRST QUERY proper gets probe sets ids with sig data for comparison
			PreparedStatement prepStat1 = parQy1.getPrepStatement();
			prepStat1.setInt(1, flyIDadult);
			prepStat1.setInt(2, flyIDlarval);
			ResultSet resSet1 = prepStat1.executeQuery();

			DevelopmentProbeset [] probesetList = new DevelopmentProbeset [MAX];		// holds developmentalProbesets (inc values)
			int probeSize = 0;					// occupancy of probeSet array
			String currProbeSetID = new String();

			if(resSet1.first())
			{
				DevelopmentProbeset set = null;		// holds pair of values - overwritten every second time
				resSet1.beforeFirst();				// (reset cursor)
				while (resSet1.next() && probeSize<probesetList.length)				
				{
					if(!resSet1.getString(1).equals(currProbeSetID))	// i.e. new probesetID
					{
						currProbeSetID = resSet1.getString(1);	// make this current
						// instantiate new dps
						set = new DevelopmentProbeset(currProbeSetID, Double.parseDouble(resSet1.getString(2)), resSet1.getInt(3));
					}
					else										// i.e. second tissue for probesetID
					{
						// add second tissue
						set.addExpression(Double.parseDouble(resSet1.getString(2)), resSet1.getInt(3));
						// get difference
						double diff = set.getValueDifference(adultMinusLarval);	// this should also set value
						if(diff > avgDiff && diff != 0)
						{
							probesetList[probeSize] = set;
							probeSize++;
						}			
					}
				}
			}
			
			// Added from Scott
			DevelopmentProbeset [] tempProbeSet = new DevelopmentProbeset [probeSize];
			System.arraycopy(probesetList, 0, tempProbeSet, 0, probeSize);
			probesetList = tempProbeSet;
			
			DevelopmentComparator developmentComparator = new DevelopmentComparator();
			Arrays.sort(probesetList, developmentComparator);

			// SECOND QUERY  - he's hived this off now
			for (int i=0; i<probeSize; i++)
			{
				PreparedStatement prepStat2 = parQy2.getPrepStatement();
				prepStat2.setString(1, probesetList[i].getProbesetID());			
				ResultSet resSet2 = prepStat2.executeQuery();
				ExperimentSet eSet = null;		// sets up ExperimentSet to hold Expts

				// Variables are external to loop to allow detection of a new set of Expts...
				String fbgn = new String();
				String probesetID = new String();
				
				int setCount = 0;			// count of exptSets for this probesetID
				
				boolean atStart = true;		// to mark the situation when the ResultSet is first read
				if(resSet2.first())
				{
					resSet2.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet2.next())		// moves to next row while rows remain
					{
						if(atStart || 
								//fbgn-probeset
								(!resSet2.getString("FBgn").equals(fbgn) || !resSet2.getString("ProbesetID").equals(probesetID))) 
						{
							// store any previously built expSet from the last 25 (or whatever) loops
							if(!atStart)
							{
								exptSetList[setListSize-1] = eSet;
							}
							// read and change values of FBgn and probesetID
							fbgn = resSet2.getString("FBgn");
							probesetID = resSet2.getString("ProbesetID");
							// read other variables needed for constructor of new ExptSet
							String cgNum = resSet2.getString("CGNum");
							String symbol = resSet2.getString("Symbol");
							String name = resSet2.getString("Name");
							// construct ExperimentSet
							eSet = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);

							// determine whether degenerate probe and set boolean in ExptSet object
							if (Integer.parseInt(resSet2.getString("ProbeDegeneracy")) == 1)
							{
								eSet.setDegenerate();
							}
							
							// determine whether exptSet is Duplicate (i.e.same gene as one already read) and set boolean in ExptSet object
							for (int j = 0; j < setListSize; j++)
							{
								if (exptSetList[j].getFBgn().equals(fbgn))
								{
									exptSetList[j].setDuplicate();
									eSet.setDuplicate();
								}
							}
							//record the experimental stuff
							String signifChange = resSet2.getString("SignifChange");
							double expression = resSet2.getDouble("Abundance");
							double expressionSE = resSet2.getDouble("AbundanceSE");				
							int signalDetected = resSet2.getInt("SignalDetected");
							double enrichment = resSet2.getDouble("Enrichment");
							int flyID = resSet2.getInt("FlyID");

							// expt setup and addition to set
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							eSet.addExpt(expt);

							atStart = false;
							setCount++;
							setListSize++;
						}
						else 
						{
							// now read data for first Expt object of new ExptSet
							String signifChange = resSet2.getString("SignifChange");
							double expression = resSet2.getDouble("Abundance");
							double expressionSE = resSet2.getDouble("AbundanceSE");				
							int signalDetected = resSet2.getInt("SignalDetected");
							double enrichment = resSet2.getDouble("Enrichment");
							int flyID = resSet2.getInt("FlyID");

							// construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							eSet.addExpt(expt);
						}
					}
					// capture the final set
					if(setCount > 0)
					{
						exptSetList[setListSize-1] = eSet;
					}
					resSet2.close(); // ! before conn.close()
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
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

	public int getListSize()
	{
		return setListSize;
	}

	public ExperimentSet[] getExptSetList()
	{
		return exptSetList;
	}

}
