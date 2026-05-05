 /*
DBQuery
Class with static method(s) to provide access to SQL query strings
Reverted to upper case table names for renamed FlyAtlasDB and changed names for publication
DPL 15.02.2016
DPL 16.10.2021	— removed invalid "Distinct" from Top queries
*/

// queries are lower case as the wretched database 12 tables are currently lowercase
public class DBQuery
{			
    // query0 retrieve all FBGn ids for a probeset
    final static String name0 = "AMBIG_FBGNS_IN_PROBESET";
    final static String query0 =
		"SELECT DISTINCT FBgn "
		+ "FROM Probeset "
        + "WHERE ProbesetID = ? "
        + "ORDER BY FBgn ";
    
    		/* GENE Searches: NB these are ordered by FBgn and ProbesetID to allow packaging into ExptSets and must return same fields in sama order */
    
    // query1: Gene search by Symbol
    final static String name1 = "GENE_BY_SYMBOL";
    final static String query1 =
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
    
    // query2: Gene search by Name
    final static String name2 = "GENE_BY_NAME";
    final static String query2 =
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
    
    // query3: Gene search by CG Number
    final static String name3 = "GENE_BY_CGNUM";
    final static String query3 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Gene.CGNum = ? "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
    
    // query4: Gene search by FBgn number
    final static String name4 = "GENE_BY_FBGN";
    final static String query4 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Gene.FBgn = ? "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
  
		/* CATEGORY Searches: also ordered by FBgn and ProbesetID to allow packaging into ExptSets and must return same fields in sama order */
    
    // query5: Category search by GO Description - retrieves same fields as above + genont stuff
    final static String name5 = "CAT_BY_DESCRIP";
    final static String query5 =    
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy, "
				+ "Ontology.GONum, Ontology.GOType, Ontology.GODescript "
		+ "FROM Experiment, Probeset, Gene, OntolOfGene, Ontology "
		+ "WHERE Gene.FBgn = Probeset.FBgn " 
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID " 
		+ "AND Probeset.FBgn = OntolOfGene.FBgn "
		+ "AND OntolOfGene.GONum = Ontology.GONum " 
		+ "AND (Ontology.GODescript = ?) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Ontology.GONum, Experiment.FlyID ";
    
    // query6: Category search by GO number
    final static String name6 = "CAT_BY_GONUM";
    final static String query6 =    
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy, Ontology.GONum, " 
				+ "Ontology.GOType, Ontology.GODescript "
		+ "FROM Experiment, Probeset, Gene, OntolOfGene, Ontology "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Probeset.FBgn = OntolOfGene.FBgn "
		+ "AND OntolOfGene.GONum = Ontology.GONum "
		+ "AND (Ontology.GONum = ?) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Ontology.GONum, Experiment.FlyID ";
    
    // query7: Category search by Free text (fragment) of GO Descriptions
    final static String name7 = "CAT_BY_FRAG";
    final static String query7 =    
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, "
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy, " 
				+ "Ontology.GONum, Ontology.GOType, Ontology.GODescript "
		+ "FROM Experiment, Probeset, Gene, OntolOfGene, Ontology "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Probeset.FBgn = OntolOfGene.FBgn "
		+ "AND OntolOfGene.GONum = Ontology.GONum "
		+ "AND (Ontology.GODescript LIKE ?) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Ontology.GONum, Experiment.FlyID ";
 
		/* TISSUE Searches: also ordered by FBgn and ProbesetID to allow packaging into ExptSets and must return same fields in sama order*/
    
    // query8: Tissue search by Enrichment of category (GO Description) - free text
    final static String name8 = "TISSUE_ENRICHMENT";
    final static String query8 =  
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, " 
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy, " 
				+ "Ontology.GONum, Ontology.GOType, Ontology.GODescript "
		+ "FROM Experiment, Probeset, Gene, OntolOfGene, Ontology "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Probeset.FBgn = OntolOfGene.FBgn "
		+ "AND OntolOfGene.GONum = Ontology.GONum "
		+ "AND (Ontology.GODescript LIKE ? ) "
		+ "AND Probeset.ProbesetID IN "
		+ "(SELECT DISTINCT ProbesetID FROM Experiment "
		+ "WHERE Experiment.FlyID = ? "
		+ "AND Experiment.Enrichment > 1 AND Experiment.SignifChange = '+' "
		+ "AND Experiment.SignalDetected > 0 ) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Ontology.GONum, Experiment.FlyID ";
			
    // query9: Tissue search by Abundance of category (GO Description) - free text
    final static String name9 = "TISSUE_ABUNDANCE";
    final static String query9 =  
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, " 
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, "
				+ "Probeset.ProbeDegeneracy, " 
				+ "Ontology.GONum, Ontology.GOType, Ontology.GODescript "
		+ "FROM Experiment, Probeset, Gene, OntolOfGene, Ontology "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Probeset.FBgn = OntolOfGene.FBgn "
		+ "AND OntolOfGene.GONum = Ontology.GONum "
		+ "AND (Ontology.GODescript LIKE ? ) "
		+ "AND Probeset.ProbesetID IN "
		+ "(SELECT DISTINCT ProbesetID FROM Experiment "
		+ "WHERE Experiment.FlyID = ? "
		+ "AND Experiment.Abundance > ? "
		+ "AND Experiment.SignalDetected > 0 ) "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Ontology.GONum, Experiment.FlyID ";
 
    // query10: Find Average Abundance for a particular stage/tissue combination (FlyID) - used in Tissue and Top search
    final static String name10 = "AVE_ABUNDANCE";
    final static String query10 = 
		"SELECT AVG(Abundance) " +
		"FROM Experiment " +
		"WHERE FlyID = ?";
    
    // query11: Probeset search for high Enrichment in particular tissue/stage (FlyID) - used by Top search — NO DISTINCT!
    final static String name11 = "PROBESET_TISSUE_ENRICHMENT";
    final static String query11 =
		"SELECT ProbesetID FROM Experiment "
		+ "WHERE FlyID = ? " 
		+ "AND Enrichment > 1 " 
		+ "AND SignalDetected > 0 "
		+ "AND SignifChange = '+' "
		+ "ORDER BY Enrichment DESC ";
    
    // query12: Probeset search for high Abundance in particular tissue/stage (FlyID) - used by Top search — NO DISTINCT!
    final static String name12 = "PROBESET_TISSUE_ABUNDANCE";
    final static String query12 =
		"SELECT ProbesetID FROM Experiment "
		+ "WHERE FlyID = ? " 
		+ "AND Abundance > ? " 
		+ "AND SignalDetected > 0 "
		+ "ORDER BY Abundance DESC ";
    
    // query13: Gene search from ProbesetID - used by Top search after identification of ProbeSets by pre-query
    final static String name13 = "GENE_BY_PROBESET";
    final static String query13 =
		"SELECT DISTINCT Gene.FBgn, Gene.CGNum, Gene.Symbol, Gene.Name, " 
				+ "Experiment.ProbesetID, Experiment.SignifChange, Experiment.Abundance, Experiment.AbundanceSE, "
				+ "Experiment.SignalDetected, Experiment.Enrichment, Experiment.FlyID, " 
				+ "Probeset.ProbeDegeneracy "
		+ "FROM Experiment, Probeset, Gene "
		+ "WHERE Gene.FBgn = Probeset.FBgn "
		+ "AND Probeset.ProbesetID = Experiment.ProbesetID "
		+ "AND Probeset.ProbesetID = ? "
		+ "ORDER BY Gene.FBgn, Experiment.ProbesetID, Experiment.FlyID ";
    
    // query14: For DevelopmentalSearch
    final static String name14 = "AVE_DEVEL_ENRICH_DIFF";
    final static String query14 =
		"SELECT (SELECT AVG(Enrichment) FROM Experiment WHERE FlyID = ?) - "
		+ "(SELECT AVG(Enrichment) FROM Experiment WHERE FlyID = ?) ";
    
    // query15: For DevelopmentalSearch
    final static String name15 = "AVE_DEVEL_ABUND_DIFF";
    final static String query15 =
    	"SELECT (SELECT AVG(Abundance) FROM Experiment WHERE FlyID = ?) - "
		+ "(SELECT AVG(Abundance) FROM Experiment WHERE FlyID = ?) ";
	
	// query16: For DevelopmentalSearch
	final static String name16 = "PROBESET_ENRICH";
	final static String query16 =
		"SELECT ProbesetID, Enrichment, SignalDetected FROM Experiment " 
		+ "WHERE FlyID = ? OR FlyID = ? "
		+ "ORDER BY ProbesetID, FlyID ";

    // query17: For DevelopmentalSearch
    final static String name17 = "PROBESET_ABUND";
    final static String query17 =
		"SELECT ProbesetID, Abundance, SignalDetected FROM Experiment " 
		+ "WHERE FlyID = ? OR FlyID = ? "
		+ "ORDER BY ProbesetID, FlyID ";
    
	// creates an array of all ParamQuerys	
	static ParamQuery pqList [] =
	{		
		new ParamQuery(name0, query0),
		new ParamQuery(name1, query1),
		new ParamQuery(name2, query2),
		new ParamQuery(name3, query3),
		new ParamQuery(name4, query4),
		new ParamQuery(name5, query5),
		new ParamQuery(name6, query6),
		new ParamQuery(name7, query7),
		new ParamQuery(name8, query8),
		new ParamQuery(name9, query9),
		new ParamQuery(name10, query10),
		new ParamQuery(name11, query11),
		new ParamQuery(name12, query12),
		new ParamQuery(name13, query13),
		new ParamQuery(name14, query14),
		new ParamQuery(name15, query15),
		new ParamQuery(name16, query16),
		new ParamQuery(name17, query17)
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
	
	/* --- Constants for simple entity queries --- */
	
	static String flyTissueQuery = 
			"SELECT FlyID, Stage, Tissue, Sex, UniTissue " +
			"FROM FlyAnat " +
			"ORDER BY Stage, Tissue ";
	
	static String probesetQuery =
			"SELECT ProbesetID, Abundance, SignalDetected, FlyID " +
			"FROM Experiment " +
			"ORDER BY ProbesetID, FlyID ";
	
	/* --- Get Methods for simple entity queries  --- */
	
	//returns SQL get all details from FlyAnat table	
	public static String getFlyTissueQuery()
	{
		return flyTissueQuery;
	}
	
	public static String getProbesetQuery()
	{
		return probesetQuery;
	}
}
