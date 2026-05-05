 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Overkill as a class in this case as only single query
DPL 27.08.2012		Reverted to uppercase table names
*/

public class DBQuery
{			
    // query0 tissues corresponding to life stage (adult or larval)
    final static String name0= "STG_TISS";
    final static String query0 =
		"SELECT FlyID, Tissue "
		+ "FROM FlyAnat "
        + "WHERE Stage = ? "
        + "ORDER BY Tissue ";
       
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0)
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