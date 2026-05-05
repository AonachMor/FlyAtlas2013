// Searches database for genes by a gene identifier
// Puts results in an array of ExperimentSets (i.e. group of Experiment objects, one per tissue/stage combination) 
// Size of this array is 1, unless gene picked up by duplicate probesets
// 10.10.2012

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneSearch 
{		
	private ExperimentSet [] exptSetList;		// holds all the ExperimentSets found from query
	private int LIST_LENGTH = 100;				// length of exptSetList - 100 exceeds number of duplicates in DB
	private int setListSize = 0;				// occupancy of exptSetList

	public GeneSearch(String gene, String query)
	{	
		exptSetList = new ExperimentSet [LIST_LENGTH];	
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = DBQuery.getParamQuery(query);

		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, gene);
			if(query.equals("SYMBOL") || query.equals("NAME"))
			{
				prepStat.setString(2, gene);
			}	
			ResultSet resSet = prepStat.executeQuery();		
			ExperimentSet expSet = null;	// sets up ExperimentSet to hold Expts
			
			// Variables are external to loop to allow detection of a new set of Expts
			String fbgn = "";
			String probesetID = "";			
				
			boolean atStart = true;	// to mark the situation when the ResultSet is first read
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					// checks if new set by by initial val of atStart or change in values of FBgn or Probe Set ID
					if(atStart || (!resSet.getString("FBgn").equals(fbgn) || !resSet.getString("ProbesetID").equals(probesetID)))
					{
						// store any previously built expSet from the last 25 (or whatever) loops
						if(!atStart)
						{
							exptSetList[setListSize-1] = expSet;
						}		
						// read and change values of FBgn and probesetID
						fbgn = resSet.getString("FBgn");
						probesetID = resSet.getString("ProbesetID");
						// read other variables needed for constructor of new ExptSet
						String cgNum = resSet.getString("CGNum");
						String symbol = resSet.getString("Symbol");
						String name = resSet.getString("Name");
						// construct ExptSet
						expSet = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);
							
						// determine whether degenerate probe and set boolean in ExptSet object
						if (resSet.getInt("ProbeDegeneracy") == 1)		// oligo.ProbeDegeneracy
						{
							expSet.setDegenerate();
						}
						
						// determine whether ExptSet is Duplicate (i.e.same gene as one already read) and set boolean in ExptSet object
						for (int i = 0; i < setListSize; i++)
						{
							if (exptSetList[i].getFBgn().equals(fbgn))
							{
								exptSetList[i].setDuplicate();
								expSet.setDuplicate();
							}
						}
						
						setListSize++;		// now we can update the list size - odd, I'd have thought do it when finally add set to list
						atStart = false;	// turn off flag 
						
						// now read data for first Expt object of new ExptSet  
						String signifChange = resSet.getString("SignifChange");
						double expression = resSet.getDouble("Abundance");					
						double expressionSE = resSet.getDouble("AbundanceSE");							
						int signalDetected = resSet.getInt("SignalDetected");
						double enrichment = resSet.getDouble("Enrichment");
						int flyID = resSet.getInt("FlyID");	
						
						// Construct the Experiment and add to the ExperimentSet
						Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
						expSet.addExpt(expt);								
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
						
						// Construct the Experiment and add to the ExperimentSet
						Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
						expSet.addExpt(expt);
					}	
				}	// end of while loop			
				// this adds final ExperimentSet to the list after exiting while loop 
				if(setListSize  > 0)
				{
					exptSetList[setListSize-1] = expSet;
				}
			}		// end of brace that starts reading ResultSet
			resSet.close(); // ! before conn.close()
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
				/* accessor methods for ExptSet[] generated by search */
	
	public ExperimentSet[] getExptSetList()
	{
		return exptSetList;
	}
	
	public int getListSize()
	{
		return setListSize;
	}

}