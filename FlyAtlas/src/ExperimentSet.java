// Stores an array of 'Experiment' objects and additional information to create a table, e.g. gene symbol, 'Ontology' objects
// Radical modification for normal population of array
// 05.09.2012

public class ExperimentSet 
{
	// variables specified in constructor
	private String fbgn; 
	private String cgNum;
	private String symbol; 
	private String name;
	private String probesetID;
	// the actual array of Experiments this class holds
	private Experiment [] exptList;	
	private final int EXPT_LENGTH = 100;
	private int exptListSize = 0;
	// booleans relating this to others of same Gene but different ProbesetID or same ProbesetID and different gene
	private boolean degenerate;					// is ambiguous
	private boolean duplicate;					// is duplicate
	private boolean bestDuplicate;
	private boolean uniqueDuplicate = false;	// is unique duplicate (i.e. non-degenerate but all mates are degenerate)
	private boolean duplicateChecked;
	// special variables relating only to profile searches
	private double pStat;					// p statistic of correlation with query set
	private double rStat;					// r statistic of correlation with query set
	// variables relating to GO terms associated with the gene concerned in this ExperimentSet
	private Ontology[] goList;				// array of Ontology objects
	private final int GO_LENGTH = 20;		// length of goList for initialization
	private int goSize = 0;					// occupancy of goList 

	public ExperimentSet(String fbgn, String cgNum, String symbol, String name, String probesetID)
	{
		this.fbgn = fbgn;
		this.cgNum = cgNum;
		this.symbol = symbol;
		this.name = name;
		this.probesetID = probesetID;
		
		exptList = new Experiment [EXPT_LENGTH];
		degenerate = false;						// default degen set to false
		duplicate = false;						// default dupl set to false
		bestDuplicate = false;
		duplicateChecked = false;
		goList = new Ontology[GO_LENGTH];
	}
	
	// returns member of ExperimentSet corresponding to a particular flyID
	public Experiment findByID(int id)
	{
		for(int i=0; i<exptListSize; i++)
		{
			Experiment expt = exptList[i];
			if(id == expt.getFlyID())
			{
				return expt;
			}
		}
		return null;
	}

	public void setDegenerate()
	{
		degenerate = true;
	}

	public boolean isDegenerate()
	{
		return degenerate;
	}
	
	public void setUniqueDuplicate(boolean ud)
	{
		uniqueDuplicate = ud;
	}
	
	public boolean isUniqueDuplicate()
	{
		return uniqueDuplicate;
	}

	public String getFBgn()
	{
		return fbgn;
	}

	public String getCGNum()
	{
		return cgNum;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public String getName()
	{
		return name;
	}

	public String getProbesetID()
	{
		return probesetID;
	}
	
	public void addExpt(Experiment expt)
	{
		exptList[exptListSize] = expt;
		exptListSize++;
	}
	
	public int getExptListSize()
	{
		return exptListSize;
	}

	public Experiment getExpt(int pos)
	{
		return exptList[pos];
	}

	public void setDuplicate()
	{
		duplicate = true;
	}

	public void setNotDuplicate()
	{
		duplicate = false;
	}

	public boolean isDuplicate()
	{
		return duplicate;
	}

	public void duplicateChecked()
	{
		duplicateChecked = true;
	}

	public boolean hasBeenChecked()
	{
		return duplicateChecked;
	}

	public void setBestDuplicate()
	{
		bestDuplicate = true;
	}

	public void notBestDuplicate()
	{
		bestDuplicate = false;
	}

	public boolean isBestDuplicate()
	{
		return bestDuplicate;
	}

	// used to determine best duplicate between two ExptSets
	public int getSetAffiCall()
	{
		int total = 0;
		for(int i=0; i<exptListSize; i++)
		{
			total = total + exptList[i].getSignalCount();
		}
		return total;
	}

	// add method for adding GOs to the goList 
	public void addGO(Ontology go)
	{
		if(goSize>goList.length - 1)		// expand array if necessary
		{
			Ontology[] newList = new Ontology[goSize*2];
			System.arraycopy(goList, 0, newList, 0, goSize);
			goList = newList;
		}
		goList[goSize] = go;
		goSize++;
	}

	public Ontology getGO(int pos)
	{
		return goList[pos];
	}

	public int getGOListSize()
	{
		return goSize;
	}

	public void setRstat(double rStat)
	{
		this.rStat = rStat;
	}

	public double getRstat()
	{
		return rStat;
	}

	public void setPstat(double pStat)
	{
		this.pStat = pStat;
	}

	public double getPstat()
	{
		return pStat;
	}
	
	// selection sort of each Experiment in array 
	// first alphabetical on stage (Adult>Larval), and then by listPosition of FlyStage pair
	public void sortForBarchart(FlyTissueList ftl)
	{		
		Experiment lowest;						// holder for first Expt on list
		int lowestPos;
		for(int i=0; i<exptListSize-1; i++)			// do series of runs
		{
			for(int j=i+1; j<exptListSize; j++)		// for each run process list
			{
				lowest = exptList[i];			// assign first of unsorted to lowest
				lowestPos = i;
				
				String lowestStage = ftl.getStageByID(lowest.getFlyID());
				String nextStage = ftl.getStageByID(exptList[j].getFlyID());
				int lowestOrder = ftl.getFlyStagePairByID(lowest.getFlyID()).getListPosition();
				int nextOrder = ftl.getFlyStagePairByID(exptList[j].getFlyID()).getListPosition();
				
				if(lowestStage.compareTo(nextStage) > 1 || 
						(lowestStage.compareTo(nextStage) == 0 && lowestOrder > nextOrder))
				{
					lowestPos = j;
				}			
				lowest = exptList[lowestPos];
				exptList[lowestPos] = exptList[i]; 		// shift current first
				exptList[i] = lowest;					// replace
			}
		}
	}
	
}
