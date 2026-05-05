// Searches database for genes by a GO identifier or search term that picks up GOs by their description (previously GoSearch) 
// Puts results in an array of ExperimentSets (i.e. group of Experiment objects, one per tissue/stage combination) 
// 05.09.2012 

import java.sql.*;

public class CategorySearch 
{
	private ExperimentSet [] exptSetList;		// holds all the ExperimentSets found from query
	private int LIST_LENGTH = 5000;				// length of exptSetList - another wing and a prayer number
	private int setListSize = 0;				// occupancy of exptSetList (was geneCount)

	public CategorySearch(String keyword, String radioChoice)
	{
		exptSetList = new ExperimentSet [LIST_LENGTH];	
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = null;
		
		if(radioChoice.equals("goTerm"))
		{
			parQ = DBQuery.getParamQuery("CAT_BY_DESCRIP");
		}
		else if(radioChoice.equals("goID"))
		{
			parQ = DBQuery.getParamQuery("CAT_BY_GONUM");
		}
		else if(radioChoice.equals("goFree"))
		{
			parQ = DBQuery.getParamQuery("CAT_BY_FRAG");
		}
		else
		{
			System.out.println("Error in radio choice at GOSearch");
		}
		
		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			
			if(radioChoice.equals("goFree"))
			{
				prepStat.setString(1, "%"+keyword+"%");		// LIKE search in both directions
			}
			else
			{
				prepStat.setString(1, keyword);				// exact searches
			}
			ResultSet resSet = prepStat.executeQuery();	
			ExperimentSet expSet = null;					// sets up ExperimentSet to hold Expts
			
			// Variables are external to loop to allow detection of a new set of Expts...
			String fbgn = "";
			String probesetID = "";
			// ...or new GO  
			String goID = "";
			
			boolean expExists = false;	// flag that expts corresponding to FBgn and probesetID read are already present
						
			boolean atStart = true;	// to mark the situation when the ResultSet is first read
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					if(atStart || 
							// fbgn-probeset different
							(!resSet.getString("FBgn").equals(fbgn) || !resSet.getString("ProbesetID").equals(probesetID)) || 
							// gene ont different 
							(!resSet.getString("GONum").equals(goID)))
					{
						// store any previously built expSet from the last 25 (or whatever) loops
						if(!atStart)
						{
							exptSetList[setListSize-1] = expSet;
						}				
						// read and change values of FBgn and probesetID
						fbgn = resSet.getString("FBgn");
						probesetID = resSet.getString("ProbesetID");
						// read gene ont variables (and change value of GO ID)
						goID = resSet.getString("GONum");
						String goType = resSet.getString("GOType");
						String goDescript = resSet.getString("GODescript");

						boolean setExists = false;		// set flag
						expExists = false;

						for (int i = 0; i < setListSize; i++)
						{
							// check if exptSet for this FBgn/probesetID exists and if so create GO object and add it ('if' must have detected new GO)
							if(fbgn.equals(exptSetList[i].getFBgn()) && probesetID.equals(exptSetList[i].getProbesetID()))
							{						
								setExists = true;			// change flag	
								expExists = true;
								Ontology go = new Ontology(goID, goType, goDescript);
								exptSetList[i].addGO(go);	// add a new GO object				
								break;						// because no point looking for match further in exptSetList
							}
						}
						
						if(!setExists)				// construct new ExptSet ('if' must have detected new FBgn/probesetID)
						{
							String cgNum = resSet.getString("CGNum");
							String symbol = resSet.getString("Symbol");
							String name = resSet.getString("Name");
							// construct ExptSet
							expSet = new ExperimentSet(fbgn, cgNum, symbol, name, probesetID);
							
							// determine whether degenerate probe and set boolean in ExptSet object
							if (resSet.getInt("ProbeDegeneracy") == 1)
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
							// construct GO object and add to new exptSet
							Ontology go = new Ontology(goID, goType, goDescript);
							expSet.addGO(go);
	
							// now read data for first Expt object of new ExptSet 
							String signifChange = resSet.getString("SignifChange");
							double expression = resSet.getDouble("Abundance");
							double expressionSE = resSet.getDouble("AbundanceSE");				
							int signalDetected = resSet.getInt("SignalDetected");
							double enrichment = resSet.getDouble("Enrichment");
							int flyID = resSet.getInt("FlyID");
							
							// construct the Experiment and add to the ExperimentSet
							Experiment expt = new Experiment(signifChange, expression, expressionSE, signalDetected, enrichment, flyID);
							expSet.addExpt(expt);
	
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
						expSet.addExpt(expt);
					}
				}
				// capture the final set
				if(setListSize > 0)
				{
					exptSetList[setListSize-1] = expSet;
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

	public int getListSize()
	{
		return setListSize;
	}

	public ExperimentSet[] getExptSetList()
	{
		return exptSetList;
	}
}
