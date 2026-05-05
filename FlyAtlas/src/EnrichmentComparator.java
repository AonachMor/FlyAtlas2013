// Sorts ExperimentSets by enrichment for a particular tissue/stage
// 20.08.2012

import java.util.Comparator;

public class EnrichmentComparator implements Comparator<ExperimentSet>
{
	int flyID = -1;		// flyID specifying stage/tissue combination
	
	public EnrichmentComparator()
	{	
	}

    public int compare(ExperimentSet experimentSet1, ExperimentSet experimentSet2)
    {	   
    	double enrich1;
    	double enrich2;
    	enrich1 = experimentSet1.findByID(flyID).getEnrichment();       	
    	enrich2 = experimentSet2.findByID(flyID).getEnrichment();
	    	
	    //reverse order...
	    if(enrich1 > enrich2)
	    {
	        return -1;
	    }
	    else if(enrich1 < enrich2)
	    {
	    	return 1;
	    }
	    else
	    {
	        return 0; 
	    }
    }
    
    public void setID(int id)
    {
    	flyID = id;
    }

}
