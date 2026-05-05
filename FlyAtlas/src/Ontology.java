// Models a gene ontology as in the Ontology table of the DB
// 29.08.2012

public class Ontology 
{
	private String id;				// GO Number 
	private String type;			// GO Type (biological process, molecular function or cellular component)
	private String description;		// Description of GO category
			
	public Ontology(String id, String type, String description)
	{	
		this.id = id;
		this.type = type;
		this.description = description;
	}
	
	public String getID()
	{	
		return id;
	}
	
	public String getType()
	{		
		return type;		
	}
	
	public String getDescription()
	{	
		return description;
	}
	
}
