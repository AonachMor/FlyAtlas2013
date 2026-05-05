 /*
Class to hold html code and write specific html menus 
for different pages of FlyAtlas
Also has method to import text from files
Includes possibly redundant javascript references for js pages, but advantage is allows global updates
08.07.2023
*/

public class PageUtility
{
	private PageDescriptor pageList[];			// list of page descriptor objects
	private final int LENGTH = 9;				// Number of pages
	
	private String top = 
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
		+"<html>\n<head>\n"
		+ "<!-- Google tag (gtag.js) -->"
		+ "<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-8HZJBJ43VQ\"></script>"
		+ "<script>"
		+ "window.dataLayer = window.dataLayer || [];"
		+ "function gtag(){dataLayer.push(arguments);}"
		+ "gtag('js', new Date());"		
		+ "gtag('config', 'G-8HZJBJ43VQ');"
		+ "</script>"
		+ "<!-- end of Google tag -->\n"
		+ "<title>FlyAtlas 2013: The Microarray-based atlas of Drosophila gene expression</title>\n"
		+ "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
		+ "<meta name=\"description\" content=\"Advanced web interface to the FlyAtlas database quantifying gene expression in individual tissues of adult and larval Drosophila melanogaster\">\n"
		+ "<meta name=\"keywords\" content=\"Drosophila melanogaster, fruit fly, gene, tissue, tissue-specific genes, gene expression, microarray\">\n"
		+ "<meta name=\"google-site-verification\" content=\"uUx69Tav2vlkNDQffd7NZi0NMnP5lPYQwSAfDuzCVc4\">\n"
		+ "<script type=\"text/javascript\" src=\"scripts/flyAtlas.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/jquery-1.6.2.min.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/jqBarGraph.1.1.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/aComplete.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/menuPop.js\"></script>\n"
		+ "<script src=\"scripts/drag.js\" type=\"text/javascript\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/aComplete.css\">\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/flyAtlas.css\">\n"
		+ "<link rel=\"icon\" type=\"image/x-ico\" href=\"images/fly.ico\">\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/flyatlas-touch-icon-57x57.png\">\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/flyatlas-touch-icon-72x72.png\">\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/flyatlas-touch-icon-114x114.png\">\n"
		+ "<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Archivo+Narrow\">\n"
		+ "<style>@import url('https://fonts.googleapis.com/css2?family=Roboto+Slab:wght@800&display=swap');</style>\n"
		+ "</head>\n";
	
	private String middle =
		"<div id=\"wrapper\">\n"
		+ "<div id=\"top\"><h1>FlyAtlas 2013</h1></div>\n"
		+ "<div id=\"innerWrapper\">\n";	
		
	private String bottom = "<div id=\"bottomWrapper\">\n"
			+ "<div id=\"centre\">\n";
	
	public PageUtility()
	{
		pageList = new PageDescriptor[LENGTH];
		initializePageList();
	}
	
	// builds top section of html page with appropriate names and links
	public String getPageTop(int pagePos)
	{
		StringBuilder pBuilder = new StringBuilder(top);		// boiler plate
		pBuilder.append(pageList[pagePos-1].getBodyLine());		// <body> line
		pBuilder.append(middle);								// boiler plate
		// links
		pBuilder.append("<div id=\"menu\">\n");
		for(int i=0; i<LENGTH; i++)
		{
			if(pageList[i].getPagePos() == pagePos)		// Page calling the html block
			{
				pBuilder.append("<span class=\"selected\">" + pageList[i].getPageName() + "</span>\n");		// no self-link
			}
			else
			{
				pBuilder.append("<a class=\"menuButton\" href=\"javascript:" + pageList[i].getToMethodName() + ";\">" + pageList[i].getPageName() + "</a>\n");
			}
		}
		pBuilder.append("</div>\n");
		pBuilder.append(bottom);	// boiler plate
		return pBuilder.toString();
	}
	
	// build a top bar for mobile only — needs to be different for home page
	public String getMobileTopstrip (int pagePos)
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"topStrip\"><!-- topStrip only seen by mobiles -->\n");
		mBuilder.append("<div id=\"topL\"><a href=\"javascript:toHomeForm();\">&nbsp;&#8801;</a></div>\n");
		mBuilder.append("<div id=\"topC\">FlyAtlas 2013: " + pageList[pagePos-1].getPageName() + "</div>\n");
		mBuilder.append("<div id=\"topR\"></div>\n");
		mBuilder.append("</div><!-- end of topStrip -->\n");	
		return mBuilder.toString();
	}
	
	// version for home page
	public String getMobileTopstrip()
	{
		StringBuilder mBuilder = new StringBuilder();
		mBuilder.append("<div id=\"topStrip\"><!-- topStrip only seen by mobiles -->\n");
		mBuilder.append("<h1>FlyAtlas 2013</h1>\n");
		mBuilder.append("</div><!-- end of topStrip -->\n");	
		return mBuilder.toString();
	}
	
	// returns foot of html page: ends, in order of centre, bottomWrapper, innerWrapper and wrapper divs, and body and html
	public String getPageFoot()
	{
		StringBuilder foot = new StringBuilder("</div>\n</div>\n</div>\n</div>\n");
		foot.append("</body></html>");
		return foot.toString();
	}
	
	// initializes array of PageDescriptors
	private void initializePageList()
	{ 
		pageList[0] = new PageDescriptor(1, "Home", "toHomeForm()", "<body>\n");
		pageList[1] = new PageDescriptor(2, "Gene", "toGeneForm()", "<body onload=\"createLink();\" onkeypress=\"geneKey(event)\">\n");
		pageList[2] = new PageDescriptor(3, "Category", "toGOForm()", "<body onload=\"createLink();\" onkeypress=\"goKey(event)\">\n");
		pageList[3] = new PageDescriptor(4, "Profile", "toProfileForm()", "<body onload=\"createLink();\" onkeypress=\"profileKey(event)\">\n");
		pageList[4] = new PageDescriptor(5, "Tissue", "toTissueForm()", "<body onload=\"createLink();\" onkeypress=\"tissueKey(event)\">\n");
		pageList[5] = new PageDescriptor(6, "Top", "toTopForm()", "<body onload=\"createLink();\" onkeypress=\"topKey(event)\">\n");
		pageList[6] = new PageDescriptor(7, "Development", "toDevelopmentForm()", "<body onload=\"createLink();\" onkeypress=\"devKey(event)\">\n");		
		pageList[7] = new PageDescriptor(8, "Docs", "toDocsForm()", "<body>\n");		
		pageList[8] = new PageDescriptor(9, "Feedback", "toFeedbackForm()", "<body>\n");
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 1 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
		}
		public int getPagePos()
		{
			return pagePos;
		}
		public String getPageName()
		{
			return pageName;
		}
		public String getToMethodName()
		{
			return toMethodName;
		}
		public String getBodyLine()
		{
			return bodyLine;
		}
	}
	
}
