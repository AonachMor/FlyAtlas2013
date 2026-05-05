// Class to generate HomePage HTML page
// DPL 09.07.2023

public class HomePage extends Page
{
	private final int PAGE_POS = 1;				// Position of page in menu
	private boolean includeErrors = false; 		// show SEs in results
	
	public HomePage(boolean includeErrors)
	{
		this.includeErrors = includeErrors;
	}
	
	public String getHome()
	{
		PageUtility pu = new PageUtility();
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip());
		htmlBuilder.append(getHomeMenu());
		htmlBuilder.append(readHTML("htmlText/home.txt"));
		
		if(includeErrors)
		{
			htmlBuilder.append("<div style=\"display:none;\"><input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"></div>");
		}
		else
		{
			htmlBuilder.append("<div style=\"display:none;\"><input type=\"checkbox\" id=\"errors\" value=\"errors\"></div>");
		}
		
		htmlBuilder.append(pu.getPageFoot());
		return htmlBuilder.toString();
	}
	
	public String getHomeMenu()
	{
		StringBuilder menuBuilder = new StringBuilder();
		menuBuilder.append("<p class=\"formIntro noindent mobileOnly\">FlyAtlas 2013 allows one to explore the expression of genes in <em>Drosophila</em> tissues. "
				+ "It derives from work in the Dow laboratory in the University of Glasgow using hybridization of mRNA to microarrays. See the Docs for full details of the search types listed below.</p>");
		menuBuilder.append("<div id=\"menuI\">\n<ul>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toGeneForm();\">Gene</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toGOForm();\">Category</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toProfileForm();\">Profile</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toTissueForm();\">Tissue</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toTopForm();\">Top</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toDevelopmentForm();\">Development</a></li>\n");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toDocsForm();\">Docs</a></li>");
		menuBuilder.append("<li><a class=\"indexLink\" href=\"javascript:toFeedbackForm();\">Feedback</a></li>\n");
		menuBuilder.append("</ul></div>");	
		return menuBuilder.toString();
	}
}
