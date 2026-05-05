// Calculates correlation scores for all probe sets and selects those above a selected 'r' value and below a selected 'p' value
// Probe correlation test for known PID- see ~ line 61
// 'calculate arrays' determines whether it is pearson or spearman

//REMEMBER ADD OPTION FOR STRICT CORRELATIONS (4/4) AND FOR SE RANK PRODUCTS
//CHANGE THE NAME OF EXPRESSION OBJECTS
// 10.10.2012

import java.util.Arrays;
import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.MatrixIndexException;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.ranking.NaturalRanking;
import org.apache.commons.math.stat.ranking.RankingAlgorithm;

public class ProbeComparison
{
	int newCount;
	Probeset[] newProbeList;
	boolean byPearson;
	Probeset queryProbeset;
	double [] test, query;
	double [][] grid;

	public ProbeComparison(Probeset queryProbeset, Probeset[] probeList, int probeCount, boolean byPearson, double rCut)
	{	
		this.byPearson = byPearson;
		this.queryProbeset = queryProbeset;

		//removes nulls
		Probeset[] trimmedSet = new Probeset[probeCount];
		System.arraycopy(probeList, 0, trimmedSet, 0, probeCount);
		probeList = trimmedSet;

		for (int i =0; i < probeCount; i++)
		{
			calculateArrays(probeList[i]);

			//gets r correlation
			PearsonsCorrelation pearson = new PearsonsCorrelation();
			probeList[i].setRstat(pearson.correlation(query, test));

			//gets P values
			pearson = new PearsonsCorrelation(grid);
			try 
			{
				probeList[i].setPstat(pearson.getCorrelationPValues().getEntry(0,1));
			} 
			catch (MatrixIndexException e) 
			{
				e.printStackTrace();
			} 
			catch (MathException e) 
			{
				e.printStackTrace();
			}

			/* FOR TESTING CORRELATIONS OF SETS WITH KNOWN PID
				if(probeList[i].getPid().equals("1630198_at")||probeList[i].getPid().equals("1640178_at"))
					System.out.println(probeList[i].getPid()+ "= " + probeList[i].getCorrelation() + "with P=" +probeList[i].getP());
			 */
		}

		int notNaN = 0;
		Probeset[] NaNfree = new Probeset[probeList.length];

		for(int NaNchecker = 0; NaNchecker < probeList.length; NaNchecker++)
		{
			if(!Double.isNaN(probeList[NaNchecker].getRstat()))
			{
				NaNfree[notNaN] = probeList[NaNchecker];
				notNaN++;
			}
		}
		
		probeList = new Probeset[notNaN];
		System.arraycopy(NaNfree, 0, probeList, 0, notNaN);
		probeCount = notNaN;
		
		// Scott had omitted this from the new version he sent me
		CorrelationComparator correlationComparator = new CorrelationComparator();
		Arrays.sort(probeList, correlationComparator);

		newCount = 0;

		newProbeList = new Probeset[probeCount];

		//taking only significant and strong correlation (respectively to booleans)
		for(int i = 0; i < probeCount; i++)
		{
			newProbeList[newCount] = probeList[i];
			newCount++;
		}

		//removes nulls
		Probeset[] trimmedSet2 = new Probeset[newCount];
		System.arraycopy(newProbeList, 0, trimmedSet2, 0, newCount);
		newProbeList = trimmedSet2;
	}

	public int getCount()
	{
		return newCount;
	}

	public Probeset[] getProbeList()
	{
		return newProbeList;
	}

	public void calculateArrays(Probeset testSet)
	{
		double[] tempTest = new double[100];
		double[] tempQuery = new double[100];

		int arrayPos = 0;

		//start at '0', empty cells at end will later be trimmed
		for(int i = 1; i < 100; i++)
		{

			if(queryProbeset.getAbundance(i)!=null && testSet.getAbundance(i)!=null)
			{
				if(queryProbeset.getAbundance(i).getSignalCount() > 0)
				{
					tempQuery[arrayPos] = queryProbeset.getAbundance(i).getLogAbundance();
				}
				else
				{
					tempQuery[arrayPos] = 0;
				}
				if(testSet.getAbundance(i).getSignalCount() > 0)
				{
					tempTest[arrayPos] = testSet.getAbundance(i).getLogAbundance();
				}
				else
				{
					tempTest[arrayPos] = 0;
				}
				arrayPos++;
			}	
		}

		//trims nulls from end of array
		test = new double[arrayPos];
		System.arraycopy(tempTest, 0, test, 0, arrayPos);
		query = new double[arrayPos];
		System.arraycopy(tempQuery, 0, query, 0, arrayPos);

		if(!byPearson)
		{
			RankingAlgorithm ranking = new NaturalRanking();
			query = ranking.rank(query);
			test = ranking.rank(test);		
		}

		//builds 2d array with COLUMNS as variables (as per ridiculous apache commons pearson spec)
		grid = new double[arrayPos][2];
		for(int i = 0; i < arrayPos; i++)
		{
			grid[i][0] = query[i];
			grid[i][1] = test[i];
		}
	}
}
