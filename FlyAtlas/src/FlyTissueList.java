/*
	FlyTissueList
	Class for maintaining an array of FlyTissue objects, with associated accessor methods
	Also holds an ordered list of adult/larval Pairs for laying out tables
	DPL 27.08.2012 / 16.11.2015
*/
import java.sql.*;
import java.io.*;

public class FlyTissueList
{
	private FlyTissue[] flyTissues;			// array of FlyTissue objects
	private int LIST_LENGTH = 100;			// length of array (actually only needs 25, so should be ok for a while)
	private int listSize = 0;				// occupancy
	
	private FlyStagePair[] flyPairs;		// array of FlyStagePair objects
	private int PAIR_LIST_LEN = 100;		// length of array (actually only needs 25, so should be ok for a while)
	private int pairListSize = 0;			// occupancy
	private final String LIST_FILE = "files/uniTissues.txt";		// file that has list of tissues with common name for table, in order they should appear
	
	// constructor creates array of FlyTissue objects by SQL call to the FlyAtlas DB
	public FlyTissueList()
	{
		flyTissues = new FlyTissue[LIST_LENGTH];
		populateList();
		//must follow population of FlyTissue list
		flyPairs = new FlyStagePair[PAIR_LIST_LEN];
		populatePairList();
	}
	
	// query database to populate ordered list of FlyTissue objects
	private void populateList()
	{		
		String query = DBQuery.getFlyTissueQuery();
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		try 
		{		
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					int id = resSet.getInt("FlyID");
					String stage = resSet.getString("Stage");
					String tissue = resSet.getString("Tissue");
					String sex = resSet.getString("Sex");
					String uniTissue = resSet.getString("UniTissue");
					FlyTissue next = new FlyTissue(id, stage, tissue, sex, uniTissue);
					flyTissues[listSize] = next;
					listSize++;
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
	
	// Read file of uniTissues and use to create FlyStagePairs added in order
	private void populatePairList()
	{
		StreamFile sf = new StreamFile(LIST_FILE, false);
		BufferedReader bf = sf.getReader();
		try
		{
    		if(bf != null)
    		{
    			String line;
    			while((line = bf.readLine()) != null)
    			{
    				// search through flyTissues for Adult and Larval uniTissue and construct object
    				FlyTissue adultTiss = getFlyTissueByUni(line, "Adult");
    				FlyTissue larvalTiss = getFlyTissueByUni(line, "Larval");
    				FlyStagePair pair = new FlyStagePair(line, adultTiss, larvalTiss, (pairListSize + 1));
					flyPairs[pairListSize] = pair;
					pairListSize++;  
    			}
    		}
		}
		catch(IOException ioe)
		{
			System.out.println("Problem reading " + LIST_FILE);
		}
	}
	
    	/* Accessor methods for FLY TISSUE list */
	
    // number of tissues (i.e. occupancy of array) - replaced previous getTissueNumber from TissueCounter
    public int getSize()
    {
    	return listSize;
    }
    
    // returns FlyTissue object at a given position in the array
    public FlyTissue getFlyTissue(int pos)
    {
    	return flyTissues[pos];
    }
    
    // returns FlyTissue object corresponding to a uniTissue and stage
    public FlyTissue getFlyTissueByUni(String uniTissue, String stage)
    {
        for(int i=0; i<listSize; i++)
        {
            String ut = flyTissues[i].getUniTissue();
            String st = flyTissues[i].getStage();
            if(ut.equals(uniTissue) && st.equals(stage))
            {
            	return flyTissues[i];
            }
        } 
        return null;
    }
    
    	/* Accessor methods replacing previous queries */
    
    // allows stage description to be retrieved for an id
    public String getStageByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getID();
            if(id == flyID)
            {
            	return flyTissues[i].getStage();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
    
    // allows tissue name to be retrieved for an id
    public String getTissueByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getID();
            if(id == flyID)
            {
            	return flyTissues[i].getTissue();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
  
    // allows UniTissue name to be retrieved for an id
    public String getUniTissueByID(int id)
    {
        for(int i=0; i<listSize; i++)
        {
            int flyID = flyTissues[i].getID();
            if(id == flyID)
            {
            	return flyTissues[i].getUniTissue();
            }
        }
        return "none";	// backstop that won't throw an npe
    }
    
				/* FLY STAGE PAIR list Accessor methods */
	
    // number of pairs (ie occupany of array - gives number of lines in table)
    public int getPairSize()
    {
    	return pairListSize;
    }
    
    // returns FlyTissue obj at a given pos in the array
    public FlyStagePair getFlyStagePair(int pos)
    {
    	return flyPairs[pos];
    }
    
    // returns a FlyTissue object if either adult or larval component ID matches search ID
    public FlyStagePair getFlyStagePairByID(int id)
    {
    	 for(int i=0; i<pairListSize; i++)
    	{	
    		if(flyPairs[i].getAdultTissue() != null &&
    				flyPairs[i].getAdultTissue().getID() == id)
    		{
    			return flyPairs[i];
    		}
    		else if(flyPairs[i].getLarvalTissue() != null &&
    				flyPairs[i].getLarvalTissue().getID() == id)
    		{
    			return flyPairs[i];
    		}
    	}
    	return null;
    }
    
}
