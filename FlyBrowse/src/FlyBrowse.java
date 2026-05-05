/*
	FlyBrowse
	Utility Servlet to take Drosophila chromosome locus and ID and prepare a link page to UCSC browser reads
	Modified to serve different data for MicroRNAs
	DPL 10.05.2017
	Updated 21.06.2025
*/	
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FlyBrowse extends HttpServlet
{		
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException 
	{	
		String tracksFilename = "FlyTracks.txt";
		// get parameters	
		String locus = req.getParameter("locus");		// Gene locus
		String id = req.getParameter("id");				// Fbgn etc
		String mir = req.getParameter("mir");			// 'true' or 'false' for MicroRNAs
		if(mir.equals("true"))
		{
			tracksFilename = "FlyMirTracks.txt";
		}

		// To prevent cross-site scripting, accept only letters or numbers, - and :
		locus = locus.replaceAll("[^-:a-zA-Z0-9]", "");
		
		// Make HTTP response
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		
		// head
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html>\n<head>\n");
		out.println("<title>View Reads in UCSC Browser</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		out.println("<script type=\"text/javascript\" src=\"scripts/flybrowse.js\"></script>");
		out.println("<style type = \"text/css\">@import url(\"scripts/flybrowse.css\");</style>");
		out.println("<link rel=\"icon\" href=\"images/drosoph.ico\" type=\"image/x-icon\">");
		out.println("<link rel=\"shortcut icon\" href=\"images/drosoph.ico\" type=\"image/x-icon\">");	
		out.println("</head>\n");
		
		//start of body and first line with button
		out.println("<body>\n<div id=\"heading\">\n<h1>RNAseq Reads for " + id + "</h1>\n</div>\n");
		out.println("<div id=\"main\">\n");
		out.println("<p class=\"warning\">\nIt will take 5 to 10 sec. for the UCSC Browser to load after pressing: ");
/*		out.println("<button onclick=\"openLinkWindow('http://genome.ucsc.edu/cgi-bin/hgTracks?db=dm6&position="
				+ locus
				+ "&hgct_customText=http://motif.mvls.gla.ac.uk/fly/"
				+ tracksFilename
				+ "&xenoRefGene=hide&phyloP27way_sel=0&phastCons27way_sel=0&multiz27way_sel=0&ensGene=pack&refGene=hide');\">Go</button>");	*/
		out.println("<button onclick=\"openLinkWindow('http://genome.ucsc.edu/cgi-bin/hgTracks?db=dm6&position="
				+ locus
				+ "&hgct_customText=http://motif.mvls.gla.ac.uk/fly/"
				+ tracksFilename
				+ "&cons124way=hide&evaSnpContainer=hide&intronEst=hide&mrna=hide&phastCons27way_sel=0&phyloP27way_sel=0&refGene=hide&refSeqComposite=hide&rmsk=hide&xenoRefGene=hide');\">Go</button>");	
		out.println("</p>");
		
		// hints
		out.println("<p class=\"title\">TWO HINTS FOR VIEWING TRACKS</p>\n");
		out.println("<ul><li>All tracks are the same height: to get an impression of quantitative differences between tissues you need to examine the scale at the left (a).</li>");
		out.println("<li>Often one is interested in a few of the many tissues presented. You may bring these together by dragging their left bars up or down (b) or click on the bar and hide those that are not of immediate interest (c).");
		out.println("Alternatively use the customization panel at the bottom of the page, which also allows you to add other reference tracks.</li></ul>\n");

		// graphics
		out.println("<div class=\"graphics\">\n");
		out.println("<img src=\"images/ucsc1.png\" alt=\"\" width=\"193\" height=\"160\"><img src=\"images/ucsc2.png\" alt=\"\" width=\"193\" height=\"160\"><img src=\"images/ucsc3.png\" alt=\"\" width=\"193\" height=\"160\">\n");
		out.println("</div>\n");
		
		// end of body
		out.println("</div>\n</body>\n</html>");	
	}
	
}

