// Models Fly table from database
// DPL 16.08.2012

public class FlyTissue
{
	private int id;				// FlyID field
	private String stage;		// Adult or Larval		NB case
	private String tissue;		// Tissue (can include additional fly info)
	private String sex;			// Sex (Male or Female)
	private String uniTissue;	// adult/larval unified tissue name (for matching on table layout)
	
	public FlyTissue(int id, String stage, String tissue, String sex, String uniTissue)
	{
		this.id = id;
		this.stage = stage;
		this.tissue = tissue;
		this.sex = sex;
		this.uniTissue = uniTissue;
	}
	
	// Accessor methods	
	public int getID()
	{
		return id;
	}
	
	public String getStage()
	{
		return stage;
	}
	
	public String getTissue()
	{
		return tissue;
	}
	
	public String getSex()
	{
		return sex;
	}
	
	public String getUniTissue()
	{
		return uniTissue;
	}
}
