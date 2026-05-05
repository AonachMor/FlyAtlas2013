// Generates an HTML page for a Tissue search, with or without results
// use "ribosome" for testing ambiguity (mRpS14) and duplicates (mRpS11) with heart
// 07.07.2023

import java.util.Arrays;

public class TissuePage extends Page 
{
	private boolean includeErrors;			// show SEs in results
	private boolean includeAmbig;			// include results for ambiguous probe sets
	private boolean includeDuplicates;		// include results for other probe sets that pick up same gene
	private boolean byEnrichment = true;	// enrichment or abundance criterion
	private boolean showOptions = false;	// whether hide/show options div should be visible
	private int displayMax;					// max No. results to display
	private boolean collapsed = true;		// whether results table(s) hide/show is in hidden (collapsed) state
	private int actualCount;
	private FlyTissueList ftList;			// need to draw tables in correct manner and get tissue details
	private int tissueID = -1;				// need as instance variable to highlight in tables
	private int maxList[] = {25, 50, 100, 200, 1000};	// cutoff values for displayMax option
	private final int PAGE_POS = 5;			// position of page in menu
	private StringBuilder htmlBuilder;		// to assemble html output	
	private int totalDisplayed;
	
	// Builds controls, setting fields as appropriate
	private String getControls(String key, String stage, int tissID, boolean atStart)
	{
		String controls1 = "<div id=\"explanation\">For a particular tissue, find which genes in a selected category have the greatest expression.</div>\n" +
							"<div id=\"controls\">" +
							"1. Select a stage and a tissue, and choose either abundance or enrichment." +
							"<p class=\"standard\"><select name=\"stage\" id=\"stage\" onchange=\"processData(); return true;\">" +
							"<option value=\" --- Select a Stage --- \"> --- Select a Stage --- </option>";	
		String stageLine =  new String();	// this sets selected stage for repeat	
		String tissueLine = new String();	// this sets selected tissue if stage set
		String greatLine = 	new String();	// this resets any enhancement/abundance choice	
		String controls5 = 	"<p class=\"standard\">2. Type a term of interest (e.g. wing, kinase) then select from the categories (gene ontologies) in the autosuggest menu.</p>";			
		String fieldLine = 	"<p class=\"standard\"><span class=\"lableftFirst\">Category:</span><input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + key + 
							"\" onkeyup=\"findNames('go');\">\n";
		String maxLine = 	new String();	// sets max cutoff for repeat
		String buttonLine = new String();	// disables button at start, enables at repeat
		String optsLine = 	new String();	// resets visibility of options
		String ambigLine = 	new String();	// resets ambiguity choice
		String dupLine =	new String();	// resets duplicate choice
		String seLine = 	new String();	// sets standard errors for repeat						
		String controls13 = "<div style=\"position:absolute;\" id=\"popup\">\n" +
							"<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n" +         
							"<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n</table>\n</div></div>\n";
		
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
				FlyTissue ft = ftList.getFlyTissue(i);						// get next FlyTissue obj in list
				if(ft.getStage().equals(stage) && ft.getID()==tissID)		// correct stage and repeat id - set selected
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
		
		// set greatLine for enrichment or abundance (extra <p> cf. top)
		StringBuilder greatBuilder = new StringBuilder("<span class=\"lableft breakBefore\">Greatest:</span><select id=\"order\">\n");
		if(byEnrichment)
		{
			greatBuilder.append("<option selected=\"selected\" value=\"enrichment\">Enrichment</option>");
			greatBuilder.append("<option value=\"abundance\">Abundance</option></select></p>\n");
		}
		else
		{
			greatBuilder.append("<option value=\"enrichment\">Enrichment</option>");
			greatBuilder.append("<option selected=\"selected\" value=\"abundance\">Abundance</option></select></p>\n");
		}
		greatLine = greatBuilder.toString();
			
		// generate max options setting selected as required
		StringBuilder maxBuilder = new StringBuilder("<span class=\"lableft\">Display:</span><select " +
													"name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">\n");
		for(int i=0; i<maxList.length; i++)
		{
			int cutoff = maxList[i];
			if(cutoff == displayMax)
			{
				if(cutoff == 1000)
				{
					maxBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">all</option>\n");
				}
				else
				{
					maxBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">" + cutoff + "</option>\n");					
				}
			}
			else
			{
				if(cutoff == 1000)
				{
					maxBuilder.append("<option value=\"" + cutoff + "\">all</option>\n");				
				}
				else
				{
					maxBuilder.append("<option value=\"" + cutoff + "\">" + cutoff + "</option>\n");
				}
			}
		}
		maxBuilder.append("</select>\n");	// finish off select
		maxLine = maxBuilder.toString();

		if(atStart)
		{
			buttonLine = "<button id=\"runButton\" disabled=\"disabled\" onclick=\"sendSearchTissueForm();\">Search</button></p>";
		}
		else
		{
			buttonLine = "<button id=\"runButton\" onclick=\"sendSearchTissueForm();\">Search</button></p>";
		}
		
		// hide/show options visibility
		if(showOptions)
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:block;\"><div></div>";
		}
		else
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:none;\"><div></div>";
		}
		
		// sets ambiguity choice
		if(includeAmbig)
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\" checked=\"checked\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n";
		}
		else
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n";
		}
		
		// sets duplicates choice
		if(includeDuplicates)
		{
			dupLine = "<input type=\"checkbox\" id=\"duplicate\" value=\"duplicate\" checked=\"checked\"> Include duplicate hits <img src=\"buttons/littleD.png\" alt=\"\"><br>\n";
		}
		else
		{
			dupLine = "<input type=\"checkbox\" id=\"duplicate\" value=\"duplicate\"> Include duplicate hits <img src=\"buttons/littleD.png\" alt=\"\"><br>\n";
		}
		
		// set checkbox for standard errors
		if(includeErrors)
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"> Show standard errors</div></div>\n";
		}
		else
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\"> Show standard errors</div></div>\n";
		}
		
		return (controls1 + stageLine + tissueLine + greatLine + controls5 + fieldLine + maxLine + buttonLine + 
						optsLine + ambigLine + dupLine + seLine + controls13);
	}
	
	// Constructor for page WITHOUT results
	public TissuePage(boolean includeAmbig, boolean includeDuplicates, boolean includeErrors)
	{
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;
		
		displayMax =  maxList[0];	// first option is default
		String keyword = "";	// start with empty field
		String stage = "";		// start with select instruction
		int tissID = 0;			// start with no tissue selected
		boolean atStart = true;	// start with search button dimmed
		
		// Build initial page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(keyword, stage, tissID, atStart));
		htmlBuilder.append(pu.getPageFoot());
	}

	// Constructor for page WITH results
	public TissuePage(ExperimentSet[] expSetList, int tissueID, boolean byEnrichment, String keyword, int geneCount, 
							boolean includeAmbig, boolean includeDuplicates, int displayMax, boolean includeErrors, 
							boolean showOptions, String[] ambigFBgnList, FlyTissueList ftList)
	{	
		// tissueID is the flyID for the stage/tissue combination used in the search		
		this.byEnrichment = byEnrichment;
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;
		this.displayMax = displayMax;
		this.showOptions = showOptions;
		this.ftList = ftList;
		this.tissueID = tissueID;
		
		boolean atStart = false;	// flag for any difference from initial page, e.g. whether search button dimmed because no choice made
		
		// get tissue/stage details as strings from FlyID
		String tissueName = ftList.getTissueByID(tissueID);
		String stage = ftList.getStageByID(tissueID);

		// why?
		ExperimentSet[] trimmedSet = new ExperimentSet[geneCount];
		System.arraycopy(expSetList, 0, trimmedSet, 0, geneCount);

		// sort results by enrichment or abundance for tissue/stage, as appropriate
		if (byEnrichment)
		{
			EnrichmentComparator enrichmentComparator = new EnrichmentComparator();
			enrichmentComparator.setID(tissueID);
			Arrays.sort(trimmedSet, enrichmentComparator);	

		}
		else
		{
			AbundanceComparator abundanceComparator = new AbundanceComparator();		// some problem here still with multiple hits and abundance sorting
			abundanceComparator.setID(tissueID);
			Arrays.sort(trimmedSet, abundanceComparator);
		}

		//how many genes to show etc.
		//geneCount= number of hits, actualCount= number of hits minus duplicates (using default settings), totalDisplayed= number shown to user
		if(!includeDuplicates)
		{
			actualCount =getNoDuplicateCount(expSetList, geneCount);
		}
		else
		{
			actualCount = geneCount;
		}

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
		htmlBuilder.append(getControls(keyword, stage, tissueID, atStart));
		htmlBuilder.append("<div class=\"box\">");

		if(actualCount>0)
		{	
			if(actualCount>1)
			{	
				if(totalDisplayed>1)
				{
					// hide and show all buttons
					htmlBuilder.append("<div><img class=\"rightButton\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");
					htmlBuilder.append("<div><img class=\"hiddenRightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");

					if(actualCount < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + actualCount + " genes found using the search term &lsquo;" + keyword + "&rsquo;\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/		
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + actualCount + " genes found using the search term &lsquo;" + keyword + "&rsquo;, " + totalDisplayed + " shown\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/					
					}
				}
				else
				{
					if(actualCount < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + actualCount+" genes found using the search term &lsquo;" + keyword + "&rsquo;\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/				
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + actualCount+" genes found using the search term &lsquo;" + keyword + "&rsquo;, " + totalDisplayed + " shown\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/	
					}
				}
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox2\">"+actualCount+" gene found using the search term &lsquo;"+ keyword + "&rsquo;</div></div>\n");
			}
		}
		if(actualCount==0)
		{
			if(byEnrichment)
			{
				htmlBuilder.append("<div class=\"textBox\">No genes found enriched in &lsquo;" + stage + " " + tissueName + "&rsquo; using the search term &lsquo;" + keyword + "&rsquo;.</div></div>");
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox\">No genes for &lsquo;" + keyword + "&rsquo; more abundant in &lsquo;" + stage + " " + tissueName + "&rsquo;.</div></div>");
			}
		}
		else
		{
			htmlBuilder.append(report);
		}
		
		// report on Ambigs
		if(!includeAmbig && ambigFBgnList != null)
		{
			htmlBuilder.append("<div class=\"textBox\">Ambiguous hits excluded for:<br><div style=\"margin-left:30px;\">");
			for(int i=0; i<ambigFBgnList.length; i++)
			{
				htmlBuilder.append(ambigFBgnList[i] + "<br>");
			}
			htmlBuilder.append("</div></div>");
		}
		
		htmlBuilder.append(pu.getPageFoot());
	}

	public String getReport(ExperimentSet[] trimmedSet, int geneCount)
	{
		StringBuilder geneBuilder = new StringBuilder();	// Builder for accumulating output

		int runningCount = 0;
		//for each set
		for(int i = 0; runningCount < totalDisplayed; i++)
		{
			if(includeDuplicates || (!trimmedSet[i].isDuplicate() || trimmedSet[i].isBestDuplicate()))
			{
				ExperimentSet exptSet = trimmedSet[i];
				appendInfo(geneBuilder, exptSet, runningCount, displayMax, collapsed);
				appendTable(geneBuilder, runningCount, exptSet, collapsed, includeErrors, ftList, tissueID, byEnrichment);	// need to append tissueID to this
				geneBuilder.append("</div>");	//ends gene div

				runningCount++;
			}
		}
		return geneBuilder.toString();
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}
	
}