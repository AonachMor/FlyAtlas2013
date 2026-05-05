// Generates an HTML page for a "Top" search, with or without results
// 08.07.2023

public class TopPage extends Page
{	
	private boolean includeErrors;				// show SEs in results
	private boolean byEnrichment = true;		// enrichment or abundance criterion
	private boolean showOptions = false;		// whether hide/show options div should be visible
	private int displayMax;						// max No. results to display
	private boolean collapsed = true;			// whether results table(s) hide/show is in hidden (collapsed) state
	private int actualCount;					// count after removal of duplicates	
	private FlyTissueList ftList;				// need to draw tables in correct manner and get tissue details
	private int tissueID = -1;					// need as instance variable to highlight in tables
	private int maxList[] = {20, 30, 40, 50};	// cutoff values for displayMax option
	private final int PAGE_POS = 6;				// position of page in menu
	private StringBuilder htmlBuilder;			// for accumulating html output
	private int totalDisplayed;					// number of results displayed to user (can be less than cutoff if fewer found)
	
	// Constructor for page WITHOUT results
	public TopPage(boolean includeErrors)
	{	
		this.includeErrors = includeErrors;
		displayMax =  maxList[0];	// first option is default

		String stage = "";	// start with select instruction
		int tissID = 0;		// start with no tissue selected
		boolean atStart = true;	// start with search button dimmed
		
		// Build initial page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(stage, tissID, atStart));
		htmlBuilder.append(pu.getPageFoot());
	}

	// Constructor for page WITH results
	public TopPage(ExperimentSet[] arraySet, int tissueID, boolean byEnrichment, int geneCount, 
											int mD, boolean includeErrors, boolean sO, FlyTissueList ftList)
	{	
		// tissueID is the specific FlyID for the stage/tissue combination used for the top search
		this.byEnrichment = byEnrichment;
		this.includeErrors = includeErrors;
		displayMax = mD;
		showOptions = sO;
		this.ftList = ftList;
		this.tissueID = tissueID;
		
		boolean atStart = false;	// flag for any difference from initial page, e.g. whether search button dimmed
		
		// get tissue/stage details as strings from tissue (FlyID in DB)
		String tissueName = ftList.getTissueByID(tissueID);
		String stage = ftList.getStageByID(tissueID);
		
		ExperimentSet[] trimmedSet = new ExperimentSet[geneCount];
		System.arraycopy(arraySet, 0, trimmedSet, 0, geneCount);

		//how many genes to show etc.
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
		htmlBuilder.append(getControls(stage, tissueID, atStart));
		
		htmlBuilder.append("<div class=\"box\">");
		
		if(displayMax>1)
		{			
			// hide/show all buttons
			htmlBuilder.append("<div><img class=\"rightButton\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");
			htmlBuilder.append("<div><img class=\"hiddenRightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");
			if(byEnrichment)
			{
				htmlBuilder.append("<div class=\"textBox2\">Top " + displayMax + " most enriched genes in &lsquo;" + stage + " " + tissueName + "&rsquo;\n");
				/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
				htmlBuilder.append(readHTML("htmlText/key.txt"));*/			
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox2\">Top " + displayMax + " most expressed genes in &lsquo;" + stage + " " + tissueName + "&rsquo;\n");
				/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
				htmlBuilder.append(readHTML("htmlText/key.txt"));*/			
			}
		}
		else
		{
			htmlBuilder.append("<div class=\"textBox2\">Top hit for &lsquo;" + stage + " " + tissueName + "&rsquo;\n");
			/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
			htmlBuilder.append(readHTML("htmlText/key.txt"));*/	
		}
		htmlBuilder.append(report);
		htmlBuilder.append(pu.getPageFoot());
	}

	public String getReport(ExperimentSet[] trimmedSet, int geneCount)
	{
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
	private String getControls(String stage, int tissueID, boolean atStart)
	{
		String controls1 = "<div id=\"explanation\">For a particular tissue, find which genes have " +
							"the greatest absolute expression (abundance) or relative expression (enrichment).</div>\n" +
							"<div id=\"controls\">" +
							"Select a stage and a tissue, choose abundance or enrichment, " +
							"how many &lsquo;top genes&rsquo; you wish listed, and press &lsquo;Search&rsquo;." +
							"<p class=\"standard\"><select name=\"stage\" id=\"stage\" onchange=\"processData(); return true;\">" +
							"<option value=\" --- Select a Stage --- \"> --- Select a Stage --- </option>";
		String stageLine =  new String();	// sets selected stage for repeat	
		String tissueLine = new String();	// sets selected tissue if stage set
		String greatLine = 	new String();	// resets any enhancement/abundance choice
		String maxLine = 	new String();	// sets max cutoff for repeat
		String buttonLine = new String();	// disables button at start, enables at repeat
		String optsLine = 	new String();	// resets visibility of options	
		String seLine = 	new String();	// resets standard errors choice					
		String controls9 = "<input type=\"checkbox\" style=\"display:none;\" id=\"duplicate\" value=\"duplicate\">\n" +
							"<input type=\"checkbox\" style=\"display:none;\" id=\"ambiguous\" value=\"ambiguous\">\n</div>\n</div>\n";	
				
		// set stageLine; also for startup set tissueLine
		if(stage.equals("Adult"))
		{
			stageLine = "<option value=\"Adult\" selected=\"selected\">Adult</option><option value=\"Larval\">Larval</option></select>&nbsp;";
		}
		else if (stage.equals("Larval"))
		{
			stageLine = "<option value=\"Adult\">Adult</option><option value=\"Larval\" selected=\"selected\">Larval</option></select>&nbsp;";
		}
		else
		{
			stageLine = "<option value=\"Adult\">Adult</option><option value=\"Larval\">Larval</option></select>&nbsp;";
			tissueLine = "<select name=\"tissue\" id=\"tissue\"><option value=\"0\"> --- First select a Stage --- </option></select>";
		}
		
		// set tissueLine for Adult or Larval
		if(stage.equals("Adult") || stage.equals("Larval"))
		{
			StringBuilder tissBuilder = new StringBuilder("<select name=\"tissue\" id=\"tissue\">");	// starting <select>
			for(int i=0; i<ftList.getSize(); i++)
			{
				FlyTissue ft = ftList.getFlyTissue(i);	// get next FlyTissue obj in list
				if(ft.getStage().equals(stage) && ft.getID() == tissueID)		// correct stage and repeat id - set selected
				{
					tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getID() + "\">" + ft.getTissue() + "</option>");
				}
				else if(ft.getStage().equals(stage))						// correct stage
				{
					tissBuilder.append("<option value=\"" + ft.getID() + "\">" + ft.getTissue() + "</option>");
				}
			}		
			tissBuilder.append("</select>");	// ending </select>
			tissueLine = tissBuilder.toString();
		}
		
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
		
		// sets buttonLine
		if(atStart)
		{
			buttonLine = "<button id=\"runButton\" disabled=\"disabled\" onclick=\"sendSearchTopForm();\">Search</button></p>";
		}
		else
		{
			buttonLine = "<button id=\"runButton\" onclick=\"sendSearchTopForm();\">Search</button></p>";
		}
		
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
		
		return (controls1 + stageLine + tissueLine + greatLine + maxLine + buttonLine + optsLine + seLine + controls9);
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}