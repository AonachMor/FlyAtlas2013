
/*
Generic results presentation page section, with variant for Profile Search
07.11.2015
11.11.2015 UTF-8 added to readHTML
DPL 18.09.2023
*/

import java.io.*;
import java.sql.*;
import java.awt.Color;
import java.text.*;

public abstract class Page 
{
	public void appendInfo(StringBuilder geneBuilder, ExperimentSet exptSet, int setID, int displayMax, boolean collapsed)
	{
		// This allows same method to generate info for either pages which start up collapsed, and those which start up expanded
		String buttonType1 = new String();
		String buttonType2 = new String();

		if(collapsed)
		{
			buttonType1 = "class=\"leftExpressionButton\"";
			buttonType2 = "class=\"hiddenLeftExpressionButton\"";
		}
		else
		{
			buttonType1 = "class=\"hiddenLeftExpressionButton\"";
			buttonType2 = "class=\"leftExpressionButton\"";
		}

		// gene name and symbol
		geneBuilder.append("<div class=\"gene\"><div class=\"geneDetails\">");

		// individual hide/show buttons for page open collapsed
		geneBuilder.append("<div><img " + buttonType1 + " id=\"tB" + setID + "\" src=\"buttons/expressionShowButton.png\" alt=\"show table\" onclick=\"javascript:toggleTable(" + setID + ", " + displayMax+ ");\"></div>\n");
		geneBuilder.append("<div><img " + buttonType2 + " id=\"tBHide" + setID + "\" src=\"buttons/expressionHideButton.png\" alt=\"hide table\" onclick=\"javascript:toggleTable(" + setID + ", " + displayMax + ");\"></div>\n");

		geneBuilder.append("<p class=\"clearer mobileOnly\"></p>\n");
		
		if(exptSet.getName()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Name:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + checkSuper(exptSet.getName()) + "</span></div>\n");
		}
		if(exptSet.getSymbol()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Symbol:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getSymbol() + "</span></div>\n");
		}

		if(exptSet.getCGNum()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Annotation Symbol:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getCGNum() + "</span></div>\n");
		}
		if(exptSet.getFBgn()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">FlyBase ID:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;"  + exptSet.getFBgn() + "</span></div>\n");
		}
		
		geneBuilder.append("</div>\n");

		// other info 
		geneBuilder.append("<div class=\"geneDetails mobileHide\" style=\"margin-left:42px;\">");
		
		if(exptSet.getProbesetID()!=null)
		{
			geneBuilder.append("<div class=\"left\"><span class=\"geneTag\">Probe Set ID:</span><br><span class=\"dent\">&nbsp;" + exptSet.getProbesetID() + "</span></div>\n");
		}
		if(exptSet.getGOListSize() > 0)		// GO numbers with title-based rollovers
		{
			geneBuilder.append("<div class=\"left\"><span class=\"geneTag\">Gene Ontology ID(s):</span><br><span class=\"dent\">&nbsp;</span>");		
			for (int j=0; j<exptSet.getGOListSize(); j++)
			{
				geneBuilder.append("<a href=\"http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=GO:" + exptSet.getGO(j).getID() + 
												"\" class=\"tip\" title=\"" + exptSet.getGO(j).getDescription() + "\">");
				geneBuilder.append(exptSet.getGO(j).getID() + "&nbsp;");
				geneBuilder.append("</a>");
			}	
			geneBuilder.append("</div>\n");
		}
		
		if(exptSet.isDegenerate())
		{
			// Get degenerate mates
			String qyPid = exptSet.getProbesetID();		// query probe set ID
			
			String[] foundSet = new String[20];		// > max ambig
			int foundSize = 0;				// No of results returned
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();		
			ParamQuery parQ= DBQuery.getParamQuery("AMBIG_FBGNS_IN_PROBESET");
			try 
			{
				parQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}
			
			try 
			{
				PreparedStatement prepStat = parQ.getPrepStatement();
				prepStat.setString(1, qyPid);
				ResultSet resSet = prepStat.executeQuery();
				// add results to array
				while (resSet.next())
				{
					String foundPid = resSet.getString(1);
					foundSet[foundSize] = foundPid;
					foundSize++;	
				}				
			}
			catch (SQLException e) 
			{System.out.println(e.toString());}
			
			geneBuilder.append("<img class=\"leftImage\" src=\"buttons/A5.png\" alt=\"Ambiguous Data Set\" title=\"This probe set is ambiguous &mdash; not specific for this gene alone.\">");
			
			geneBuilder.append("<div class=\"left\" style=\"padding-top:18px;\"><span style=\"font-weight:normal;font-size:13px;color:red;\">Also detects gene(s):</span>");
			
			for(int j=0; j<foundSize; j++)
			{
				if(!foundSet[j].equals(exptSet.getFBgn()))
				{
					geneBuilder.append(" " + foundSet[j]);
				}
			}
			geneBuilder.append("</div>\n");
		}
		
		if(exptSet.isDuplicate())
		{
			if(exptSet.isUniqueDuplicate())
			{
				geneBuilder.append("<img class=\"leftImage\" src=\"buttons/D5u.png\" alt=\"Duplicate Data Set\" title=\"The gene detected by this probe set is also detected by other probe sets, but the latter are all ambiguous.\">");
			}
			else
			{
				geneBuilder.append("<img class=\"leftImage\" src=\"buttons/D5.png\" alt=\"Duplicate Data Set\" title=\"The gene detected by this probe set is also detected by other probe sets.\">");
			}
		}
		
		// link out
		geneBuilder.append("<div><a href=\"javascript:loadLinks('" + exptSet.getFBgn() + "','" + exptSet.getCGNum() + "');\" title=\"Load external links to this gene in new window\"><img src=\"buttons/extLinks.jpg\" alt=\"link out\" class=\"linkImg\"></a></div>");
		
		geneBuilder.append("</div>\n");
	}

	// Version for Profile search
	public void appendInfoP(StringBuilder geneBuilder, ExperimentSet exptSet, int actualCount, int displayMax, int runningCount, FlyTissueList ftList)
	{
		geneBuilder.append("<div class=\"gene\">\n<div class=\"geneDetails\">\n");

		// expression buttons
/*		geneBuilder.append("<div>\n<img class=\"hiddenLeftExpressionButton\" id=\"tB" + runningCount + 
				"\" src=\"buttons/expressionShowButton.png\" alt=\"show table\" onclick=\"javascript:toggleTable(" + runningCount + ", "+displayMax+");\">\n</div>\n");
		geneBuilder.append("<div>\n<img class=\"leftExpressionButton\" id=\"tBHide" + runningCount + 
				"\" src=\"buttons/expressionHideButton.png\" alt=\"hide table\" align=\"right\" onclick=\"javascript:toggleTable(" + runningCount + ", "+displayMax+");\">\n</div>\n");
		geneBuilder.append("<p class=\"clearer mobileOnly\"></p>\n");
		*/

		geneBuilder.append("<div>\n<img class=\"leftExpressionButton\" id=\"tB" + runningCount + 
				"\" src=\"buttons/expressionShowButton.png\" alt=\"show table\" align=\"right\" onclick=\"javascript:toggleTable(" + runningCount + ", "+displayMax+");\">\n</div>\n");
		geneBuilder.append("<div>\n<img class=\"hiddenLeftExpressionButton\" id=\"tBHide" + runningCount + 
				"\" src=\"buttons/expressionHideButton.png\" alt=\"hide table\" onclick=\"javascript:toggleTable(" + runningCount + ", "+displayMax+");\">\n</div>\n");
		geneBuilder.append("<p class=\"clearer mobileOnly\"></p>\n");
		
		if(exptSet.getName()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Name:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getName() + "</span>\n</div>\n");
		}
		
		if(exptSet.getSymbol()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Symbol:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getSymbol() + "</span>\n</div>\n");
		}		

		if(exptSet.getCGNum()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Annotation Symbol:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getCGNum() + "</span>\n</div>\n");
		}
		if(exptSet.getFBgn()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">FlyBase ID:</span><span class=\"mobileHide\"><br></span>"
					+ "<span class=\"dent\">&nbsp;" + exptSet.getFBgn() + "</span>\n</div>\n");		
		}
		
		geneBuilder.append("</div>\n");
		
		// extra info
		geneBuilder.append("<div class=\"geneDetails mobileHide\" style=\"margin-left:42px;\">\n");
		if(exptSet.getProbesetID()!=null)
		{
			geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Probe Set ID:</span><br><span class=\"dent\">&nbsp;" + exptSet.getProbesetID() + "</span>\n</div>\n");
		}
		
		String cor = new String();
		if(exptSet.getPstat() < 0.0001)
		{
			cor = String.format("<em>r</em>=%.3f, <em>P<sub>B</sub></em>&lt;0.0001", exptSet.getRstat());
		}
		else
		{
			cor = String.format("<em>r</em>=%.3f, <em>P<sub>B</sub></em>=%.4f", exptSet.getRstat(), exptSet.getPstat());
		}
		geneBuilder.append("<div class=\"left\">\n<span class=\"geneTag\">Correlation:</span><br><span class=\"dent\">&nbsp;" + cor + "</span>\n</div>\n");

		if(exptSet.isDegenerate())
		{
			geneBuilder.append("<img class=\"leftImage\" src=\"buttons/A5.png\" alt=\"Ambiguous Data Set\" title=\"The probe set detecting this data is ambiguous- not specific to this gene alone.\">");
		}

		if(exptSet.isDuplicate())
		{
			if(exptSet.isUniqueDuplicate())
			{
				geneBuilder.append("<img class=\"leftImage\" src=\"buttons/D5u.png\" alt=\"Duplicate Data Set\" title=\"The gene detected by this probe set is also detected by other probe sets, but the latter are all ambiguous.\">");
			}
			else
			{
				geneBuilder.append("<img class=\"leftImage\" src=\"buttons/D5.png\" alt=\"Duplicate Data Set\" title=\"The gene detected by this probe set is also detected by other probe sets.\">");
			}
		}		
		// link out
		geneBuilder.append("<div>\n<a href=\"javascript:loadLinks('" + exptSet.getFBgn() + "','" + exptSet.getCGNum() + "');\" title=\"Load external links to this gene in new window\"><img src=\"buttons/extLinks.jpg\" alt=\"link out\" class=\"linkImg\"></a></div>");
		// finish div
		geneBuilder.append("</div>\n");
	}
	
	// gene and cat version - minimal
	public void appendTable(StringBuilder geneBuilder, int setID, ExperimentSet exptSet, boolean collapsed, 
																boolean includeErrors, FlyTissueList  ftList)
	{
		boolean hasBarchart = false;
		Probeset queryProbeSet = null;
		ExperimentSet querySet = null;
		String gene = "";
		int tissueID = -1;
		boolean byEnrichment = true;
		appendTable(geneBuilder, setID, exptSet, collapsed, hasBarchart, queryProbeSet, gene, querySet, includeErrors, ftList, tissueID, byEnrichment);
	}
	
	// tissue and top version - has tissueID and byEnrichment so can highlight these in tables
	public void appendTable(StringBuilder geneBuilder, int setID, ExperimentSet exptSet, boolean collapsed, 
			boolean includeErrors, FlyTissueList  ftList, int tissueID, boolean byEnrichment)
    {
        boolean hasBarchart = false;
        Probeset queryProbeSet = null;
        ExperimentSet querySet = null;
        String gene = "";
        appendTable(geneBuilder, setID, exptSet, collapsed, hasBarchart, queryProbeSet, gene, querySet, includeErrors, ftList, tissueID, byEnrichment);
    }
	
	// profile version - has queryProbeSet, querySet and gene (query gene?) and hasBarchart
	public void appendTable(StringBuilder geneBuilder, int setID, ExperimentSet exptSet, boolean collapsed, 
			boolean hasBarchart, Probeset queryProbeSet, String gene, 
			ExperimentSet querySet, boolean includeErrors, FlyTissueList  ftList)
	{
		int tissueID = -1;
		boolean byEnrichment = true;
		appendTable(geneBuilder, setID, exptSet, collapsed, hasBarchart, queryProbeSet, gene, querySet, includeErrors, ftList, tissueID, byEnrichment);
	}

	// Full constructor not used directly: setID (0, 1, 2...) is trimmedSetNum (gene) or currentSetNum  as only used for generating ids for javascript
	public void appendTable(StringBuilder geneBuilder, int setID, ExperimentSet exptSet, boolean collapsed, 
																boolean hasBarchart, Probeset queryProbeSet, String gene, 
																ExperimentSet querySet, boolean includeErrors, FlyTissueList  ftList,
																int tissueID, boolean byEnrichment)
	{
		int numReps = 4;								// total number of Affymetrix chip replicates
		boolean enrichTextWhite;						// whether numbers should be white (because of dark background) or not
		boolean abundTextWhite;							// do.
		String errors = new String();

		// Layout table
		if(collapsed)
		{
			// extra = "style=\"display:none;\" ";			
			geneBuilder.append("<div class=\"resultsWrapper\" " + "style=\"display:none;\" " + "id=\"t" + setID + "\"><div class=\"geneTable\"");
		}
		else
		{
			geneBuilder.append("<div class=\"resultsWrapper\" id=\"t" + setID + "\"><div class=\"geneTable\"");
		}

		if(hasBarchart)
		{
			//geneBuilder.append(" style=\"display:none;\"");
			geneBuilder.append(" style=\"block;\"");
		}

		// write table header, emphasizing selected stage/order combination if tissue or top search
		String stage = ftList.getStageByID(tissueID);		// adult/larval stage
		
		geneBuilder.append("><table class=\"results\"><tr><th style=\"width:27%\">Tissue</th>");
		if(!byEnrichment && stage.equals("Adult"))
		{
			geneBuilder.append("<th style=\"color:#005c37;\">Adult Abundance</th>");
		}
		else
		{
			geneBuilder.append("<th>Adult Abundance</th>");			
		}
		if(byEnrichment && stage.equals("Adult"))
		{
			geneBuilder.append("<th style=\"color:#005c37;\">Adult Enrichment</th>");
		}
		else
		{
			geneBuilder.append("<th>Adult Enrichment</th>");			
		}
		if(byEnrichment && stage.equals("Larval"))
		{
			geneBuilder.append("<th style=\"color:#005c37;\">Larval Enrichment</th>");
		}
		else
		{
			geneBuilder.append("<th>Larval Enrichment</th>");			
		}
		if(!byEnrichment && stage.equals("Larval"))
		{
			geneBuilder.append("<th style=\"color:#005c37;\">Larval Abundance</th>");
		}
		else
		{
			geneBuilder.append("<th>Larval Abundance</th>");		
		}
		geneBuilder.append("</tr>");

		// now write each table row in order specified in ftList object's FlyStagePair list
		for (int i=0; i < ftList.getPairSize(); i++)
		{			
			// write table cell with common tissue name
			String nextUniTissue = ftList.getFlyStagePair(i).getUniTissue();	// next uniTissue in list
			String selUniTissue = ftList.getUniTissueByID(tissueID);		// uniTissue selected for Tissue or Top search (or 'none')
			if(nextUniTissue.equals(selUniTissue))
			{
				geneBuilder.append("<tr><td class=\"uniTissue\">" + nextUniTissue + "</td>");
			}
			else
			{
				geneBuilder.append("<tr><td>" + nextUniTissue + "</td>");				
			}

			// write ADULT if it occurs or empty cell
			if(ftList.getFlyStagePair(i).getAdultTissue() == null)
			{
				geneBuilder.append("<td class=\"patternBack\">&nbsp;</td><td class=\"patternBack\">&nbsp;</td>");
			}
			else
			{
				Experiment adultExpt = exptSet.findByID(ftList.getFlyStagePair(i).getAdultTissue().getID());		// SIMPLIFY
				
				// deal with colour
				Color adultEnrichCol = getEnrichmentColor(adultExpt.getEnrichment());
				enrichTextWhite = isDark(getBrightness(adultEnrichCol));	
				String adultEnrichHTMLcolour = getHTMLcolour(adultEnrichCol);
				
				Color adultAbundCol = getAbundanceColor(adultExpt.getAbundance());
				abundTextWhite = isDark(getBrightness(adultAbundCol));
				String adultAbundHTMLcolour = getHTMLcolour(adultAbundCol);
				
				if(includeErrors)
				{
					errors = "<span style=\"font-size:75%;\"> &plusmn; " + manicure(adultExpt.getAbundanceSE(), false, true) + "</span>";
				}
				else
				{
					errors = "";
				}

				// write ADULT ABUNDANCE row
				if(adultExpt.getSignalCount() == 0)
				{
					geneBuilder.append("<td class=\"abundND\"><a href=\"#\" class=\"tip\" title=\"not detected in any of the 4 arrays\">ND</a></td>");
				}
				else
				{
					geneBuilder.append("<td style=\"background-color:" + adultAbundHTMLcolour + ";");

					if(abundTextWhite)
					{
						geneBuilder.append("color: white;");
					}

					if(adultExpt.getSignalCount() == numReps)
					{
						geneBuilder.append("\">" + Math.round(adultExpt.getAbundance()) + errors + "</td>");
					}
					else	// [parentheses]
					{
						geneBuilder.append("\"><span class=\"parenth\"><a href=\"#\" class=\"tip\" ");
						if(abundTextWhite)
						{
							geneBuilder.append("style=\"color:white;\" ");	// needed to overight a.tip style which has text black
						}						
						geneBuilder.append("title=\"detected in " + adultExpt.getSignalCount() + " out of " + numReps + " array(s)\">[" + Math.round(adultExpt.getAbundance()) + errors + "]</a></span></td>");
					}
				}

				// write ADULT ENRICHMENT row
				if(adultExpt.getSignalCount() == 0)
				{
					geneBuilder.append("<td class=\"enrichND\"><a href=\"#\" class=\"tip\" title=\"not detected in any of the 4 arrays\">ND</a></td>");
				}
				else 
				{
					geneBuilder.append("<td style=\"background-color:" + adultEnrichHTMLcolour + ";");
					
					if(enrichTextWhite)
					{
						geneBuilder.append("color: white;");
					}
					
					if(adultExpt.getSignalCount() == numReps)
					{
						geneBuilder.append("\">" + manicure(adultExpt.getEnrichment(), false, false) + "</td>");
					}
					else	// [parentheses]
					{
						geneBuilder.append("\"><span class=\"parenth\"><a href=\"#\" class=\"tip\" ");
						if(enrichTextWhite)
						{
							geneBuilder.append("style=\"color:white;\" ");	// needed to overight a.tip style which has text black
						}					
						geneBuilder.append("title=\"detected in " + adultExpt.getSignalCount() + " out of " + numReps + " array(s)\">[" + manicure(adultExpt.getEnrichment(), true, false) + "]</a></span></td>");
					}
				}

			}

			// write LARVAL if it occurs or empty cell
			if(ftList.getFlyStagePair(i).getLarvalTissue() == null)
			{					
				geneBuilder.append("<td class=\"patternBack\">&nbsp;</td><td class=\"patternBack\">&nbsp;</td></tr>\n");
			}
			else
			{
				// find appropriate larval ExperimentSet by using id of larval member of pair to search through exptSetList
				Experiment larvalExpt = exptSet.findByID(ftList.getFlyStagePair(i).getLarvalTissue().getID());		// SIMPLIFY				
				
				// deal with colour
				Color larvEnrichCol = getEnrichmentColor(larvalExpt.getEnrichment());
				enrichTextWhite = isDark(getBrightness(larvEnrichCol));		
				String larvEnrichHTMLColour = getHTMLcolour(larvEnrichCol);
				
				Color larvAbundCol = getAbundanceColor(larvalExpt.getAbundance());
				abundTextWhite = isDark(getBrightness(larvAbundCol));
				String larvAbundHTMLColour = getHTMLcolour(larvAbundCol);

				if(includeErrors)
				{
					errors = "<span style=\"font-size:75%;\"> &plusmn; " + manicure(larvalExpt.getAbundanceSE(), false, true) + "</span>";
				}
				else
				{
					errors = "";
				}

				// write LARVAL ENRICHMENT row
				if(larvalExpt.getSignalCount() == 0)
				{
					geneBuilder.append("<td class=\"enrichND\"><a href=\"#\" class=\"tip\" title=\"not detected in any of the 4 arrays\">ND</a></td>");
				}
				else 
				{
					geneBuilder.append("<td style=\"background-color:" + larvEnrichHTMLColour + ";");
					
					if(enrichTextWhite)
					{
						geneBuilder.append("color: white;");
					}

					if(larvalExpt.getSignalCount() == numReps)
					{
						geneBuilder.append("\">" + manicure(larvalExpt.getEnrichment(), false, false) + "</td>");
					}
					else	// [parentheses]
					{
						geneBuilder.append("\"><span class=\"parenth\"><a href=\"#\" class=\"tip\" ");
						if(enrichTextWhite)
						{
							geneBuilder.append("style=\"color:white;\" ");	// needed to overwrite a.tip style which has text black
						}					
						geneBuilder.append("title=\"detected in " + larvalExpt.getSignalCount() + " out of " + numReps + " array(s)\">[" + manicure(larvalExpt.getEnrichment(), true, false) + "]</a></span></td>");
					}					
				}

				// write LARVAL ABUNDANCE row
				if(larvalExpt.getSignalCount() == 0)
				{
					geneBuilder.append("<td class=\"abundND\"><a href=\"#\" class=\"tip\" title=\"not detected in any of the 4 arrays\">ND</a></td></tr>");
				}
				else
				{
					geneBuilder.append("<td style=\"background-color:" + larvAbundHTMLColour + ";");

					if(abundTextWhite)
					{
						geneBuilder.append("color: white;");
					}
					
					if(larvalExpt.getSignalCount() == numReps)
					{
						geneBuilder.append("\">" + Math.round(larvalExpt.getAbundance()) + errors + "</td></tr>");
					}
					else	// [parentheses]
					{
						geneBuilder.append("\"><span class=\"parenth\"><a href=\"#\" class=\"tip\" ");
						if(abundTextWhite)
						{
							geneBuilder.append("style=\"color:white;\" ");	// needed to overwrite a.tip style which has text black
						}						
						geneBuilder.append("title=\"detected in " + larvalExpt.getSignalCount() + " out of " + numReps + " array(s)\">[" + Math.round(larvalExpt.getAbundance()) + errors + "]</a></span></td></tr>");
					}
				}
			}
		}
		// close table and the geneTable div
		geneBuilder.append("</table></div>");

		if(hasBarchart)
		{
			buildBars(geneBuilder, setID, exptSet, queryProbeSet, gene, querySet, ftList);
		}
		// close the 'resultsWrapper' container div
		geneBuilder.append("</div>");
	}

	// writes the javascript/jQuery to create each bar chart
	public void buildBars(StringBuilder geneBuilder, int runningCount, ExperimentSet exptSet,
								Probeset queryProbeSet, String gene, ExperimentSet querySet, FlyTissueList ftl)
	{
		String divID = "barChart" + runningCount;
		geneBuilder.append("<div class=\"barComparison\" id=\"" + divID + "\" style=\"display:none;\"></div>");
		geneBuilder.append("<script type=\"text/javascript\">");
		geneBuilder.append("arrayOfData = new Array(");
		boolean comma = false;

		exptSet.sortForBarchart(ftl);
		querySet.sortForBarchart(ftl);
		
		for(int i = 0; i < exptSet.getExptListSize(); i++)
		{
			if(exptSet.getExpt(i)!=null)
			{
				if(comma)
				{
					geneBuilder.append(",");
				}
				
				Experiment expt = exptSet.getExpt(i);
				
				double queryLevel;
				double testLevel;
				
				if(querySet.getExpt(i).getSignalCount() > 0)
				{
					queryLevel = queryProbeSet.getAbundance(expt.getFlyID()).getAbundance();
				}
				else
				{
					queryLevel = 0;
				}
				
				if(expt.getSignalCount() > 0)
				{
					testLevel = expt.getAbundance();
				}
				else
				{
					testLevel = 0;
				}
				
				if(queryLevel == 0 && testLevel == 0)
				{
					geneBuilder.append("[[" + queryLevel + "," + testLevel + "], 'ND']");
				}
				else
				{
					geneBuilder.append("[[" + queryLevel + "," + testLevel + "], '']");
				}

				comma = true;
			}
		}
		geneBuilder.append(");");
		
		//add a title variable?
		//add in the query gene symbol
		geneBuilder.append("$('#" +divID+ "').jqBarGraph({ data: arrayOfData,colors: ['#6CA68F','#008852']," +
				"legends: ['" + gene + "','" + exptSet.getSymbol() + "'],legend: true,animate: false," +
						"width: 818,height: 200,type: 'multi',showValues: false }); ");

		geneBuilder.append("</script>");
	}
	
	// Returns background colour for abundance cells on a white to black log scale
	public Color getAbundanceColor(double abundance)
	{
		int red = 0;
		int green = 0;
		int blue = 0;	
		double base = 2;
		int numSteps = 15;
		int range = 255;
		
		double logVal = Math.log(abundance) / Math.log(base);
		red = range - (int) (logVal*range) / numSteps;
		if(red>255)
		{
			red = 255;
		}
		green = red; blue = red;
		
		return new Color(red, green, blue);
	}
	
	// Returns background colour for enrichment cells on a yellow/white/red divergent scale
	public Color getEnrichmentColor(double enrichment)
	{
		int red = 0;
		int green = 0;
		int blue = 0;
		Color colour = new Color(red, green, blue);
		double base = 1.55;		// For log 
		int numHighSteps = 7;	// Number of log steps for e > 1
		int numLowSteps = 4;	// Number of log steps for e < 1
		int gbRange = 210;		// For reds e > 1
		
		if(enrichment > Math.pow(base, numHighSteps))	// deal with extreme high values first - base 1.55 with 7 steps = ca.21.5
		{
			green = 230 - gbRange;
			blue = 230 - gbRange;
			int rRange = 50;
			int addSteps = 15;
			double logVal = Math.log(enrichment) / Math.log(base);	
			red = 250 - (int) (logVal*rRange ) / addSteps;
			colour = new Color(red, green, blue);
		}
		else if(enrichment > 1)
		{
    		double logVal = Math.log(enrichment) / Math.log(base);
    		int rRange = 5;
    		int gRange = 230;
    		int bRange = 15;
    		double rDecrement = (logVal*rRange ) / numHighSteps ;
    		double gDecrement = (logVal*gRange ) / numHighSteps ;
    		double bDecrement = (logVal*bRange ) / numHighSteps ;
    		red = 255 - (int) rDecrement;
    		green = 255 - (int) gDecrement;
    	    blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}
		else if(enrichment == 1)
		{
			red = 255; green = 255; blue = 40;		// yellow
			colour = new Color(red, green, blue);
		}
		else if(enrichment < 1)			// yellow range below 1
		{		
			double lowBase = 1.8;
			if(enrichment < 0)
			{
				enrichment = 0;
			}
			
			int bRange = 215;			// B range from 255 to 40
    		double logVal = Math.log(enrichment) / Math.log(lowBase);
    		double bDecrement = (logVal* bRange)  / numLowSteps; 	
    		
    		if(bDecrement < -bRange)
    		{
    			bDecrement = -bRange;		// 40 minimum value for yellow
    		}
    		red = 255;
    		green = 255;
    		blue = 40 - (int) bDecrement;
    		colour = new Color(red, green, blue);
		}				    
		return colour;
	}

	// returns number of ExptSets after removing duplicates - NB does not change ExptSet, just for layout info
	public int getNoDuplicateCount(ExperimentSet[] exptSetList, int numExptSets)
	{
		int count = 0;
		for(int i = 0; i < numExptSets; i++)
		{
			if(!exptSetList[i].isDuplicate()||exptSetList[i].isBestDuplicate())
			{
				count++;
			}
		}
		return count;
	}

	// Reads a file into a utf-8 String - typically a file in the same directory, e.g. "htmlText/mypage"
	// This allows one to write and edit external blocks of html without recompiling etc.
	public String readHTML(String path)
	{
		String outString;
		InputStream stream = getClass().getResourceAsStream(path);
		if (stream !=null)
		{
			try
			{
				byte [] b = new byte[8092];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int i = 0;
				while( (i=stream.read(b)) > 0)
				{
					out.write(b, 0, i);
				}
				stream.close();
				outString = out.toString("UTF-8");	// !
			}
			catch (IOException x)
			{
				outString = "Text Misread. Please notify the site owner.";
			}
		}
		else
		{
			outString = "Text Misread. Please notify the site owner.";		
		}
		return outString;
	}
	
	// utility method gets brightness of a colour
	private int getBrightness(Color c) 
	{
	    return (int) Math.sqrt(c.getRed() * c.getRed() * 0.241 +
	      						c.getGreen() * c.getGreen() * 0.691 +
	      						c.getBlue() * c.getBlue() * 0.068);
	}
	
	// utility method takes a brightness value and determines whether above a darkness threshold
	private boolean isDark(int brightness)
	{
	    if (brightness < 130)
	    {
	    	return true;
	    }
	    else
	    {
	    	return false;
		}
	}
	
	// utility method to generate html colour string of the type rgb(215,65,98) from java Color
	private String getHTMLcolour(Color colour)
	{
		return "rgb(" + colour.getRed() + "," + colour.getGreen() + "," + colour.getBlue() + ")";
	}
	
	// utility method to return a String value with visually appropriate number of decimal places (not strict num sig figs)
	// paren = value in parentheses, sError = SE value (for abundance)
	private String manicure(double value, boolean paren, boolean sError)
	{
		NumberFormat N = NumberFormat.getInstance();
		N.setGroupingUsed(false);		// no comma separators for thousands (mainly single digit thous which shouldn't have them)
		if(value < 1.0)
		{
			if(paren || sError)
			{
				N.setMaximumFractionDigits(1); 
				N.setMinimumFractionDigits(1);
			}
			else
			{
				N.setMaximumFractionDigits(2); 
				N.setMinimumFractionDigits(2);			
			}
		}
		else if(value < 10.0)
		{
			if(sError)
			{
				N.setMaximumFractionDigits(0); 
				N.setMinimumFractionDigits(0);
			}
			else
			{
				N.setMaximumFractionDigits(1); 
				N.setMinimumFractionDigits(1);				
			}
		}
		else
		{
			N.setMaximumFractionDigits(0); 
			N.setMinimumFractionDigits(0);
		}	
		return N.format(value);
	}
	
	// Checks for [+] indication of superscript and marks up for HTML
	private String checkSuper(String name)
	{
		if(name.indexOf("[+]") != -1)
		{
			int start = name.indexOf("[+]");
			name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			// check for second case as in Na[+]/H[+]
			if(name.indexOf("[+]") != -1)
			{
				start = name.indexOf("[+]");
				name = name.substring(0, start) + "<sup>+</sup>" + name.substring(start+3);
			}
		}
		else if(name.indexOf("[2+]") != -1)
		{
			int start = name.indexOf("[2+]");
			name = name.substring(0, start) + "<sup>2+</sup>" + name.substring(start+4);			
		}
		return name;
	}
	
}
