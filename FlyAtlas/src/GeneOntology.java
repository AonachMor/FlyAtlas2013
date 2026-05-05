// Stores data about gene ontologies - HOWEVER EVIDENCE (not currently used -REMOVE) IS SPECIFIC FOR CONTEXT 

public class GeneOntology 
{
	private String number, type, description, evidence;
			
	public GeneOntology(String n, String t, String d, String e)
	{	
		number = n;
		type = t;
		description = d;
		evidence = e;
	}
	
	public String getNumber()
	{	
		return number;
	}
	
	public String getType()
	{		
		return type;		
	}
	
	public String getDescription()
	{	
		return description;
	}
	
	public String getEvidence()
	{		
		return evidence;		
	}
	
	public void addEvidence(String e)
	{	
		if(evidence.indexOf(e) == -1)
		{
			evidence = evidence + " & " + e;
		}
	}
}
