 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
DPL 29.08.2012
Reverted to Upper case table names and changed some of table and field names
*/

public class DBQuery
{			
    // query0 search Gene symbol by initial letters
    final static String name0 = "SYMBOL";
    final static String query0 =
		"SELECT Symbol "
		+ "FROM Gene "
        + "WHERE Symbol LIKE ? "
		+ "OR RomanSymbol LIKE ? "
        + "ORDER BY Symbol ";
 
     // query1 search Gene name by initial letters
    final static String name1 = "NAME";
    final static String query1 =
		"SELECT Name "
		+ "FROM Gene "
        + "WHERE Name LIKE ? "
		+ "OR RomanName LIKE ? "
        + "ORDER BY Name ";
    
    // query2 search Gene CG Number by initial letters
    final static String name2 = "CG_NUM";
    final static String query2 =
		"SELECT CGNum "
		+ "FROM Gene "
        + "WHERE CGNum LIKE ? "
        + "ORDER BY CGNum ";
        
    // query3 search Gene FB number by initial letters
    final static String name3 = "FB_GENE";
    final static String query3 =
		"SELECT FBgn "
		+ "FROM Gene "
        + "WHERE FBgn LIKE ? "
        + "ORDER BY FBgn ";
        
    // query4 search Gene ontology description by 'like'
    final static String name4 = "GO_TERM";
    final static String query4 =
		"SELECT GODescript "
		+ "FROM Ontology "
        + "WHERE GODescript LIKE ? "
        + "ORDER BY GODescript ";
    
    // query5 search Gene Ontology id by 'like'
    final static String name5 = "GO_ID";
    final static String query5 =
		"SELECT GONum "
		+ "FROM Ontology "
        + "WHERE GONum LIKE ? "
        + "ORDER BY GONum ";
    
    // query6 search Symbol exactly on one letter 
    final static String name6 = "SYMBOL_SINGLE";
    final static String query6 =
		"SELECT Symbol "
		+ "FROM Gene "
        + "WHERE Symbol = ? "
        + "ORDER BY Symbol ";
    
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6)
	};
    
	// finds ParamQuery object by queryName and returns
	public static ParamQuery getParamQuery(String name)
	{
		for (int i=0; i < pqList.length; i++)
		{
		 	if (pqList[i].getQueryName().equals(name))
		 	{
		 		return pqList[i];
		 	}
		}
		return null;
	}
}