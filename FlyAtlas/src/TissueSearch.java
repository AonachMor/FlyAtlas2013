// Searches DB for tissues by GO description term allowing choice of above average enrichment or abundance
// 04.09.2012

import java.sql.*;

public class TissueSearch 
{
	private ExperimentSet [] exptSetList;			// Array of Experiment Set objects
	private final int LIST_LENGTH = 16000;			// length of exptSetList ~ Number of Rows in oligo table (act 15100) 
	private int setListSize = 0;					// occupancy of exptSetList

	public TissueSearch(String keyword, int tissueID, boolean byEnrichment)
	{
		exptSetList = new ExperimentSet [LIST_LENGTH];	
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = null;	
		if(byEnrichment)
		{
			parQ = DBQuery.getParamQuery("TISSUE_ENRICHMENT");
		}
		else
		{
			parQ = DBQuery.getParamQuery("TISSUE_ABUNDANCE");
		}
		
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			// First get average
			double average = 0;			
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
						average = resSet0.getDouble(1);
					}
				}
			}
			
			// Then do main query
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, "%"+keyword+"%");		
			prepStat.setInt(2, tissueID);
			if(!byEnrichment)
			{
				prepStat.setDouble(3, average);	
			}
			ResultSet resSet = prepStat.executeQuery();
			ExperimentSet set = null;				// sets up ExperimentSet to hold Expts
			
			// Variables are external to loop to allow detection of a new set of Expts...
			String fbgn = new String("");
			String probesetID = new String("");
			// ...or new GO  
			String goID = new String("");
			
			boolean expExists = false;
		
			boolean atStart = true;		// to mark the situation when the ResultSet is first read
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					if(atStart || 
							// fbgn and probeset
							(!resSet.getString("FBgn").equals(fbgn) || !resSet.getString("ProbesetID").equals(probesetID)) || 
							// GONum
							(!resSet.getString("GONum").equals(goID)))
					{
						// store any previously built expSet from the last 25 (or whatever) loops
						if(!atStart)
						{
							exptSetList[setListSize-1] = set;
						}		
						// read and change values of FBgn and probesetID
						fbgn = resSet.getString("FBgn");
						probesetID = resSet.getString("ProbesetID");
						// read gene ont variables (and change value of GO ID)
						goID = resSet.getString("GONum");
						String goType = resSet.getString("GOType");
						String goDescript = resSet.getString("GODescript");

						boolean setExists = false;			// set flag
						expExists = false;

						// check if exptSet for this FBgn/probesetID exists and if so create GO object and add it ('if' must have detected new GO)
						for (int i = 0; i < setListSize; i++)
						{
							if(fbgn.equals(exptSetList[i].getFBgn()) && probesetID.equals(exptSetList[i].getProbesetID()))
							{
								setExists = true;				// change flag
								expExists = true;
								Ontology go = new Ontology(goID, goType, goDescript);
								exptSetList[i].addGO(go);		// add a new GO object
								break;							// because no point looking for match further in exptSetList
							}
						}
						
						if(!setExists)						// construct new ExptSet ('if' must have detected new FBgn/probesetID)
						{
							String cgNum = resSet.getString("CGNum");
							String symbol = resSet.getString("Symbol");
							String name = resSet.getString("Name");
							// construct ExptSet
							set = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);
	
							// determine whether degenerate probe and set boolean in ExptSet object
							if (resSet.getInt("ProbeDegeneracy") == 1)
							{
								set.setDegenerate();
							}
							
							// determine whether ExptSet is Duplicate (i.e.same gene as one already read) and set boolean in ExptSet object
							for (int i = 0; i < setListSize; i++)
							{
								if (exptSetList[i].getFBgn().equals(fbgn))
								{
									exptSetList[i].setDuplicate();
									set.setDuplicate();
								}
							}			
							// construct GO object and add to new exptSet
							Ontology go = new Ontology(goID, goType, goDescript);
							set.addGO(go);
	
							// now read data for first Expt object of new ExptSet
							String signifChange = resSet.getString("SignifChange");
							double expression = resSet.getDouble("Abundance");
							double expressionSE = resSet.getDouble("AbundanceSE");	
							int signalDetected = resSet.getInt("SignalDetected");
							double enrichment = resSet.getDouble("Enrichment");
							int flyID = resSet.getInt("FlyID");
							
							// construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							set.addExpt(expt);
	
							atStart = false;	// turn off flag
							setListSize++;
						}
					}
					else if (!expExists)
					{
						// read data for new Experiment object
						String signifChange = resSet.getString("SignifChange");
						double expression = resSet.getDouble("Abundance");
						double expressionSE = resSet.getDouble("AbundanceSE");	
						int signalDetected = resSet.getInt("SignalDetected");
						double enrichment = resSet.getDouble("Enrichment");
						int flyID = resSet.getInt("FlyID");
						
						// construct the Experiment and add to the ExperimentSet
						Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
						set.addExpt(expt);
					}
				}
				// capture the final set
				if(setListSize>0)
				{
					exptSetList[setListSize-1] = set;
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
