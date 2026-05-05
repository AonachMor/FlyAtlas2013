// Uses the probeset IDs of an array of 'Probeset' objects to find corresponding Genes
// Used by Profile Search after related ProbeSets have been identified
// Previously named SearchByPID
// 05.09.2012

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileSearch
{
	private ExperimentSet [] exptSetList;			// Array of Experiment Set objects
	private final int LIST_LENGTH = 10000;			// length of exptSetList
	private int setListSize = 0;					// occupancy of exptSetList

	public ProfileSearch(Probeset[] probesetList, int probeListSize, int maxDisplayed)
	{
		exptSetList = new ExperimentSet [LIST_LENGTH];	
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = DBQuery.getParamQuery("GENE_BY_PROBESET");
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			for(int i = 0; i < probeListSize; i++)
			{		
				PreparedStatement prepStat = parQ.getPrepStatement();
				prepStat.setString(1, probesetList[i].getProbesetID());
				ResultSet resSet = prepStat.executeQuery();
				ExperimentSet exptSet = null;				// sets up ExperimentSet to hold Expts	

				// Variables are external to loop to allow detection of a new set of Expts...
				String fbgn = new String("");
				String probesetID = new String("");
				
				int setCount = 0;				// count of exptSets for this probesetID

				boolean atStart = true;	// to mark the situation when the ResultSet is first read
				if(resSet.first())
				{
					resSet.beforeFirst();		// reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{	
						//checks if new exptSet by first line..
						if(atStart || 
								//fbgn-probeset
								(!resSet.getString("FBgn").equals(fbgn) || !resSet.getString("ProbesetID").equals(probesetID)))
						{
							// store any previously built expSet from the last 25 (or whatever) loops
							if(!atStart)
							{
								exptSet.setRstat(probesetList[i].getRstat());
								exptSet.setPstat(probesetList[i].getPstat());
								exptSetList[setListSize-1] = exptSet;
							}
							// read and change values of FBgn and probesetID
							fbgn = resSet.getString("FBgn");
							probesetID = resSet.getString("ProbesetID");						
							// read other variables needed for constructor of new ExptSet
							String cgNum = resSet.getString("CGNum");
							String symbol = resSet.getString("Symbol");
							String name = resSet.getString("Name");
							// construct ExperimentSet
							exptSet = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);

							// determine whether degenerate probe and set boolean in ExptSet object
							if (resSet.getInt("ProbeDegeneracy") == 1)
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
							String signifChange = resSet.getString("SignifChange");
							double expression = resSet.getDouble("Abundance");
							double expressionSE = resSet.getDouble("AbundanceSE");								
							int signalDetected = resSet.getInt("SignalDetected");
							double enrichment = resSet.getDouble("Enrichment");
							int flyID = resSet.getInt("FlyID");				

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
							String signifChange = resSet.getString("SignifChange");
							double expression = resSet.getDouble("Abundance");
							double expressionSE = resSet.getDouble("AbundanceSE");								
							int signalDetected = resSet.getInt("SignalDetected");
							double enrichment = resSet.getDouble("Enrichment");
							int flyID = resSet.getInt("FlyID");			

							// construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							exptSet.addExpt(expt);
						}
					}
					// capture the final set
					if(setCount > 0)
					{
						exptSet.setRstat(probesetList[i].getRstat());
						exptSet.setPstat(probesetList[i].getPstat());
						exptSetList[setListSize-1] = exptSet;	
					}
				}
				resSet.close(); // ! before conn.close()
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