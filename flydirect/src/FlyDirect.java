/*
FlyDirect
DPL 10.10.2012
 */	

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FlyDirect extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException 
	{	
		// capture parameters
		String idType = req.getParameter("idtype");		// Parameter to specify whether FlyBaseID, CGnum, gene name, gene symbol
		String gene = req.getParameter("gene");			// Parameter to specify value of id
		String output = req.getParameter("output");		// Parameter specifying txt or xml output	
		
		// Handle null idType 
		if(idType == null)
		{
			idType = "";
		}
		// To prevent cross-site scripting, accept only letters or numbers for idType
		idType = idType.replaceAll("[^a-zA-Z0-9]", "");
		String query = new String();
		
		// check idType here so can respond to bad types rather than throw SQL exception later on
		if (idType.equals("fbgn"))
		{
			query = "FBGN";
		}		
		else if(idType.equals("cgnum"))
		{
			query = "CGNUM";		
		}
		else if(idType.equals("symbol"))
		{
			query = "SYMBOL";		
		}
		else if(idType.equals("name"))
		{
			query = "NAME";		
		}
		else
		{
			query = "NONE";
		}
		
		// To prevent cross-site scripting, accept only letters for output
		// Handle null output
		if(output == null)
		{
			output = "";
		}
		// To prevent cross-site scripting, accept only letters for output
		output = output.replaceAll("[^a-z]", "");	
		if(output.equals("xml"))
		{
			output = "xml";
		}
		else
		{
			output = "txt";		// default if nothing specified	
		}

		// Set Content type
		if(output.equals("xml"))
		{
			res.setContentType("text/xml;charset=UTF-8");
		}
		else
		{
			 res.setContentType("text/plain;charset=UTF-8");	
		}
		res.setHeader("Cache-Control", "no-cache");
		
		// Do stuff and respond. NB Don't get PrintWriter until ContentType has been set
		PrintWriter writer = res.getWriter();
		
		// handle null gene
		if(gene == null)
		{
			gene = "";
		}
		gene = new String(gene.getBytes("8859_1"), "UTF-8");	// from FlyAtlas code	
		
		if(!query.equals("NONE"))
		{
    		GeneSearch search = new GeneSearch(gene, query);	
    		int foundNum = search.getListSize();					
    		if(foundNum > 0)
    		{
        		ExperimentSet[] exptSetList = search.getExptSetList();
        		if(output.equals("txt"))
        		{
        			printTextOutput(exptSetList, foundNum, writer);
        		}
        		else
        		{
        			printXMLOutput(exptSetList, foundNum, writer);
        		}
    		}   
    		else
    		{
    			writer.println("No of matching results found: " + foundNum + "\n");
    		}
		}
		else
		{
			writer.println("Invalid idtype.");
		}
		writer.close();
	}

	// processes the experimentSet array as plain text
	public void printTextOutput(ExperimentSet[] exptSetList, int listSize, PrintWriter writer)
	{
		writer.println("No of matching results found: " + listSize + "\n");
		for(int i=0;i<listSize; i++)
		{			
			writer.println(exptSetList[i].getTextFormatted());
		}
	}

	// processes the experimentSet array as XML
	public void printXMLOutput(ExperimentSet[] exptSetList, int listSize, PrintWriter writer)
	{
		// Header
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<!DOCTYPE flyatlas SYSTEM \"http://motif.mvls.gla.ac.uk/flyatlas.dtd\">");
		writer.println("<flyatlas>");
		writer.println(exptSetList[0].getGeneXMLtag());		// gene start with ID attributes
		for(int i=0;i<listSize; i++)
		{
			writer.println("<probeset probesetID=\"" + exptSetList[i].getProbesetID() + "\">");
				writer.println("<fly stage=\"adult\">");
					writer.println(exptSetList[i].getTissueXML("Adult"));
				writer.println("</fly>");
				writer.println("<fly stage=\"larval\">");
					writer.println(exptSetList[i].getTissueXML("Larval"));		
				writer.println("</fly>");		
			writer.println("</probeset>");
		}
		writer.println("</gene>");
		writer.println("</flyatlas>");
	}
	
}
