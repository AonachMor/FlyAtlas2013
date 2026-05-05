// Sorts Probesets by developmental difference in a particular tissue
// 11.09.2012

import java.util.Comparator;

public class DevelopmentComparator implements Comparator<DevelopmentProbeset>
{		
	public DevelopmentComparator()
	{	
	}

    public int compare(DevelopmentProbeset probeset1, DevelopmentProbeset probeset2)
    {	
    	double value1 = probeset1.getValueDifference();
    	double value2 = probeset2.getValueDifference();
    
	   //reverse order...
	    if(value1 > value2)
	    {
	        return -1;
	    }
	    else if(value1 < value2)
	    {
	        return 1;
	    }
	    else
	    {
	        return 0; 
	    }	    
    }
}