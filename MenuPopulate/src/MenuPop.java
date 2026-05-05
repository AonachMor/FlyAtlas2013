/*
Servlet that takes a stage as parameter, makes a query to a MySQL database
and returns tissues as text/xml
DPL 29.06.2012
 */	
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class MenuPop extends HttpServlet
{	        	
	StringBuffer tissueBuffer;	// Buffer for accumulating output

	public void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException 
	{		
		String stage =  (String) req.getParameter("stg");
		String output = "";

		if(stage != null)
		{
			// make query
			output = getTissueList(stage);
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			// write out the response string
			res.getWriter().write(output);
		}
		else 
		{
			// Write error message - useful only for development
			res.setContentType("text/xml");
			res.setHeader("Cache-Control", "no-cache");
			res.getWriter().write("Error: " + stage);
		}
	}

	// makes connection and requests tissues for a given stage	
	public String getTissueList(String stg)
	{
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		ParamQuery parQ = DBQuery.getParamQuery("STG_TISS");

		try 
		{
			parQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}

		tissueBuffer = new StringBuffer("");

		try 
		{
			PreparedStatement prepStat = parQ.getPrepStatement();
			prepStat.setString(1, stg);
			ResultSet resSet = prepStat.executeQuery();
			formatOutput(resSet);
		}
		catch (SQLException e) 
		{}

		return tissueBuffer.toString();
	}

	// lays out results in xml structure
	public void formatOutput(ResultSet resSet) throws SQLException 
	{
		if(resSet.first())			// only write if tissues - should be redundant
		{
			resSet.beforeFirst();	// hack to reset cursor as 'if' moves it on a row!
			
			// xml pragma etc
			tissueBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			tissueBuffer.append("<tissues>");

			while (resSet.next())	// moves to next row while rows remain
			{
				String foundID = resSet.getString(1);
				String foundTiss = resSet.getString(2);
				
				tissueBuffer.append("<tiss>");
				tissueBuffer.append("<tissid>" + foundID + "</tissid>");
				tissueBuffer.append("<tissdescrip>" + foundTiss + "</tissdescrip>");
				tissueBuffer.append("</tiss>");
			}
			
			tissueBuffer.append("</tissues>");
		} 
		else 
		{
			tissueBuffer.append("");
		}
	}

}
