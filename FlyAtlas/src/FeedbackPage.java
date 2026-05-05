// Class to generate FeedBack HTML page
// DPL 08.07.2023

public class FeedbackPage extends Page
{
	private final int PAGE_POS = 9;				// Position of page in menu
	private boolean includeErrors = false; 		// show SEs in results
	
	public FeedbackPage(boolean includeErrors)
	{
		this.includeErrors = includeErrors;
	}	
	
	public String getFeedback()
	{
		PageUtility pu = new PageUtility();
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(pu.getMobileTopstrip(PAGE_POS));
		htmlBuilder.append(readHTML("htmlText/feedbackForm.txt"));
		
		// set hidden checkbox for SE choice so carried over between pages	
		htmlBuilder.append("<div style=\"display:none;\">");
		
		if(includeErrors)
		{
			htmlBuilder.append("<input type=\"checkbox\" id=\"errors\" value=\"errors\" checked=\"checked\"></div>");
		}
		else
		{
			htmlBuilder.append("<input type=\"checkbox\" id=\"errors\" value=\"errors\"></div>");
		}
		
		htmlBuilder.append(pu.getPageFoot());
		return htmlBuilder.toString();
	}
}
