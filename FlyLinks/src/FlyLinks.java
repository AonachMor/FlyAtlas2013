/*
	FlyLinks
	Utility Servlet to take Drosophila identifiers and return page of links to fly resources
	DPL 29.11.2012
	Modified 03.07.2016 to allow choice of stylesheets and back link to FlyAtlas 1
	Update 22.06.2021 FlyMet link added
	Update 08.08.2021 Amigo link corrected
	Update 22.06.2021 DINeR link restored
	Update 19.10.2021 flyatlas2.org replaced by motif server address and FlyAtlas 2013 and 2 hard-coded
	Update 04.11.2022 BeetleAtlas link added
	Update 24.01.2025 VDRC link updated
	Update 27.01.2025 Flymine link removed
*/	
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FlyLinks extends HttpServlet
{		
	final String NO_CG = "No CG identifier";
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException 
	{	
		// get parameters	
		String fbgn = req.getParameter("fbgn");		// FGgn number
		String cg = new String();					// CG number
		if(req.getParameter("cg") != null && !req.getParameter("cg").equals("null") && !req.getParameter("cg").equals(""))
		{
			cg = req.getParameter("cg");			
		}
		else
		{
			cg = NO_CG;
		}
		
		// To prevent cross-site scripting, accept only letters or numbers 
		fbgn = fbgn.replaceAll("[^a-zA-Z0-9]", "");
		if(!cg.equals(NO_CG))
		{
			cg = cg.replaceAll("[^a-zA-Z0-9]", "");
		}
		
		int versionNo = 1;		// FlyAtlas version number — 1 or 2, 1 default so don't have to alter FlyAtlas 2013
		String versionString = req.getParameter("versionNo");
		if(versionString == null || versionString.equals("1"))
		{
			versionNo = 1;
		}
		else if (versionString.equals("2"))
		{
			versionNo = 2;			
		}
		
		int dinerID = 0;		// neuropeptide id for DINeR site (default 0 = no id)
		String dinerString = req.getParameter("dinerID");
		try
		{
			dinerID = Integer.parseInt(dinerString);
		}
		catch (NumberFormatException ex)
		{
			dinerID = 0;
		}
		
		// Make http response
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		PrintWriter out = res.getWriter();
		
		// head
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html>\n<head>\n");
		out.println("<title>External Links</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		out.println("<script type=\"text/javascript\" src=\"scripts/external.js\"></script>");
		if(versionNo == 1)
		{
			out.println("<style type = \"text/css\">@import url(\"scripts/external.css\");</style>");
		}
		else if(versionNo == 2)
		{
			out.println("<style type = \"text/css\">@import url(\"scripts/external2.css\");</style>");		
		}
		out.println("<link rel=\"icon\" href=\"fly.ico\" type=\"image/x-icon\">");
		out.println("<link rel=\"shortcut icon\" href=\"fly.ico\" type=\"image/x-icon\">");	
		out.println("</head>\n");
		//start of body
		out.println("<body>\n<div id=\"heading\"><h1>External Links</h1></div>");
		out.println("<div id=\"main\">");
		out.println("<h2>Gene: " + fbgn + " / " + cg + "</h2>\n");
		out.println("<p>The buttons below allow you to query several external resources with this <em>Drosophila</em> gene. " +
				"The links have been generated automatically, so a negative response is possible and only indicates that " +
				"the particular resource does not have information for the gene in question.</p>");

		// FlyAtlas 2013
		if(versionNo == 2)
		{
    		out.println("<div class=\"linkOut\">\n");
    		out.println("<button onclick=\"openLinkWindow('https://motif.mvls.gla.ac.uk/flyatlas/index.html?maxdisplayed=30&amp;search=gene&amp;gene=" + fbgn + "&amp;radioGene=FBgene');\">FlyAtlas 1</button>");
    		out.println("<span class=\"resource\">FlyAtlas — Microarray version:</span> Compare RNAseq to microarray (where data exist)\n");
    		out.println("</div>\n");
		}
		else if(versionNo == 1)
		{
    		out.println("<div class=\"linkOut\">\n");
    		out.println("<button onclick=\"openLinkWindow('https://motif.mvls.gla.ac.uk/FlyAtlas2/index.html?search=gene&gene=" + fbgn + "&idtype=fbgn');\">FlyAtlas 2</button>");
    		out.println("<span class=\"resource\">FlyAtlas 2 — RNA Seq version:</span> Compare Microarray to RNAseq (where data exist)\n");
    		out.println("</div>\n");			
		}
		
		// FlyMet link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('http://www.flymet.org/met_explore/gene_tissue_explorer/" + fbgn + "');\">FlyMet</button>");
		out.println("<span class=\"resource\">FlyMet:</span> Metabolites mapped to <em>Drosophila</em> genes (where data exist)\n");
		out.println("</div>\n");
		
		// DINeR link
		if(dinerID > 0)
		{
    		out.println("<div class=\"linkOut\">\n");
    		out.println("<button onclick=\"openLinkWindow('https://www.neurostresspep.eu/diner/infosearchw?species=32&neuropeptide=" + dinerID + "');\">DINeR</button>");
    		out.println("<span class=\"resource\">DINeR:</span> Neuropeptide information from the nEUROSTRESSPEP project\n");
    		out.println("</div>\n");
		}
		
		// FlyBase link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://flybase.org/reports/" + fbgn + ".html');\">FlyBase</button>");
		out.println("<span class=\"resource\">FlyBase:</span> The Database for <em>Drosophila</em> genetics and molecular biology\n");
		out.println("</div>\n");
		
		// FlyMine link	
/*		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://www.flymine.org/query/portal.do?origin=flybase&amp;class=gene&amp;externalid=" + fbgn + "');\">FlyMine</button>");
		out.println("<span class=\"resource\">FlyMine:</span> An integrated database for <em>Drosophila</em> and <em>Anopheles</em> genomics\n");
		out.println("</div>\n");*/
		
		// Beetle Atlas link	
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://www.beetleatlas.org/?search=gene&gene=" + fbgn + "&idtype=flyFBgn');\">BeetleAtlas</button>");
		out.println("<span class=\"resource\">BeetleAtlas:</span> Tissue expression of related gene(s) in <em>Tribolium</em> (where data exist)\n");
		out.println("</div>\n");
		
		// BDGP link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://insitu.fruitfly.org/cgi-bin/ex/search.pl?ftype=2&amp;ftext=" + fbgn + "');\">BDGP</button>");
		out.println("<span class=\"resource\">BDGP:</span> Berkley <em>Drosophila</em> gene project (Patterns of gene expression in embryogenesis)\n");
		out.println("</div>\n");
		
		// BioGRID link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://thebiogrid.org/search.php?organism=7227&amp;search=" + fbgn + "');\">BioGRID</button>");
		out.println("<span class=\"resource\">BioGRID:</span> Biological General Repository for Interaction Databases\n");
		out.println("</div>\n");
		
		// VDRC link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://shop.vbc.ac.at/vdrc_store/catalogsearch/result/?q=" + fbgn + "');\">VDRC</button>");
		out.println("<span class=\"resource\">VDRC:</span> Vienna <em>Drosophila</em> and RNAi Centre\n");
		out.println("</div>\n");
		
		// DRSC link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('https://www.flyrnai.org/cgi-bin/DRSC_gene_lookup.pl?gname=" + fbgn + "');\">DRSC</button>");
		out.println("<span class=\"resource\">DRSC:</span> <em>Drosophila</em> RNAi Screening Centre,  at Harvard Medical School\n");
		out.println("</div>\n");
		
		// AmiGO link
		out.println("<div class=\"linkOut\">\n");
		out.println("<button onclick=\"openLinkWindow('http://amigo.geneontology.org/amigo/search/bioentity?q=" + cg + "');\">AmiGO</button>");
		out.println("<span class=\"resource\">AmiGO:</span> Gene Ontology Information\n");
		out.println("</div>\n");
		
		// end of body
		out.println("</div>\n</body>\n</html>");	
	}
	
}

