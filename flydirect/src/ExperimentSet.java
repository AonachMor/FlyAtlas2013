// Stores an array of 'Experiment' objects and additional information to create a table, e.g. gene symbol, 
// Could have lots more stuff cut from it
// 11.10.2012

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
	
	// build XML <gene> tag with attributes
	public String getGeneXMLtag()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<gene");
		builder.append(" fbgn=\"" + fbgn + "\"");
		builder.append(" cgnum=\"" + cgNum + "\"");
		builder.append(" symbol=\"" + symbol + "\"");
		builder.append(" name=\"" + name + "\"");
		builder.append(">");
		return builder.toString();
	}
	
	// Returns a set of either adult or larval xml tissue elements
	public String getTissueXML(String stage)
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(int i=0;i<exptListSize; i++)
		{
			Experiment expt = exptList[i];
			if(expt.getStage().equals(stage))
			{
				if(first==false)
				{
					builder.append("\n");
				}
				builder.append("<tissue type=\"" + expt.getTissue() + "\">\n");
					builder.append("<abundance>" + expt.getAbundance() + "</abundance>\n");
					builder.append("<se>" + expt.getAbundanceSE() + "</se>\n");
					builder.append("<enrichment>" + expt.getEnrichment() + "</enrichment>\n");
					builder.append("<change>" + expt.getSignifChange() + "</change>\n");
					builder.append("<replicates>" + expt.getSignalCount() + "</replicates>\n");
				builder.append("</tissue>");
				first = false;
			}
		}		
		return builder.toString();
	}
	
	// Formats for tab-separated plain text output
	public String getTextFormatted()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(probesetID + "\n");
		builder.append(fbgn + "\n");
		builder.append(cgNum + "\n");
		builder.append(symbol + "\n");
		builder.append(name + "\n");
		for(int i=0; i<exptListSize; i++)
		{
			builder.append(exptList[i].toString() + "\n");
		}
		return builder.toString();
	}
	
}
