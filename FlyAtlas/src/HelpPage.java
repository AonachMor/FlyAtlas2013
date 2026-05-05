// Class to generate Help HTML page
// DPL 08.07.2023

public class HelpPage extends Page
{
	private final int PAGE_POS = 8;				// Position of page in menu
	private boolean includeErrors = false; 		// show SEs in results
	
	public HelpPage(boolean includeErrors)
	{
		this.includeErrors = includeErrors;
	}
	
	public String getHelp()
	{
		PageUtility pu = new PageUtility();
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(readHTML("htmlText/help.txt"));
		
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
}
