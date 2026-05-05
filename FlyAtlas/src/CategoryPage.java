// Generates an HTML page for a Category (Gene Ontology) search, with or without results (was GoPage)
// use "mitochondrial envelope" for testing ambiguity and duplicates, 0000285 for testing GO ambiguity
// DPL 20.09.2023

public class CategoryPage extends Page 
{
	private boolean includeErrors; 			// show SEs in results
	private boolean includeAmbig; 			// include results for ambiguous probe sets
	private boolean includeDuplicates;		// include results for other probe sets that pick up same gene
	private boolean showOptions = false;	// whether hide/show options div should be visible
	private int displayMax;					// max No results to display
	private int totalDisplayed;				// actual No results displayed (can be less than max)
	private int maxList[] = {25, 50, 100, 200, 1000};	// cutoff values for displayMax option
	private String radioSelect;				// value of radio button for type of category (GO) search (term, id or free)
	private boolean collapsed = true;		// whether results table(s) hide/show is in hidden (collapsed) state
	private int trimmedSetNum = 0;			// number of sets in results after removing (or not) ambiguous or duplicate members
	private FlyTissueList ftList;			// need to draw tables in correct manner	
	private final int PAGE_POS = 3;			// Position of page in menu
	private StringBuilder htmlBuilder;		// For building html

	
	// Instantiate initial page
	public CategoryPage(boolean includeAmbig, boolean includeDuplicates, boolean includeErrors)
	{
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;

		// Build initial page
		displayMax =  maxList[0];	// first option is default
		radioSelect = "goTerm";		// default
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(""));
		htmlBuilder.append(pu.getPageFoot());
	}

	// Instantiate a results page
	public CategoryPage(ExperimentSet[] exptSetList, int numExptSets, String keyword, boolean includeAmbig, boolean includeDuplicates, boolean includeErrors, 
									int displayMax, String radioSelect, boolean showOptions, String[] ambigFBgnList, FlyTissueList ftList)
	{
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;
		this.displayMax = displayMax;
		this.showOptions = showOptions;
		this.radioSelect = radioSelect;	
		this.ftList = ftList;

		// calculate No. of results to show, depending on whether duplicates excluded and what displayMax cutoff is
		if(!includeDuplicates)
		{
			trimmedSetNum = getNoDuplicateCount(exptSetList, numExptSets);
		}
		else
		{
			trimmedSetNum = numExptSets;
		}
		
		if(displayMax < trimmedSetNum)
		{
			totalDisplayed = displayMax;
		}
		else
		{
			totalDisplayed = trimmedSetNum;
		}

		// Start building the results page
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(keyword));		
		htmlBuilder.append("<div class=\"box\">");

		// This sends results to be formatted after removing ambiguous/duplicate results - getReport() method therefore may alter trimmedSetNum
		String report = new String(getReport(exptSetList));
		if(trimmedSetNum > 0)
		{
			if(trimmedSetNum > 1)
			{	
				//hide and show all buttons
				htmlBuilder.append("<div class=\"box\"><div><img class=\"rightButton\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");
				htmlBuilder.append("<div><img class=\"hiddenRightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");

				if(totalDisplayed > 1)
				{
					if(trimmedSetNum < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " genes found using the search term &lsquo;" + keyword + "&rsquo;\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));	*/			
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " genes found using the search term &lsquo;" + keyword + "&rsquo;, " + totalDisplayed + " shown\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/				
					}
				}
				else
				{
					if(trimmedSetNum < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum +" genes found using the search term &lsquo;" + keyword + "&rsquo;\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));	*/				
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum +" genes found using the search term &lsquo;" + keyword + "&rsquo;, " + totalDisplayed + " shown\n");
						/*htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div></div>\n");
						htmlBuilder.append(readHTML("htmlText/key.txt"));*/				
					}
				}
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " gene found using the search term &lsquo;"+ keyword + "&rsquo;</div></div>\n");
			}
		}
		if(trimmedSetNum == 0)
		{
			htmlBuilder.append("<div class=\"textBox\">No genes found using the search term &lsquo;" + keyword + "&rsquo;.</div>");
		}
		
		htmlBuilder.append(report);		
		
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

	// NB unlike gene method doesn't use setsize as uses pre-computed totalDisplayed
	public String getReport(ExperimentSet[] exptSetList)
	{
		StringBuilder geneBuilder = new StringBuilder();	// Builder for accumulating output
		int currentSetNum = 0;		// variable distinct from trimmedSetNum needed here (contrast gene) as cut-off reduces number sent

		//for each set
		for(int i = 0; currentSetNum < totalDisplayed; i++)
		{
			if(includeDuplicates || (!exptSetList[i].isDuplicate() || exptSetList[i].isBestDuplicate()))
			{
				ExperimentSet exptSet = exptSetList[i];
				appendInfo(geneBuilder, exptSet, currentSetNum, displayMax, collapsed);
				appendTable(geneBuilder, currentSetNum, exptSet, collapsed, includeErrors, ftList);
				geneBuilder.append("</div>");	//ends gene div

				currentSetNum++;
			}
		}
		return geneBuilder.toString();
	}

	// Builds controls, setting fields as appropriate
	private String getControls(String key)
	{
		String controls1 = "<div id=\"explanation\">Find how individual genes belonging to a particular category are expressed in different tissues.</div>\n" +
								"<div id=\"controls\">\n";
		String radioLine = 	new String();		// resets gene specification method radio button	
		String fieldLine = "<p class=\"standard\"><span class=\"lableftFirst\">Term:</span><input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + key + 
							"\" onkeyup=\"findNames('go');\">\n";
		String maxLine = 	new String();		// sets max cutoff for repeat						
		String controls4 = "<button onclick=\"sendSearchGoForm();\">Search</button></p>\n";
		String optsLine = 	new String();		// retains visibility of hide/show options
		String ambigLine = 	new String();		// resets ambiguity choice
		//String rgLine = 	new String();		// resets colour choice				
		String dupLine = 	new String();		// include results for other probe sets that pick up same gene 
		String seLine =		new String(); 		// resets any include standard errors choice			
		String controls10 = "<div style=\"position:absolute;\" id=\"popup\">\n" +
							"<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n" +         
							"<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n</table>\n</div></div>\n";
		
		// set radio button selection from radio parameter
		StringBuilder radioBuilder = new StringBuilder("");
		if(radioSelect.equals("goTerm"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goTerm\" checked=\"checked\"> Category " +
					"<span class=\"mobileHide\">(Type a term of interest &mdash; e.g. &lsquo;wing&rsquo; or &lsquo;kinase&rsquo; &mdash; then select from the autosuggest menu.)</span><br>\n");
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goTerm\"> Category " +
					"<span class=\"mobileHide\">(Type a term of interest &mdash; e.g. &lsquo;wing&rsquo; or &lsquo;kinase&rsquo; &mdash; then select from the autosuggest menu.)</span><br>\n");
		}
		if(radioSelect.equals("goID"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goID\" checked=\"checked\"> Gene Onotology ID <span class=\"mobileHide\">(e.g. 0005201)</span><br>\n");
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goID\"> Gene Onotology ID <span class=\"mobileHide\">(e.g. 0005201)</span><br>\n");
		}		
		if(radioSelect.equals("goFree"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goFree\" checked=\"checked\"> Free Search " +
					"<span class=\"mobileHide\">(Type a partial or complete term &mdash; e.g. &lsquo;mitochon&rsquo;. No autosuggest, all matching categories searched.)</span><br>\n");			
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"goField\" id=\"goFree\"> Free Search " +
					"<span class=\"mobileHide\">(Type a partial or complete term &mdash; e.g. &lsquo;mitochon&rsquo;. No autosuggest, all matching categories searched.)</span><br>\n");
		}
		radioLine = radioBuilder.toString();
		
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

		// set visibility of options hide/show div
		if(showOptions)
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:block;\"><div></div>";
		}
		else
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:none;\"><div></div>";
		}
		
		// set checkbox for ambiguity choice
		if(includeAmbig)
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\" checked=\"checked\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n";
		}
		else
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n";
		}	
		
		// set checkbox for duplicate choice
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
		
		StringBuilder controlBuilder = new StringBuilder(controls1 + radioLine + fieldLine + maxLine + controls4 + optsLine +
																ambigLine + dupLine + seLine + controls10);
		return controlBuilder.toString();
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}