// Generates an HTML page for a "Development" search, with or without results
// 08.07.2023

public class DevelopmentPage extends Page
{	
	private boolean includeErrors;				// show SEs in results
	private boolean byEnrichment = true;		// enrichment or abundance criterion
	private boolean showOptions = false;		// whether hide/show options div should be visible
	private int displayMax;						// max No. results to display
	private boolean collapsed = true;			// whether results table(s) hide/show is in hidden (collapsed) state
	private int actualCount;					// count after removal of duplicates	
	private FlyTissueList ftList;				// need to draw tables in correct manner and get tissue details
	private String uniTissue;					// need as instance variable to highlight in tables
	private boolean adultMinusLarval;			// if true, comparison is for adult greater than larval ?
	private int maxList[] = {20, 30, 40, 50};	// cutoff values for displayMax option
	private final int PAGE_POS = 7;				// position of page in menu
	private StringBuilder htmlBuilder;			// for accumulating html output
	private int totalDisplayed;					// number of results displayed to user (can be less than cutoff if fewer found)
	
	
	// Constructor for page WITHOUT results
	public DevelopmentPage(boolean includeErrors, FlyTissueList ftList)
	{	
		this.includeErrors = includeErrors;
		this.ftList = ftList;				// needed to populate menu
		displayMax =  maxList[0];			// first option is default

		boolean adultMinusLarval = true;					// start with Adult > Larval
		
		String uniTissue = new String();
		// assign initial value of uniTissue to first on list (would have been easier not to select I guess)
		for(int i=0; i<ftList.getPairSize(); i++)
		{
			FlyStagePair fsp = ftList.getFlyStagePair(i);	// get next FlyStagePair obj in list
			if(fsp.hasBothStages() == true)
			{
				uniTissue = fsp.getUniTissue();
				break;
			}
		}

		boolean atStart = true;	// start with search button dimmed
		
		// Build initial page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(adultMinusLarval, uniTissue, atStart));
		htmlBuilder.append(pu.getPageFoot());
	}

	// Constructor for page WITH results
	public DevelopmentPage(ExperimentSet[] arraySet, String uniTissue, boolean byEnrichment, int geneCount, 
											int mD, boolean includeErrors, boolean sO, boolean adultMinusLarval, FlyTissueList ftList)
	{	
		this.byEnrichment = byEnrichment;
		this.includeErrors = includeErrors;
		displayMax = mD;
		showOptions = sO;
		this.ftList = ftList;
		this.uniTissue = uniTissue;
		this.adultMinusLarval = adultMinusLarval;
		
		// get tissue names for layout where these differ
		FlyTissue adultTissue = ftList.getFlyTissueByUni(uniTissue, "Adult");
		String adultTissName = adultTissue.getTissue();
		FlyTissue larvalTissue = ftList.getFlyTissueByUni(uniTissue, "Larval");
		String larvalTissName = larvalTissue.getTissue();
		
		String comparitor1 = new String();
		String comparitor2 = new String();
		if(adultMinusLarval)
		{
			comparitor1 = "adult " + adultTissName;
			comparitor2 = "larval " + larvalTissName;
		}
		else
		{
			comparitor1 = "larval " + larvalTissName;
			comparitor2 = "adult "  + adultTissName;
		}
		
		boolean atStart = false;	// flag for any difference from initial page, e.g. whether search button dimmed
		
		ExperimentSet[] trimmedSet = new ExperimentSet[geneCount];
		System.arraycopy(arraySet, 0, trimmedSet, 0, geneCount);

		//geneCount= number of hits, actualCount= number of hits minus duplicates (using default settings), totalDisplayed= number shown to user
		actualCount = getNoDuplicateCount(arraySet, geneCount);
		
		if(displayMax < actualCount)
		{
			totalDisplayed = displayMax;
		}
		else
		{
			totalDisplayed = actualCount;
		}

		//gets report and establishes actual gene count
		String report = new String(getReport(trimmedSet, geneCount));

		// Build the results page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(adultMinusLarval, uniTissue, atStart));
		
		htmlBuilder.append("<div class=\"box\">");
					
		// hide/show all buttons
		htmlBuilder.append("<div><img class=\"rightButton\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");
		htmlBuilder.append("<div><img class=\"hiddenRightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");
		if(byEnrichment)
		{
			htmlBuilder.append("<div class=\"textBox2\">The " + totalDisplayed + " " + comparitor1 + " genes showing most difference in enrichment from " + comparitor2 + ":\n");
/*			htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
			htmlBuilder.append(readHTML("htmlText/key.txt"));	*/	
		}
		else
		{
			htmlBuilder.append("<div class=\"textBox2\">The " + totalDisplayed + " " + comparitor1 + " genes showing most difference in abundance from " + comparitor2 + ":\n");
/*			htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
			htmlBuilder.append(readHTML("htmlText/key.txt"));	*/
		}

		htmlBuilder.append(report);
		htmlBuilder.append(pu.getPageFoot());
	}

	public String getReport(ExperimentSet[] trimmedSet, int geneCount)
	{
		// Set tissue ID to that of dominant comparator for emphasis in table
		String stage = new String();
		if(adultMinusLarval)
		{
			stage = "Adult";
		}
		else
		{
			stage = "Larval";
		}
		FlyTissue ft = ftList.getFlyTissueByUni(uniTissue, stage);
		int tissueID = ft.getID();
		
		StringBuilder geneBuilder = new StringBuilder();	// Builder for accumulating output
		
		int runningCount = 0;
		//for each set
		for(int i = 0; runningCount < totalDisplayed; i++)
		{
			if(!trimmedSet[i].isDuplicate() || trimmedSet[i].isBestDuplicate())
			{
				ExperimentSet exptSet = trimmedSet[i];
				appendInfo(geneBuilder, exptSet, runningCount, displayMax, collapsed);
				appendTable(geneBuilder, runningCount, exptSet, collapsed, includeErrors, ftList, tissueID, byEnrichment);
				geneBuilder.append("</div>");	//ends gene div	
				
				runningCount++;
			}
		}
		return geneBuilder.toString();
	}
	
	// Builds controls, setting fields as appropriate
	private String getControls(boolean adultMinusLarval, String uniTissue, boolean atStart)
	{
		String controls1 = "<div id=\"explanation\">For a particular tissue, find which genes show " +
							"the greatest difference in expression between larva and adult.</div>\n" +
							"<div id=\"controls\">" +
							"1. Select whether expression should be greater in the adult or larval stage.";
		String radioLine = 	new String();	// resets radio button for adult or larval 'dominance'		
		String controls3 =  "2. Choose the tissue, abundance or enrichment, how many genes you wish listed, and press &lsquo;Search&rsquo;.";		
		String tissueLine = new String();	// sets selected uniTissue
		String greatLine = 	new String();	// resets enhancement/abundance choice for repeat	
		String maxLine = 	new String();	// sets max cutoff for repeat
		String buttonLine = "<button onclick=\"sendSearchDevelopmentForm();\">Search</button></p>\n";
		String optsLine = 	new String();	// resets visibility of options	
		String seLine = 	new String();	// resets standard errors choice					
		String controls10 = "<input type=\"checkbox\" style=\"display:none;\" id=\"duplicate\" value=\"duplicate\">\n" +
							"<input type=\"checkbox\" style=\"display:none;\" id=\"ambiguous\" value=\"ambiguous\">\n</div>\n</div>\n";	
		
		// set radioLine
		if(adultMinusLarval)
		{
			radioLine = "<p class=\"standard\"><input type=\"radio\" name=\"devField\" id=\"devAdult\" checked=\"checked\"> Adult > Larval<br>" +
						"<input type=\"radio\" name=\"devField\" id=\"devLarval\"> Larval > Adult<br></p>";
		}
		else
		{
			radioLine = "<p class=\"standard\"><input type=\"radio\" name=\"devField\" id=\"devAdult\"> Adult > Larval<br>" +
					"<input type=\"radio\" name=\"devField\" id=\"devLarval\" checked=\"checked\"> Larval > Adult<br></p>";
		}
		
		// set tissueLine 
		StringBuilder tissBuilder = new StringBuilder("<p class=\"standard\"> Tissue: <select name=\"uniTissue\" id=\"uniTissue\">");	 // starting <select>
		for(int i=0; i<ftList.getPairSize(); i++)
		{
			FlyStagePair fsp = ftList.getFlyStagePair(i);	// get next FlyStagePair obj in list
			if(fsp.getUniTissue().equals(uniTissue) && fsp.hasBothStages() == true)		// corresponding uniTissue - set selected
			{
				tissBuilder.append("<option selected=\"selected\" value=\"" + fsp.getUniTissue() + "\">" + fsp.getUniTissue() + "</option>");
			}
			else if(fsp.hasBothStages() == true)
			{
				tissBuilder.append("<option value=\"" + fsp.getUniTissue() + "\">" + fsp.getUniTissue() + "</option>");
			}
		}		
		tissBuilder.append("</select>&nbsp;");	// ending </select>
		tissueLine = tissBuilder.toString();
		
		// set greatLine for enrichment or abundance
		StringBuilder greatBuilder = new StringBuilder("<span class=\"lableft breakBefore\">Greatest:</span><select id=\"order\">\n");
		if(byEnrichment)
		{
			greatBuilder.append("<option selected=\"selected\" value=\"enrichment\">Enrichment</option>");
			greatBuilder.append("<option value=\"abundance\">Abundance</option></select>\n");
		}
		else
		{
			greatBuilder.append("<option value=\"enrichment\">Enrichment</option>");
			greatBuilder.append("<option selected=\"selected\" value=\"abundance\">Abundance</option></select>\n");
		}
		greatLine = greatBuilder.toString();
		
		// generate max options setting selected as required
		StringBuilder maxBuilder = new StringBuilder("<span class=\"lableft breakBefore\">Display:</span><select " +
													"name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">\n");
		for(int i=0; i<maxList.length; i++)
		{
			int cutoff = maxList[i];
			if(cutoff == displayMax)
			{
					maxBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">" + cutoff + "</option>\n");					
			}
			else
			{
					maxBuilder.append("<option value=\"" + cutoff + "\">" + cutoff + "</option>\n");
			}
		}
		maxBuilder.append("</select>\n");	// finish off select
		maxLine = maxBuilder.toString();
		
		// hide/show options visibility
		if(showOptions)
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:block;\"><div>\n";
		}
		else
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:none;\"><div>\n";
		}
		
		// set standard errors checkbox choice
		if(includeErrors)
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"> Show standard errors<br></div>\n";
		}
		else
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\"> Show standard errors<br></div>\n";
		}
		
		return (controls1 + radioLine + controls3 + tissueLine + greatLine + maxLine + buttonLine + optsLine + seLine + controls10);
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}