/*
Class to handle JDBC connections to db on localhost
Based on examples in JDBC chapters in various O'Reilly books
Version for new drivers
DPL 27.08.2012 Database extension (12) removed and user changed
 */

import java.sql.*;

public class Connect
{
	Connection conn = null;
	String username = "charles";
	String password = "atlas";
	String host = "jdbc:mysql://localhost/FlyAtlasDB";

	ResultSet resSet;

	public Connect()
	{
		connect(host);
	}	

	private void connect(String url)
	{
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");	
			conn = DriverManager.getConnection(url, username, password); 
		}
		catch(ClassNotFoundException ex)
		{
			System.out.println(ex.toString());
		}
		catch(SQLException e)
		{
			System.out.println("Trying to connect to MySQL " + e);
		}		
	}

	// Allows user to construct prepared query
	public Connection getConnection()
	{
		return conn;	
	}

	// Takes PreparedStatement to run query
	public ResultSet runPreparedQuery(PreparedStatement stmt)
	{
		try 
		{
			resSet = stmt.executeQuery();					
		}
		catch(SQLException e)
		{
			System.out.println("Trying to make query" + e.toString());
		}
		return resSet;		
	}

}