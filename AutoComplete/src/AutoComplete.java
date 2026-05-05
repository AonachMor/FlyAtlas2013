/*
Servlet that takes a text string, makes a query to a MySQL database
and returns terms starting with this prefix as text/xml
DPL 28.06.2012: Handle other search types by varying autocomplete start length
10.08.2012: Gene Ontology autocomplete at both sides and Roman versions of Greek letters
10.08.2012 - single letter symbol
27.08.2012 - fixed single letter so checks if it's a Greek letter
Updated 07.11.2021 for Tomcat 8.5 UTF-8 handling of parameters
 */	

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class AutoComplete extends HttpServlet
{
	// Number flags as variables to aid comprehension
	private final int GO_TERM = 0;
	private final int GENE_SYMBOL = 1;
	private final int GENE_NAME = 2;
	private final int GENE_CG = 3;
	private final int GENE_FB = 4;
	private final int GO_ID = 5;
	private final int GO_FREE = 6;

	private int mode = GENE_SYMBOL;		// flag for query

	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException 
	{	
		String prefix = "";	// This is the the user entry initialize to avoid poss npe
		int tooSmall = 10;	// prefix must be longer than this
		
		String geneQ = req.getParameter("gene");	// text typed into field for gene request
		String goQ = req.getParameter("go");		// text typed into field for go request
		
		String searchType = req.getParameter("searchType");	// radio search type
		
		if(geneQ != null && !geneQ.equals(""))
		{
			prefix = geneQ.trim();
			if(searchType.equals("symbol"))
			{
				mode = GENE_SYMBOL;
				tooSmall = 0;
			}
			else if(searchType.equals("name"))
			{
				mode = GENE_NAME;
				tooSmall = 1;
			}
			else if(searchType.equals("cgnum"))
			{
				mode = GENE_CG;
				tooSmall = 4;
			}
			else if(searchType.equals("fbgene"))
			{
				mode = GENE_FB;
				tooSmall = 8;
			}
		}
		else if(goQ != null && !goQ.equals(""))
		{
			prefix = goQ.trim();
			if(searchType.equals("goTerm"))		// npe here
			{
				mode = GO_TERM;
				tooSmall = 2;
			}
			else if(searchType.equals("goID"))
			{
				mode = GO_ID;
				tooSmall = 4;
			}
			else if(searchType.equals("goFree"))
			{
				mode = GO_FREE;
				tooSmall = 50;		// no autocomplete but needed to clear?
			}
		}
		
		if(prefix.length() > tooSmall)		// don't look at too short strings
		{	
		    res.setContentType("text/xml;charset=UTF-8");
			res.setHeader("Cache-Control", "no-cache");
			// Don't get PrintWriter until ContentType has been set
			PrintWriter writer = res.getWriter();
			writer.println("<response>");
			
			//String utf8Prefix = new String(prefix.getBytes("8859_1"), "UTF-8");	// Tomcat 8 — Uncomment for Tomcat 6
			//int foundNum = doQuery(utf8Prefix, writer);
			
			int foundNum = doQuery(prefix, writer);
			
			if(foundNum ==0)
			{
				res.setStatus(HttpServletResponse.SC_NO_CONTENT); 
				return;
			}   
			         
			writer.println("</response>");
			writer.close();
		}
		else 
		{
			res.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

	// makes connection and requests gene symbols starting with prefix
	public int doQuery(String prefix, PrintWriter writer)
	{
		int foundNum = 0;				// No of results returned
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		ParamQuery parQ = null;

		if (mode == GO_TERM)
		{
		 	parQ= DBQuery.getParamQuery("GO_TERM");
		}		
		else if(mode == GENE_SYMBOL)		// modify this so it looks through a list of Greek letters if length 1
		{
			if (prefix.length() == 1)
			{
				if(isGreek(prefix))
				{
					parQ= DBQuery.getParamQuery("SYMBOL");
				}
				else
				{
					parQ= DBQuery.getParamQuery("SYMBOL_SINGLE");
				}
			}
			else if  (prefix.length() > 1)
			{
				parQ= DBQuery.getParamQuery("SYMBOL");
			}	 	
		}
		else if(mode == GENE_NAME)
		{
		 	parQ= DBQuery.getParamQuery("NAME");		
		}
		else if(mode == GENE_CG)
		{
		 	parQ= DBQuery.getParamQuery("CG_NUM");		
		}
		else if(mode == GENE_FB)
		{
		 	parQ= DBQuery.getParamQuery("FB_GENE");		
		}
		else if(mode == GO_ID)
		{
		 	parQ= DBQuery.getParamQuery("GO_ID");
		}
		else if(mode == GO_FREE)
		{
		 	parQ= DBQuery.getParamQuery("GO_TERM");
		}

		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}


		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			if (mode == GO_TERM || mode == GO_FREE)	// find anywhere
			{
				prepStat.setString(1, '%' + prefix + '%');			
			}
			else if(mode == GENE_SYMBOL || mode == GENE_NAME)	// find at start of term or Roman term
			{
				if(parQ.getQueryName().equals("SYMBOL_SINGLE"))
				{
					prepStat.setString(1, prefix);
				}
				else
				{
					prepStat.setString(1, "" + prefix + '%');
					prepStat.setString(2, "" + prefix + '%');
				}
			}
			else	// find at start of term
			{
				prepStat.setString(1, "" + prefix + '%');
			}
			ResultSet resSet = prepStat.executeQuery();
			
			while (resSet.next())
			{
				String foundSymbol = resSet.getString(1);
				foundNum++;
				writer.println("<name>" + foundSymbol + "</name>");
			}				
		}
		catch (SQLException e) 
		{System.out.println(e.toString());}
		
		return foundNum;
	}
	
	public boolean isGreek(String letter)
	{
		if(letter.equals("\u03B1"))
			{return true;}
		else if(letter.equals("\u03B2"))
			{return true;}			
		else if(letter.equals("\u03B3"))
			{return true;}		
		else if(letter.equals("\u03B4"))
			{return true;}
		else if(letter.equals("\u03B5"))
			{return true;}
		else if(letter.equals("\u03B6"))
			{return true;}
		else if(letter.equals("\u03B7"))
			{return true;}
		else if(letter.equals("\u03B8"))
			{return true;}		
		else if(letter.equals("\u03B9"))
			{return true;}		
		else if(letter.equals("\u03BA"))
			{return true;}
		else if(letter.equals("\u03BB"))
			{return true;}
		else if(letter.equals("\u03BC"))
			{return true;}
		else if(letter.equals("\u03BD"))
			{return true;}
		else if(letter.equals("\u03BE"))
			{return true;}
		else if(letter.equals("\u03BF"))
			{return true;}
		else if(letter.equals("\u03C0"))
			{return true;}
		else if(letter.equals("\u03C1"))
			{return true;}
		else if(letter.equals("\u03C2"))
			{return true;}
		else if(letter.equals("\u03C3"))
			{return true;}
		else if(letter.equals("\u03C4"))
			{return true;}
		else if(letter.equals("\u03C5"))
			{return true;}
		else if(letter.equals("\u03C6"))
			{return true;}
		else if(letter.equals("\u03C7"))
			{return true;}
		else if(letter.equals("\u03C8"))
			{return true;}
		else if(letter.equals("\u03C9"))
			{return true;}
		else
			{return false;}
	}
}
