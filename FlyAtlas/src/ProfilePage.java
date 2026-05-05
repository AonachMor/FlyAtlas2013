/*
Generates an HTML page for a Profile search, with or without results
Use Tehao to test effect of running a search on a gene that does not apparently have genes with similar profiles
18.09.2023
*/

public class ProfilePage extends Page
{
	private String geneQuery;				// gene search term
	// private String pString; 				// String version of p statistic cutoff
	private String rString;					// String version of r statistic cutoff
	private Probeset queryProbeSet;
	private int displayMax;					// max No of results to display
	private int totalDisplayed;				// actual No results displayed (can be less than max)
	private int trimmedSetNum;				// number of sets in results after removing (or not) ambiguous or duplicate members
	private ExperimentSet querySet;
	private boolean includeErrors; 			// show SEs in results
	private boolean byPearson;				// profile comparison uses Pearson rather than Spearman correlation
	private boolean showOptions = false;	// whether hide/show options div should be visible
	//private boolean collapsed = false;		// whether tables/bar charts are hidden or not at start (this needs to be true)
	private boolean collapsed = true;
	private boolean hasBarchart = true;		// identifier for appendTable method
	private String radioSelect;				// value of radio button for type of gene search (symbol, name etc)
	private FlyTissueList ftList;			// need to draw tables in correct manner
	private final int PAGE_POS = 4;			// Position of page in menu
	private StringBuilder htmlBuilder;		// For building html
	
	private int maxList[] = {25, 35, 50, 100};						// cutoff values for displayMax option
	private String rList[] = {"0.50","0.55","0.60","0.65","0.70","0.75","0.80","0.85","0.90"};	// cutoff values for r statistic
	final String DEFAULT_R = "0.70";								// default value of rString

	// initial page
	public ProfilePage(boolean byPearson, boolean includeErrors)
	{
		this.includeErrors = includeErrors;
		this.byPearson = byPearson;
		
		radioSelect = "geneSymbol";	// default
		displayMax =  maxList[0];	// default
		String geneQuery = "";		// blank initially
		rString = DEFAULT_R;		// initial value
		
		// Build initial page
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(geneQuery, rString));
		htmlBuilder.append(pu.getPageFoot());
	}

	// invalid/ambiguous gene descriptor results page
	public ProfilePage(boolean byPearson, boolean includeErrors, int displayMax, String geneQuery, 
							String rString, String radioSelect, boolean showOptions, boolean ambigRemoved)
	{
		this.byPearson = byPearson;
		this.includeErrors = includeErrors;
		this.displayMax = displayMax;
		this.rString = rString;
		this.radioSelect = radioSelect;		// radio button checked for type of gene selection
		this.showOptions = showOptions;
		
		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(geneQuery, rString));
	
		htmlBuilder.append("<div class=\"box\"><div class=\"textBox\">No results for &lsquo;" + geneQuery + "&rsquo;. ");
		if(ambigRemoved)
		{
			htmlBuilder.append("This gene is only represented by ambiguous probe sets and cannot be used for a Profile search.");
		}
		else
		{
			htmlBuilder.append("Please select gene identifiers from the autosuggest menu (remember searches are case-sensitive).");
		}
		htmlBuilder.append("</div></div>");
		
		htmlBuilder.append(pu.getPageFoot());
	}

	// valid and unambiguous gene results page
	public ProfilePage(ExperimentSet[] exptSetList, int numExptSets, boolean byPearson, String  geneQuery, int displayMax, boolean includeErrors, 
						String rString, Probeset queryProbeSet, ExperimentSet querySet, String radioSelect, boolean showOptions, FlyTissueList ftList)
	{
		this.geneQuery = geneQuery;				// search term
		this.includeErrors = includeErrors;
		this.displayMax = displayMax;
		this.byPearson = byPearson;
		this.rString= rString;
		this.queryProbeSet = queryProbeSet;
		this.querySet = querySet;
		this.radioSelect = radioSelect;
		this.showOptions = showOptions;
		this.ftList = ftList;
		
		String longQuery;							// geneQuery + FBgn (if different) or + CG
		String fbgnQuery = querySet.getFBgn();		// FBgn corresponding to query
		final String CG_NUM = "No CG number";
		String cgQuery; 
		if(querySet.getCGNum() != null)
		{
			cgQuery = querySet.getCGNum();	// CGNum corresponding to query
		}
		else
		{
			cgQuery = CG_NUM;
		}
		
		if(fbgnQuery.equals(geneQuery))
		{
			if(cgQuery.equals(CG_NUM))
			{
				longQuery = "&lsquo;" + geneQuery + "&rsquo;";
			}
			else
			{
				longQuery = "&lsquo;" + geneQuery + "&rsquo; (" + cgQuery + ")";
			}
		}
		else if(cgQuery.equals(geneQuery))
		{
			longQuery = "&lsquo;" + geneQuery + "&rsquo; (" + fbgnQuery + ")";
		}
		else
		{
			if(cgQuery.equals(CG_NUM))
			{
				longQuery = "&lsquo;" + geneQuery + "&rsquo; (" + fbgnQuery + ")";
			}
			else
			{
				longQuery = "&lsquo;" + geneQuery + "&rsquo; (" + fbgnQuery + ", " + cgQuery + ")";
			}			
		}

		// calculate No. of results to show after removing duplicates and taking into account displayMax
		trimmedSetNum = getNoDuplicateCount(exptSetList, numExptSets);
		if(displayMax < trimmedSetNum)
		{
			totalDisplayed = displayMax;
		}
		else
		{
			totalDisplayed = trimmedSetNum;
		}

		// gets report and removes duplicates if 'no-duplicates' set, as is the case with Profile Search
		String report = new String(getReport(exptSetList));

		PageUtility pu = new PageUtility();
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(getControls(geneQuery, rString));

		htmlBuilder.append("<div class=\"box\">");
		
		String qualifier = " at <em>r</em>&ge;" + rString + ", <em>P<sub>B</sub></em>&le;0.05";

		if(trimmedSetNum > 0)
		{
			// table-histogram buttons
			htmlBuilder.append("<div><img class=\"rightButton mobileHide\" id=\"showBarChartsButton\" onclick=\"javascript:tableGraphToggle();\" src=\"buttons/barProfileButton.png\" alt=\"show data as bar charts\"></div>");
			htmlBuilder.append("<div><img class=\"hiddenRightButton mobileHide\" id=\"showTablesButton\" onclick=\"javascript:tableGraphToggle();\" src=\"buttons/tableProfileButton.png\" alt=\"show data as tables\"></div>");

			if(trimmedSetNum > 1)
			{
				// hide/show all buttons - note these are set to hide as profile opens with show
/*				htmlBuilder.append("<div><img class=\"rightButton\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");
				htmlBuilder.append("<div><img class=\"hiddenRightButton mobileHide\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");			
*/
				//hide and show all buttons
				htmlBuilder.append("<div><img class=\"rightButton\" id=\"showAllButton\" onclick=\"javascript:showAllTissueTables();\" src=\"buttons/allShowButton.png\" alt=\"show all tables\"></div>");
				htmlBuilder.append("<div><img class=\"hiddenRightButton mobileHide\" id=\"hideAllButton\" onclick=\"javascript:hideAllTissueTables();\" src=\"buttons/allHideButton.png\" alt=\"hide all tables\"></div>");
				
				if(totalDisplayed > 1)
				{

					if(trimmedSetNum < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum +" profiles resembling " + longQuery + qualifier + ".\n");		
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " profiles resembling " + longQuery + qualifier + "; " + totalDisplayed + " shown.\n");		
					}
				}
				else
				{
					if(trimmedSetNum < displayMax)
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " profiles resembling " + longQuery + qualifier + ".\n");			
					}
					else
					{
						htmlBuilder.append("<div class=\"textBox2\">" + trimmedSetNum + " profiles resembling " + longQuery + qualifier + "; " + totalDisplayed + " shown.\n");			
					}
				}
			}
			else
			{
				htmlBuilder.append("<div class=\"textBox2\">"+trimmedSetNum+" profile resembling " + longQuery + qualifier + ".</div></div>\n");
			}
		}
		if(trimmedSetNum == 0) 
		{
			htmlBuilder.append("<div class=\"textBox\">No gene profiles correlated with " + longQuery + qualifier + ".</div></div>");
		}
		else
		{
			htmlBuilder.append(report);
		}
		htmlBuilder.append(pu.getPageFoot());
	}
 
	// NB unlike gene method doesn't use setsize as uses pre-computed totalDisplayed;; also no includeDuplicates boolean here
	public String getReport(ExperimentSet[] exptSetList)
	{
		StringBuilder geneBuilder = new StringBuilder();	// Builder for accumulating output
		int currentSetNum = 0;		// variable distinct from trimmedSetNum needed here (contrast gene) as cut-off reduces number sent

		//for each set
		for(int i = 0; currentSetNum < totalDisplayed; i++)
		{
			if( !exptSetList[i].isDuplicate() || exptSetList[i].isBestDuplicate())
			{
				ExperimentSet exptSet = exptSetList[i];		//rename arraySet
				appendInfoP(geneBuilder, exptSet, trimmedSetNum, displayMax, currentSetNum, ftList);
				appendTable(geneBuilder, currentSetNum, exptSet, collapsed, hasBarchart, 
										queryProbeSet, geneQuery, querySet, includeErrors, ftList);
				//ends gene div
				geneBuilder.append("</div>");
				currentSetNum++;
			}
		}
		return geneBuilder.toString();
	}
	
	// Builds controls, setting fields as appropriate
	private String getControls(String key, String rString)
	{
		String controls1 = "<div id=\"explanation\">For a gene of interest, find others with a similar profile of expression across tissues.</div>\n"
									+ "<div id=\"controls\">";
		String radioLine = new String();			
		String fieldLine = "<p class=\"standard\"><span class=\"lableftFirst\">Gene:</span><input type=\"text\" autocomplete=\"off\" id=\"inputField\" value=\"" + key + 
							"\" style=\"height:15px;\" onkeyup=\"findNames('gene');\">";
		String maxLine = new String();			// sets max cutoff for repeat
		String controls5 = "<button onclick=\"sendSearchProfileForm();\">Search</button></p>\n";					
		String optsLine = new String();		// resets visibility of options 
		String seLine = new String();		// resets standard errors selection	
		String corrLine = new String();		// resets correlation (Pearson or Spearman) choice		
		String rLine = new String();		// resets r stats choice
		String controls10 = "<input type=\"checkbox\" style=\"display:none;\" id=\"selectall\" value=\"selectall\">\n" +
				"<input type=\"checkbox\" style=\"display:none;\" id=\"duplicate\" value=\"duplicate\">\n" +
				"<input type=\"checkbox\" style=\"display:none;\" id=\"ambiguous\" value=\"ambiguous\">\n</div>\n</div>\n" +
				"<div style=\"position:absolute;\" id=\"popup\">\n" +
				"<table id=\"menuTable\" class=\"revertFont\" cellspacing=\"0\" cellpadding=\"0\">\n" +         
				"<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody></table></div></div>\n";	
		
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

		// generate max options setting selected as required
		StringBuilder maxBuilder = new StringBuilder("<span class=\"lableft\">Display:</span><select " +
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
		
		// sets visibility of hide/show options
		if(showOptions)
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:block;\"><div>";
		}
		else
		{
			optsLine = "<div id=\"visible\"></div><div id=\"hideme\" style=\"display:none;\"><div>";
		}		
			
		// generate rcut options setting selected as required
		StringBuilder rBuilder = new StringBuilder("r &gt; <select name=\"rcut\" id=\"rcut\">\n");
		for(int i=0; i<rList.length; i++)
		{
			String cutoff = rList[i];
			if(cutoff.equals(rString))
			{
					rBuilder.append("<option selected=\"selected\" value=\"" + cutoff + "\">" + cutoff + "</option>\n");					
			}
			else
			{
					rBuilder.append("<option value=\"" + cutoff + "\">" + cutoff + "</option>\n");
			}
		}
		rBuilder.append("</select><br>\n");	// finish off select
		rLine = rBuilder.toString();

		// set correlation choice (pearson or spearman)
		StringBuilder corrBuilder = new StringBuilder();
		if(byPearson)
		{
			corrBuilder.append("<input type=\"radio\" checked=\"checked\" id=\"pearson\" name=\"correlation\" value=\"pearson\"> Pearson ");
			corrBuilder.append("<input type=\"radio\" id=\"spearman\" name=\"correlation\" value=\"spearman\"> Spearman correlation<br>\n");
		}
		else
		{
			corrBuilder.append("<input type=\"radio\" id=\"pearson\" name=\"correlation\" value=\"pearson\"> Pearson ");
			corrBuilder.append("<input type=\"radio\" checked=\"checked\" id=\"spearman\" name=\"correlation\" value=\"spearman\"> Spearman correlation<br>\n");
		}
		corrLine = corrBuilder.toString();
		
		// set checkbox for standard errors
		if(includeErrors)
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"> Show standard errors<br>\n";
		}
		else
		{
			seLine = "<input type=\"checkbox\" id=\"errors\" value=\"errors\"> Show standard errors\n";
		}
		
		/*StringBuilder controlBuilder = new StringBuilder(controls1 + radioLine + fieldLine + maxLine + controls5 + optsLine +  
															rLine + corrLine + seLine + controls10);*/
		StringBuilder controlBuilder = new StringBuilder(controls1 + radioLine + fieldLine + maxLine + controls5 + optsLine +  
				 corrLine + rLine + seLine + controls10);
		return controlBuilder.toString();
	}

	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}