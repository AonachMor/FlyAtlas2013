// Sorts ExperimentSets by abundance for a particular stage/tissue
// 29.08.2012

import java.util.Comparator;

public class AbundanceComparator implements Comparator<ExperimentSet>
{	
	int flyID = -1;		// flyID specifying stage/tissue combination
	
	public AbundanceComparator()
	{	
	}

    public int compare(ExperimentSet experimentSet1, ExperimentSet experimentSet2)
    {
    	double express1;
    	double express2;
	    express1 = experimentSet1.findByID(flyID).getAbundance();   	
	    express2 = experimentSet2.findByID(flyID).getAbundance();        
	    	
	   // reverse order...
	    if(express1 > express2)
	    {
	        return -1;
	    }
	    else if(express1 < express2)
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
