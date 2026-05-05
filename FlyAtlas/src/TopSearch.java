// Searches database for genes enriched or expressed for a particular tissue 
// 05.09.2012

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopSearch 
{	
	private ExperimentSet [] exptSetList;		// Array of Experiment Set objects
	private final int LIST_LENGTH = 16000;		// length of exptSetList ~ Number of Rows in oligo table (act 15100)
	private int setListSize = 0;				// occupancy of exptSetList

	public TopSearch(int tissueID, boolean byEnrichment, int maxDisplayed)
	{
		exptSetList = new ExperimentSet[LIST_LENGTH];		
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		// Param Query for First query proper
		ParamQuery parQy1 = null;	
		if(byEnrichment)
		{
			parQy1 = DBQuery.getParamQuery("PROBESET_TISSUE_ENRICHMENT");
		}
		else
		{
			parQy1 = DBQuery.getParamQuery("PROBESET_TISSUE_ABUNDANCE");
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
			// PRELIM QUERY to get avgAbundance Abundance (== Abundance)
			double avgAbundance = 0;			
			if(!byEnrichment)
			{
				ParamQuery parQy0 = DBQuery.getParamQuery("AVE_ABUNDANCE");
				parQy0.setPrepStatement(conn);			
				PreparedStatement prepStat0 = parQy0.getPrepStatement();
				prepStat0.setInt(1, tissueID);			
				ResultSet resSet0 = prepStat0.executeQuery();		
				if(resSet0.first())
				{
					resSet0.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet0.next())		// moves to next row while rows remain
					{
						avgAbundance = resSet0.getDouble(1);
					}
				}
			}

			PreparedStatement prepStat1 = parQy1.getPrepStatement();
			prepStat1.setInt(1, tissueID);
			if(!byEnrichment)
			{
				prepStat1.setDouble(2, avgAbundance);	
			}
			ResultSet resSet1 = prepStat1.executeQuery();
						
			String [] probesetIDs = new String [LIST_LENGTH];	// stores probe-set ids found in 1st Qy
			int idListSize = 0;									// occupancy of probesetIDs array
			
			if(resSet1.first())
			{
				resSet1.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet1.next() && idListSize < probesetIDs.length)				
				{
					probesetIDs[idListSize] = resSet1.getString(1);
					idListSize++;
				}
			}				
			
			// SECOND QUERY
			for (int i = 0; i<idListSize-1; i++)
			{	
				PreparedStatement prepStat2 = parQy2.getPrepStatement();
				prepStat2.setString(1, probesetIDs[i]);
				ResultSet resSet2 = prepStat2.executeQuery();
				ExperimentSet exptSet = null;				// sets up ExperimentSet to hold Expts		

				// Variables are external to loop to allow detection of a new set of Expts...
				String fbgn = new String();
				String probesetID = new String();
				
				int setCount = 0;				// count of exptSets for this probesetID
	
				boolean atStart = true;	// to mark the situation when the ResultSet is first read
				if(resSet2.first())
				{
					resSet2.beforeFirst();		// reset cursor as 'if' moves it on a row
					while (resSet2.next())		// moves to next row while rows remain
					{
						if(atStart || 
								// fbgn or probeset
								(!resSet2.getString("FBgn").equals(fbgn) || !resSet2.getString("ProbesetID").equals(probesetID))) 
						{						
							// store any previously built exptSet from the last 25 (or whatever) loops
							if(!atStart)
							{
								exptSetList[setListSize-1] = exptSet;
							}
							// read and change values of FBgn and probesetID
							fbgn = resSet2.getString("FBgn");
							probesetID = resSet2.getString("ProbesetID");
							// read other variables needed for constructor of new ExptSet
							String cgNum = resSet2.getString("CGNum");
							String symbol = resSet2.getString("Symbol");
							String name = resSet2.getString("Name");
							// construct ExperimentSet
							exptSet = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);
							
							// determine whether degenerate probe and set boolean in ExptSet object
							if (resSet2.getInt("ProbeDegeneracy") == 1)
							{
								exptSet.setDegenerate();
							}
							
							// determine whether exptSet is Duplicate (i.e.same gene as one already read) and set boolean in ExptSet object
							for (int j = 0; j < setListSize; j++)
							{
								if (exptSetList[j].getFBgn().equals(fbgn))
								{
									exptSetList[j].setDuplicate();
									exptSet.setDuplicate();
								}
							}
							
							// now read data for first Expt object of new ExptSet 
							String signifChange = resSet2.getString("SignifChange");
							double expression = resSet2.getDouble("Abundance");
							double expressionSE = resSet2.getDouble("AbundanceSE");						
							int signalDetected = resSet2.getInt("SignalDetected");
							double enrichment = resSet2.getDouble("Enrichment");
							int flyID = resSet2.getInt("FlyID");					

							// construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							exptSet.addExpt(expt);

							atStart = false;
							setCount++;
							setListSize++;
						}
						else 
						{
							//  read data for Expt object of pre-existing ExptSet
							String signifChange = resSet2.getString("SignifChange");
							double expression = resSet2.getDouble("Abundance");
							double expressionSE = resSet2.getDouble("AbundanceSE");						
							int signalDetected = resSet2.getInt("SignalDetected");
							double enrichment = resSet2.getDouble("Enrichment");
							int flyID = resSet2.getInt("FlyID");

							// Construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							exptSet.addExpt(expt);
						}
					}
					// capture the final set for this probesetID
					if(setCount > 0)
					{
						exptSetList[setListSize-1] = exptSet;
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

	public ExperimentSet[] getExptSetList()
	{
		return exptSetList;
	}

	public int getListSize()
	{
		return setListSize;
	}

}
