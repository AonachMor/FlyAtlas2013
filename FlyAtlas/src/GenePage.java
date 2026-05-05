// Generates an HTML page for a Gene search, with or without results
// use "Tyler" for testing ambiguity and "Bmcp" for duplicates, sesB for unique duplicates
// DPL 17.07.2023

public class GenePage extends Page 
{
	private String geneQuery;				// gene search term
	private boolean includeErrors; 			// show SEs in results
	private boolean includeAmbig;			// include results for ambiguous probe sets 
	private boolean includeDuplicates;		// include results for other probe sets that pick up same gene
	private boolean showOptions = false;	// whether options hide/show div should be visible
	private int displayMax = 50;			// = all as only likely to be a couple of genes with duplicates
	private String radioSelect;				// value of radio button for type of gene search (symbol, name etc)
	private boolean collapsed = false;		// whether results table(s) hide/show is in hidden (collapsed) state
	private int trimmedSetNum = 0;			// number of sets in results after removing (or not) ambiguous or duplicate members
	private FlyTissueList ftList;			// need to draw tables in correct manner	
	private final int PAGE_POS = 2;			// Position of page in menu	
	private StringBuilder htmlBuilder;		// For building html
	
	// Instantiate initial page
	public GenePage(boolean includeAmbig, boolean includeDuplicates, boolean includeErrors)
	{
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;			// consider whether to set this in constructor at startup 
				
		// Build initial page
		radioSelect = "geneSymbol";		// default radio button selection
		geneQuery = "";					// empty field initially	
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(geneQuery));
		htmlBuilder.append(pu.getPageFoot());
	}

	// Instantiate a results page
	public GenePage(ExperimentSet[] exptSetList, int numExptSets, String geneQuery, boolean includeAmbig, boolean includeDuplicates, boolean includeErrors,
											int displayMax, String radioSelect, boolean showOptions, boolean ambigRemoved, FlyTissueList  ftList)
	{
		this.includeAmbig = includeAmbig;
		this.includeDuplicates = includeDuplicates;
		this.includeErrors = includeErrors;
		this.displayMax = displayMax;
		this.showOptions = showOptions;
		this.geneQuery = geneQuery;		// search gene term
		this.radioSelect = radioSelect;	// radio button checked for type of gene selection
		this.ftList = ftList;			// reference list from fly table
			
		// Start building the results page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));	
		htmlBuilder.append(getControls(geneQuery));	// send search term so can be set in results page
		
		// This sends results to be formatted after removing ambiguous/duplicate results - getReport() method therefore may alter trimmedSetNum
		String report = new String(getReport(exptSetList, numExptSets));
		if(trimmedSetNum > 0)
		{	
			if(trimmedSetNum>1)
			{
				// show all button
				htmlBuilder.append("<div class=\"box\"><img class=\"rightButton\" style=\"display:none;\" id=\"showAllButton\" onclick=\"javascript:showAllTables();\"  src=\"buttons/allShowButton.png\" alt=\"show all tables\">");
				htmlBuilder.append("<img class=\"rightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\">");
				// results count
				htmlBuilder.append("<div class=\"textBox2\">Results for " + trimmedSetNum + " probe sets that hybridize to &lsquo;" + geneQuery + "&rsquo;\n");
/*				htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div>\n");
				htmlBuilder.append(readHTML("htmlText/key.txt"));*/
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox2\">Results found for &lsquo;" + geneQuery + "&rsquo;.\n");
/*				htmlBuilder.append("<div style=\"float:right; padding-right:15px;\"><a href=\"javascript:hideShowIS('indexWin');\" title=\"Key to Results\"><img src=\"buttons/key.gif\" alt=\"\" id=\"expand\"></a></div></div>\n");
				htmlBuilder.append(readHTML("htmlText/key.txt"));	*/
			}
		}
		
		if(trimmedSetNum == 0)	// no hits or single ambiguous result removed
		{		
			htmlBuilder.append("<div class=\"box\"><div class=\"textBox\">");
			if(ambigRemoved)
			{
				htmlBuilder.append("<img src=\"buttons/A5.png\" alt=\"\" style=\"vertical-align:bottom;\"> &lsquo;" + geneQuery + "&rsquo; is only represented by an ambiguous probe set. " +
						"Should you still wish to see the results for this probe set, select &lsquo;Include ambiguous hits&rsquo; from &lsquo;Options&rsquo; (above) and re-run the query.");
			}
			else
			{
				htmlBuilder.append("No genes corresponding to &lsquo;" + geneQuery + 
						"&rsquo;. Please select gene identifiers from the autosuggest menu " +
						"(remember searches are case-sensitive).");
			}
			htmlBuilder.append("</div></div>");
		}
		else
		{
			htmlBuilder.append(report);
		}
		htmlBuilder.append(pu.getPageFoot());
	}

	public String getReport(ExperimentSet[] exptSetList, int numExptSets)
	{
		StringBuilder geneBuilder = new StringBuilder();	// Builder for accumulating output

		// for each ExptSet in exptSetList - counter i is passed to append methods for setListPos argument
		for(int i = 0; i < numExptSets; i++)
		{
			if(includeDuplicates || (!exptSetList[i].isDuplicate() || exptSetList[i].isBestDuplicate()))
			{
				ExperimentSet exptSet = exptSetList[i];
				appendInfo(geneBuilder, exptSet, trimmedSetNum, displayMax, collapsed);
				appendTable(geneBuilder, trimmedSetNum, exptSet, collapsed, includeErrors, ftList);		
				geneBuilder.append("</div>");	// ends gene div			
				trimmedSetNum++;				// increment count of trimmed sets
			}
		}
		return geneBuilder.toString();
	}
	
	// Builds controls for page, setting fields as appropriate
	private String getControls(String geneQuery)
	{
		String controls1 = "<div id=\"explanation\">For a particular gene, find the pattern of expression in different tissues.</div>\n" +
								"<div id=\"controls\">";		
		String radioLine = 	new String();		// resets gene specification method radio button
		String fieldLine = "<p class=\"standard\"><span class=\"lableftFirst\">Gene:</span><input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + geneQuery + 
							"\" style=\"height:15px;\" onkeyup=\"findNames('gene');\">";		
		String controls4 = "<button onclick=\"sendSearchGeneForm();\">Search</button></p>\n";
		String optsLine = 	new String();		// resets visibility of options
		String ambigLine = 	new String();		// resets ambig choice
		String dupLine = 	new String();		// resets duplicates choice
		String seLine = 	new String();		// resets any include standard errors choice
		String controls9 = "<input type=\"text\" style=\"display:none;\" id=\"maxdisplayed\" value=\"30\">\n</div>\n</div>\n" +					
				"<div style=\"position:absolute;\" id=\"popup\">\n" +
				"<table id=\"menuTable\" class=\"revertFont\" cellspacing=\"0\" cellpadding=\"0\">\n" +         
				"<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody></table></div>\n</div>\n";

		// set radio button selection from radio parameter
		StringBuilder radioBuilder = new StringBuilder("");
		if(radioSelect.equals("geneSymbol"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"geneSymbol\" checked=\"checked\"> Gene Symbol (e.g. vkg) " +
					"<span class=\"mobileHide\">&mdash; start typing, then select from the autosuggest menu</span><br>\n");
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"geneSymbol\"> Gene Symbol (e.g. vkg) " +
					"<span class=\"mobileHide\">&mdash; case-sensitive: start typing, then select from the autosuggest menu</span><br>\n");
		}
		if(radioSelect.equals("geneName"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"geneName\" checked=\"checked\"> Gene Name (e.g. viking)<br>\n");
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"geneName\"> Gene Name (e.g. viking)<br>\n");
		}		
		if(radioSelect.equals("CGnum"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"CGnum\" checked=\"checked\"> Annotation symbol (e.g. CG16858)<br>\n");			
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"CGnum\"> Annotation symbol (e.g. CG16858)<br>\n");
		}
		if(radioSelect.equals("FBgene"))
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"FBgene\" checked=\"checked\"> FlyBase ID (e.g. FBgn0016075)<br>\n");		
		}
		else
		{
			radioBuilder.append("<input type=\"radio\" name=\"geneField\" id=\"FBgene\"> FlyBase ID (e.g. FBgn0016075)<br>\n");
		}		
		radioLine = radioBuilder.toString();
		
		// hide/show options visibility
		if(showOptions)
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:block;\">";
		}
		else
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:none;\"><div></div>";
		}
		
		// set checkbox for show ambiguous choice
		if(includeAmbig)
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\" checked=\"checked\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n ";
		}
		else
		{
			ambigLine = "<div>\n<input type=\"checkbox\" id=\"ambiguous\" value=\"ambiguous\"> Include ambiguous hits <img src=\"buttons/littleA.png\" alt=\"\"><br>\n ";
		}
		
		// set checkbox for show duplicates choice
		if(includeDuplicates)
		{
			dupLine = "<input type=\"checkbox\" id=\"duplicate\" value=\"duplicate\" checked=\"checked\"> Include duplicate hits <img src=\"buttons/littleD.png\" alt=\"\"><br>";
		}
		else
		{
			dupLine = "<input type=\"checkbox\" id=\"duplicate\" value=\"duplicate\"> Include duplicate hits <img src=\"buttons/littleD.png\" alt=\"\"><br>";
		}
				
		// set checkbox for standard errors
		if(includeErrors)
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"> Show standard errors<br>\n";
		}
		else
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\"> Show standard errors<br>\n";
		}
		
		return (controls1 + radioLine + fieldLine + controls4 + optsLine + ambigLine + dupLine + seLine + controls9);
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}