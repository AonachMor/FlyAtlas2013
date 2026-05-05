// Sorts ProbeSets on r correlation stat
// 27.08.2012

import java.util.Comparator;

public class CorrelationComparator implements Comparator<Probeset>
{
	public CorrelationComparator()
	{			
	}
	
	 public int compare(Probeset probeSet1, Probeset probeSet2)
	 {     	   
	    //reverse order...
	    if(probeSet1.getRstat() > probeSet2.getRstat())
	    {
	        return -1;
	    }
	    else if(probeSet1.getRstat() < probeSet2.getRstat())
	    {
	        return 1;
	    }
	    else
	    {
	        return 0;    
	    }
	}
}
