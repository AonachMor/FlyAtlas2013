/* FlyAtlas
Controller
DPL 18.06.2016
Updated 06.07.2023 to add Help as page
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class FlyAtlas extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private ExperimentSet[] exptSetList;		// Array of ExperimentalSets holding results
	private int numExptSets;					// Number of ExptSets - changed from exptSetList occupancy if ambiguity and duplicates removed
	private FlyTissueList  ftList;				// stores info about all fly tissues and stages - here needed for count

	public FlyAtlas() 
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		ftList = new FlyTissueList();
		
		/* CHECK & SET SEARCH PARAMETERS */
		
		boolean includeAmbiguous = false;				// ambiguous results (check box)
		if (request.getParameter("ambiguous")!=null)
		{
			includeAmbiguous = true;
		}
		
		boolean includeDuplicates = false;				// duplicates (check box)
		if (request.getParameter("duplicate")!=null)
		{
			includeDuplicates = true;
		}
		
		boolean includeErrors = false;
		if (request.getParameter("errors")!=null)		// include Errors
		{
			includeErrors = true;
		}
		
		boolean byPearson = true;
		if(request.getParameter("correlation")!=null)	// Pearson or Spearman
		{
			if(request.getParameter("correlation").equals("spearman"))
			{
				byPearson = false;
			}
		}
		
		int maxDisplayed = 50;							// Max results to display
		if(request.getParameter("maxdisplayed")!=null)
		{
			maxDisplayed = Integer.parseInt(request.getParameter("maxdisplayed"));
		}
				
		double rCut = 0.7;								// r cut-off (stat)	
		String rString = new String();					// String value for menus
		if(request.getParameter("rcut")!=null)
		{
			rString = request.getParameter("rcut");
			rCut = Double.parseDouble(request.getParameter("rcut"));
		}	
		
		String radioGene = new String();					// radio button checked for type of gene selection
		if (request.getParameter("radioGene") != null)
		{
			radioGene = request.getParameter("radioGene");
		}
		else
		{
			radioGene = "geneSymbol";
		}
		
		String radioGo = new String();					// radio button checked for type of GO selection
		if (request.getParameter("radioGo") != null)
		{
			radioGo = request.getParameter("radioGo");
		}
		else
		{
			radioGo = "goTerm";
		}
		
		String radioDev = new String();					// radio button checked for type of Development selection
		if (request.getParameter("radioDev") != null)
		{
			radioDev = request.getParameter("radioDev");
		}
		else
		{
			radioDev = "devAdult";
		}
		
		boolean showOptions = false;					// whether hide/show options div should be visible
		if(request.getParameter("showoptions")!=null)
		{
			showOptions = true;
		}
		
				/* BUILD START PAGE */
		if (request.getParameter("page") == null  && request.getParameter("search") == null)	// Defines startup 
		{		
			HomePage home = new HomePage(includeErrors);
			out.println(home.getHome());		
		}

				/* BUILD LINK START PAGES */
		else if (request.getParameter("page") != null)	// Page request identification to distinguish from results pages
		{		
			if (request.getParameter("page").equals("tissue"))			// Tissue page
			{
				TissuePage tissuePage = new TissuePage(includeAmbiguous, includeDuplicates, includeErrors);
				out.println(tissuePage.getHTML());
			}
			else if (request.getParameter("page").equals("gene"))		// Gene page
			{
				GenePage genePage = new GenePage(includeAmbiguous, includeDuplicates, includeErrors);
				out.println(genePage.getHTML());
			}
			else if (request.getParameter("page").equals("go"))			// Gene Ontology page
			{
				CategoryPage goPage = new CategoryPage(includeAmbiguous, includeDuplicates, includeErrors);
				out.println(goPage.getHTML());
			}
			else if (request.getParameter("page").equals("profile"))	// Profile page
			{
				ProfilePage profilePage = new ProfilePage(byPearson, includeErrors);
				out.println(profilePage.getHTML());
			}
			else if (request.getParameter("page").equals("top"))		// Top page
			{
				TopPage topPage = new TopPage(includeErrors);
				out.println(topPage.getHTML());
			}
			else if (request.getParameter("page").equals("development"))		// Development page
			{
				DevelopmentPage devPage = new DevelopmentPage(includeErrors, ftList);
				out.println(devPage.getHTML());
			}
			else if (request.getParameter("page").equals("home"))		// Home page
			{
				HomePage home = new HomePage(includeErrors);
				out.println(home.getHome());
			}
			else if (request.getParameter("page").equals("contact"))	// Feedback page — NB contact
			{
				FeedbackPage feedback = new FeedbackPage(includeErrors);
				out.println(feedback.getFeedback());
			}
			else if (request.getParameter("page").equals("help"))		// Home page
			{
				HelpPage help = new HelpPage(includeErrors);
				out.println(help.getHelp());
			}
		}
		
					/*------------------ SEARCHES FROM DIFFERENT PAGES  ------------------*/
		
		// GENE search
		else if (request.getParameter("search").equals("gene"))	
		{		
			// get and manicure search term
			String geneQuery = request.getParameter("gene");		// parameter for SQL
			geneQuery = new String(geneQuery.getBytes("8859_1"), "UTF-8");	
			geneQuery = geneQuery.trim();			//trim whitespace

			// search database
			GeneSearch geneSearch = new GeneSearch(geneQuery, radioGene);
			int initialListSize = geneSearch.getListSize();	// allows one to check if ambiguous gene removed 
			numExptSets = initialListSize;
			exptSetList = geneSearch.getExptSetList();
			
			// process raw returned results
			setUniqueDuplicates(exptSetList);					// sets flag so will know if listed duplicate is uniquq	
			boolean ambigRemoved = false;						// set flag for whether any ambiguous genes have been removed
			if (!includeAmbiguous)
			{
				exptSetList = removeAmbiguous(exptSetList);
				if(initialListSize > numExptSets)
				{
					ambigRemoved = true;
				}
			}
			if (!includeDuplicates)
			{
				exptSetList = sortDuplicates(exptSetList);
			}
			
			// Construct a HTML page of results NB the exptSetList may be shorter than the original
			GenePage genePage = new GenePage(exptSetList, numExptSets, geneQuery, includeAmbiguous, includeDuplicates,
								includeErrors, maxDisplayed, radioGene, showOptions, ambigRemoved, ftList);
			out.println(genePage.getHTML());
		}
		
		// CATEGORY (GO) search
		else if(request.getParameter("search").equals("go"))
		{					
			String goWord = request.getParameter("go");		// parameter for SQL
			goWord = new String(goWord.getBytes("8859_1"), "UTF-8");	
			goWord = goWord.trim();	//trim whitespace
			
			String[] ambigFBgnList = null;		// for list of ambig FBgn ids
			
			CategorySearch catSearch = new CategorySearch(goWord, radioGo);
			exptSetList = catSearch.getExptSetList();
			numExptSets = catSearch.getListSize();
			
			setUniqueDuplicates(exptSetList);					// sets flag so will know if listed duplicate is uniquq
			
			if (!includeAmbiguous)
			{
				ambigFBgnList = getAmbiguous(exptSetList);		// get list of ambig FBgn ids
				exptSetList = removeAmbiguous(exptSetList);
			}
			if (!includeDuplicates)
			{
				exptSetList = sortDuplicates(exptSetList);
			}
			// construct HTML page
			CategoryPage catPage = new CategoryPage(exptSetList, numExptSets, goWord, includeAmbiguous, includeDuplicates, 
							includeErrors, maxDisplayed, radioGo, showOptions, ambigFBgnList, ftList);
			out.println(catPage.getHTML());		
		}
		
		// PROFILE search (actually Gene search followed by ProfileSearch
		else if (request.getParameter("search").equals("profile"))
		{
			ProfilePage profilePage = null;							// need to declare here as two poss types

			String geneQuery = request.getParameter("gene");
			geneQuery = new String(geneQuery.getBytes("8859_1"), "UTF-8");
			geneQuery = geneQuery.trim();
			
			// search database
			GeneSearch geneSearch = new GeneSearch(geneQuery, radioGene);
			ExperimentSet[] trimmedExptSet = geneSearch.getExptSetList();	// array of Expt sets corresponding to probesets recognizing qy gene
			int initialSetCount = geneSearch.getListSize();					// number of these
					
			// remove ambiguous and sort
			numExptSets = initialSetCount;									// initialize to total - must be done BEFORE next two calls
			trimmedExptSet = removeAmbiguous(trimmedExptSet);				// this modifies numExptSets if any ambiguous			
			trimmedExptSet = sortDuplicates(trimmedExptSet);				// sort and assign best duplicate
			
			// gets best duplicate
			int pos = -1; 							// specifies position in array for selected gene
			for(int i=0; i<numExptSets; i++)
			{
				if(!trimmedExptSet[i].isDuplicate() || trimmedExptSet[i].isBestDuplicate())
				{
					pos = i;		// position of single unique gene or of best duplicate
				}
			}
			
			// for Profile Search duplicates are not allowed, so numExptSets must be 0 or 1
			if(numExptSets > 1)
			{
				numExptSets = 1;
			}
			
			if(numExptSets == 0)		// no genes found
			{
				boolean ambigRemoved = false;
				if(initialSetCount > 0)
				{
					ambigRemoved = true;
				}
				profilePage = new ProfilePage(byPearson, includeErrors, maxDisplayed, geneQuery, rString, radioGene, showOptions, ambigRemoved);
			}
			else if(numExptSets == 1)	// 1 gene found
			{
				ProbesetList probesets = new ProbesetList(trimmedExptSet[pos].getProbesetID());
				Probeset[] probeList = probesets.getList();
				int probeCount = probesets.getListSize();		// 14742 for vkg
	
				//makes a probeset object from the gene found (these store expressions as doubles)
				Probeset queryProbeSet = new Probeset(trimmedExptSet[pos].getProbesetID());
				for (int i = 0; i<100; i++)
				{
					if(trimmedExptSet[pos].getExpt(i)!=null)
					{
						Abundance abund = new Abundance(trimmedExptSet[pos].getExpt(i).getAbundance(), trimmedExptSet[pos].getExpt(i).getSignalCount(), 
								trimmedExptSet[pos].getExpt(i).getFlyID());
						queryProbeSet.addAbundance(abund);
					}
				}
				ProbeComparison comparison = new ProbeComparison(queryProbeSet, probeList, probeCount, byPearson, rCut);

				probeCount = comparison.getCount();
				probeList = comparison.getProbeList();				// 14183 for vkg
				
				// Bonferroni P correction  (followed by trim)
				double pCut = 0.05;			
				probeList = bonferroniCorr(probeList, pCut);
				probeCount = probeList.length;						// 10 for vkg		
				
				int aboveR =0;
				
				for(int i = 0; i < probeCount; i++)
				{
    				if(probeList[i].getRstat() > rCut)
    				{
    					aboveR++;
    				}
				}		
					// aboveR = 6 for vkg								
				
				//reorder by r
				CorrelationComparator correlationComparator = new CorrelationComparator();
				Arrays.sort(probeList, correlationComparator);
				
				Probeset[] newList = new Probeset[aboveR];
				System.arraycopy(probeList, 0, newList, 0, aboveR);
				probeList = newList;
				
				probeCount = probeList.length;
				
				ProfileSearch search = new ProfileSearch(probeList, probeCount, maxDisplayed);
				exptSetList = search.getExptSetList();
				numExptSets = search.getListSize();
				
				setUniqueDuplicates(exptSetList);					// sets flag so will know if listed duplicate is unique
				
				if (!includeAmbiguous)
				{
					exptSetList = removeAmbiguous(exptSetList);
				}
				if (!includeDuplicates)
				{
					exptSetList = sortDuplicates(exptSetList);
				}
				
				// remove any results that correspond to different probesets picking same gene
				String fbgnQuery = trimmedExptSet[pos].getFBgn();	
				exptSetList = removeQueryDuplicates(exptSetList, fbgnQuery);

				// construct HTML page with results
				profilePage = new ProfilePage(exptSetList, numExptSets, byPearson, geneQuery, maxDisplayed, 
						includeErrors, rString, queryProbeSet, trimmedExptSet[pos], radioGene, showOptions, ftList);
			}
			else
			{
				System.out.println("More than one gene found: must be some mistake!");
			}
			// output results page
			out.println(profilePage.getHTML());
		}	
		
		// TISSUE search
		else if (request.getParameter("search") != null)	
		{
			if (request.getParameter("search").equals("tissue"))
			{	
				// get parameters
				String go = request.getParameter("go");
				go = new String(go.getBytes("8859_1"), "UTF-8");
				go = go.trim();	//trim whitespace 
				
				int tissueID = Integer.parseInt(request.getParameter("tissue"));
				String order = request.getParameter("order");
				
				String[] ambigFBgnList = null;		// for list of ambig FBgn ids

				boolean byEnrichment = true;
				if (order.equals("abundance"))
				{
					byEnrichment=false;
				}
				
				// search database
				TissueSearch tissueSearch = new TissueSearch(go, tissueID, byEnrichment);
				exptSetList = tissueSearch.getExptSetList();
				numExptSets = tissueSearch.getListSize();
				
				setUniqueDuplicates(exptSetList);					// sets flag so will know if listed duplicate is uniquq
				
				if (!includeAmbiguous)
				{
					ambigFBgnList = getAmbiguous(exptSetList);		// get list of ambig FBgn ids
					exptSetList = removeAmbiguous(exptSetList);
					
				}
				
				if (!includeDuplicates)
				{
					exptSetList = sortDuplicates(exptSetList);
				}

				// construct HTML page with results
				TissuePage tissuePage = new TissuePage(exptSetList, tissueID, byEnrichment, go, numExptSets, includeAmbiguous, 
						includeDuplicates, maxDisplayed, includeErrors, showOptions, ambigFBgnList, ftList);
				out.println(tissuePage.getHTML());
			}
			
			// TOP search		
			else if (request.getParameter("search").equals("top"))
			{
				int tissueID = Integer.parseInt(request.getParameter("tissue"));		
				String order = request.getParameter("order");
				boolean byEnrichment = true;	
				if (order.equals("abundance"))
				{
					byEnrichment=false;
				}
				
				TopSearch topSearch = new TopSearch(tissueID, byEnrichment, maxDisplayed);
				exptSetList = topSearch.getExptSetList();
				numExptSets = topSearch.getListSize();
				
				setUniqueDuplicates(exptSetList);					// sets flag so will know if listed duplicate is uniquq
				
				// if ambiguous are to be removed, do so here
				if (!includeAmbiguous)
				{
					exptSetList = removeAmbiguous(exptSetList);	// Surely this is a space problem
				}
				// if duplicates are to be removed, do so here
				if (!includeDuplicates)
				{
					exptSetList = sortDuplicates(exptSetList);
				}
				
				// construct HTML top page with results
				TopPage topPage = new TopPage(exptSetList, tissueID, byEnrichment, numExptSets, maxDisplayed, includeErrors, showOptions, ftList);
				out.println(topPage.getHTML());
			}
			
			// DEVELOPMENT search
			else if (request.getParameter("search").equals("development"))
			{
				// get parameters
				// String rd = request.getParameter("radioDev");
				String uniTissue = request.getParameter("uniTissue");
				String order = request.getParameter("order");
				
				boolean byEnrichment = true;	
				if (order.equals("abundance"))
				{
					byEnrichment=false;
				}
				
				boolean adultMinusLarval;
				if(radioDev.equals("devAdult"))
				{
					adultMinusLarval=true;
				}
				else
				{
					adultMinusLarval=false;
				}
			
				DevelopmentalSearch devSearch = new DevelopmentalSearch(uniTissue, adultMinusLarval, byEnrichment, maxDisplayed, ftList);
				exptSetList = devSearch.getExptSetList();
				numExptSets = devSearch.getListSize();
				
				// if ambiguous are to be removed, do so here
				if (!includeAmbiguous)
				{
					exptSetList = removeAmbiguous(exptSetList);	// Surely this is a space problem
				}
				// if duplicates are to be removed, do so here
				if (!includeDuplicates)
				{
					exptSetList = sortDuplicates(exptSetList);
				}
				
				// construct HTML devel page with results
				DevelopmentPage devPage = new DevelopmentPage(exptSetList, uniTissue, byEnrichment, numExptSets, maxDisplayed, includeErrors, showOptions, adultMinusLarval, ftList);
				out.println(devPage.getHTML());
				
			}
		}
	}

	// Determines which duplicate ExperimentSet is best and sets flag
	public ExperimentSet[] sortDuplicates(ExperimentSet[] exptSetList)
	{
		//for all sets
		for (int i = 0; i < numExptSets; i++)
		{
			//if the set is duplicate and that group of duplicates hasn't already been resolved
			if (exptSetList[i].isDuplicate()&&!exptSetList[i].hasBeenChecked())
			{
				int mostPresent =i;
				exptSetList[i].setBestDuplicate();

				//compare against all other sets 
				for (int j = 0; j < numExptSets; j++)
				{
					if(j!=i)
					{
						//if it's another of the same gene
						if(exptSetList[i].getFBgn().equals(exptSetList[j].getFBgn()))
						{
							// means set not checked repeatedly
							exptSetList[j].duplicateChecked();

							if(exptSetList[mostPresent].getSetAffiCall() < exptSetList[j].getSetAffiCall())
							{
								exptSetList[mostPresent].notBestDuplicate();
								exptSetList[j].setBestDuplicate();
								mostPresent = j;
							}
						}
					}
				}
			}
		}
		return exptSetList;
	}

	// removes ambiguous sets and alters numExptSets accordingly
	// must be done before sortDuplicates so that with defaults the ambiguous one isn't set as the 'best duplicate', 
	// preventing any in the group from being displayed
	public ExperimentSet[] removeAmbiguous(ExperimentSet[] exptSetList)
	{
		ExperimentSet[] newArraySet = new ExperimentSet[numExptSets];
		int newGeneCount = 0;

		for (int i = 0; i < numExptSets; i++)
		{
			if(!exptSetList[i].isDegenerate())
			{
				newArraySet[newGeneCount] = exptSetList[i];
				newGeneCount++;
			}
		}

		numExptSets = newGeneCount;
		return newArraySet;
	}
	
	// removes any results for duplicates of query gene
	public ExperimentSet[] removeQueryDuplicates(ExperimentSet[] exptSetList, String fbgn)
	{
		ExperimentSet[] newArraySet = new ExperimentSet[numExptSets];
		int newGeneCount = 0;

		for (int i = 0; i < numExptSets; i++)
		{
			if(!exptSetList[i].getFBgn().equals(fbgn))
			{
				newArraySet[newGeneCount] = exptSetList[i];
				newGeneCount++;
			}
		}
		
		numExptSets = newGeneCount;
		return newArraySet;
	}
	
	// run first to set flag for any non-ambiguous duplicates that have no other non-ambiguous mates
	public void setUniqueDuplicates(ExperimentSet[] exptSetList)
	{
		boolean isUniqueDuplicate;		// default false 
		for (int i = 0; i < numExptSets; i++)
		{
			isUniqueDuplicate = false;	// reinitialize for each set
			if(!exptSetList[i].isDegenerate() && exptSetList[i].isDuplicate())	// look at any non-ambiguous duplicates
			{		
				isUniqueDuplicate = true;		// change default for duplicates so can negate
				for (int j = 0; j < numExptSets; j++)	// compare to the others in the list
				{
					if(i!=j && exptSetList[i].getFBgn().equals(exptSetList[j].getFBgn())
							 && exptSetList[j].isDegenerate() == false)
					{
						isUniqueDuplicate = false;
					}
				}
			}
			exptSetList[i].setUniqueDuplicate(isUniqueDuplicate);
		}
	}
	
	// Generates and returns a String array of FBgn ids of ambiguous genes (eliminating repeats of these)
	public String[] getAmbiguous(ExperimentSet[] exptSetList)
	{
		String[] ambigNameList = new String[numExptSets];	// sufficient capacity
		int ambigCount = 0;
		for (int i = 0; i < numExptSets; i++)
		{
			if(exptSetList[i].isDegenerate())
			{
			    boolean repeat = false;		// flag to prevent inclusion twice
			    for(int k=0; k<ambigCount; k++)
			    {
    				if(exptSetList[i].getFBgn().equals(ambigNameList[k]))
    				{
    				    repeat = true;
    				}
    			    }
    			    if(!repeat)
    			    {
    				ambigNameList[ambigCount] = exptSetList[i].getFBgn();
    				ambigCount++;
			    }
			}
		}
		// Put in an array of ambigCount length so no need to send this to constructor
		String [] trimmedAmbigNameList = new String[ambigCount];
		for(int j=0; j<ambigCount; j++)
		{
			trimmedAmbigNameList[j] = ambigNameList[j];
		}
		
		if(ambigCount>0)
		{
			return trimmedAmbigNameList;
		}
		else
		{
			return null;
		}
	}
	
	// Bonferroni correction of P values in Profile Search
	public Probeset[] bonferroniCorr(Probeset[] probeList, double pCut)
	{		
		for(int i = 0; i < probeList.length; i++)
		{	
			probeList[i].setPstat(probeList[i].getPstat() * probeList.length);
		}	
		
		Probeset[] probeListCut = new Probeset[probeList.length];	
		int count = 0;
		
		//cut out results with p value less than the cut-off
		for(int i = 0; i < probeList.length; i++)
		{	
			if(probeList[i].getPstat() < pCut)
			{
				probeListCut[count] = probeList[i];
				count++;
			}
		}
		
		Probeset[] shorterProbeList = new Probeset[count];	
		System.arraycopy(probeListCut, 0, shorterProbeList, 0, count);	
		probeList = shorterProbeList;
		return probeList;
	}


}
