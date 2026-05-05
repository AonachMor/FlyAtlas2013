// This models all the probesets EXCEPT A SINGLE QUERY PROBESET in an array of Probeset objects (formerly GetProbeSets)
// Used by ProfileSearch
// 05.09.2012

import java.sql.*;

public class ProbesetList 
{
	private Probeset[] probeList;
	private int probeListSize = 0;
	private int LIST_LENGTH = 15000;
	
	public ProbesetList(String queryID)
	{	
		probeList = new Probeset [LIST_LENGTH];
		populateList(queryID);
	}
	
	private void populateList(String queryID)
	{
		String query = DBQuery.getProbesetQuery();		// ProbesetID, Abundance, SignalDetected, FlyID
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			
			Probeset probe = null;		// sets up Probeset to which Abundance objects will be added for ea tissue/stage

			// Variable external to loop to allow detections of a new group of ProbeSetIDs from experiment table
			String probesetID = "";			
			
			boolean atStart = true;	// to mark the situation when the ResultSet is first read
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					if(!resSet.getString(1).equals(queryID))					// if it's not the query probe set
					{
						// checks if new set by by initial val of atStart or change in value of probesetID (String 1)
						if(atStart || !resSet.getString(1).equals(probesetID))
						{
							// store any previously built Probeset from the last 25 (or whatever) loops
							if(!atStart)
							{
								probeList[probeListSize - 1] = probe;
							}												
							// read this first probesetID and use it to construct the new Probeset
							probesetID = resSet.getString("ProbesetID");						
							probe = new Probeset(probesetID);
							// read other variables and use to construct Abundance object which is added to the Probeset object
							double abundance = resSet.getDouble("Abundance");
							int signalDetected = resSet.getInt("SignalDetected");
							int flyID = resSet.getInt("FlyID");
							
							Abundance abund = new Abundance(abundance, signalDetected, flyID);
							probe.addAbundance(abund);
							
							atStart = false;
							probeListSize++;
						}
						else
						{	
							// read variables needed to construct Abundance object to add to the Probeset object
							double abundance = resSet.getDouble("Abundance");
							int signalDetected = resSet.getInt("SignalDetected");
							int flyID = resSet.getInt("FlyID");
							
							Abundance abund = new Abundance(abundance, signalDetected, flyID);
							probe.addAbundance(abund);
						}
					}
				}
				// this adds final Probeset object to the list after exiting while loop 
				if(probeListSize > 0)
				{
					probeList[probeListSize-1] = probe;	
				}
			}
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
	
	// getters for instance variables
	
	public int getListSize()
	{
		return probeListSize;	// should be one less than the total number of probesets in the db
	}
	
	public Probeset[] getList()
	{
		return probeList;
	}

}
