 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
DPL 14.09.2012 corrected 15.10.2021
*/

public class DBQuery
{			
    // query0 search by FBgn
    final static String name0 = "FBGN";
    final static String query0 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Gene.FBgn = ? "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
 
     // query1 search by CGnum
    final static String name1 = "CGNUM";
    final static String query1 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Gene.CGNum = ? "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
    
    // query2 search by gene symbol
    final static String name2 = "SYMBOL";
    final static String query2 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND (Gene.Symbol = BINARY ? "
		+ "OR Gene.RomanSymbol = BINARY ? ) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
        
    // query3 search by gene name
    final static String name3 = "NAME";
    final static String query3 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND (Gene.Name = ? "
		+ "OR Gene.RomanName = ? ) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
    
    // query4 get tissue and stage from flyID
    final static String name4 = "STAGE_TISSUE";
    final static String query4 =
    	"SELECT DISTINCT Stage, Tissue "
    	+ "FROM FlyAnat "
    	+ "WHERE FlyID = ? ";
    
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4)
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