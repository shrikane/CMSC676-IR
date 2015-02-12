package edu.umbc.cs676.runtime;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class IndexSearcher {

	private TreeMap<String,TreeMap<Integer, Double>> index ;

	/***
	 * Builds term matrix in memory from index
	 */
	IndexSearcher()
	{
		BufferedReader dictFileHandel = null;
		BufferedReader postingFileHandel = null;
		try {

			InputStream dictInputStream = getClass().getClassLoader().getResourceAsStream("\\com\\umbc\\cs676\\config\\dict.txt");
			InputStream postingsInputStream = getClass().getClassLoader().getResourceAsStream("\\com\\umbc\\cs676\\config\\postings.txt");
			dictFileHandel = new BufferedReader(new InputStreamReader(dictInputStream)); // BufferedReader( new FileReader(indexDirPath+"\\dict.txt"));
			postingFileHandel =new BufferedReader(new InputStreamReader(postingsInputStream)); //new BufferedReader( new FileReader(indexDirPath+"\\postings.txt"));
			boolean isEOF = false;
			index = new TreeMap<String,TreeMap<Integer, Double>>();
			while(!isEOF)
			{
				String dictEntry = dictFileHandel.readLine();
				String term = dictEntry;
				dictEntry = dictFileHandel.readLine();
				dictFileHandel.readLine(); // for pointers 
				if(dictEntry == null)
				{
					isEOF = true;
					break;
				}
				int postingLength = Integer.parseInt(dictEntry);
				for(int loopIndex =0 ;loopIndex <postingLength ; loopIndex++ )
				{
					String postingEntry = postingFileHandel.readLine();
					if(postingEntry == null )
					{
						isEOF = true;
						break;
					}
					String [] postingEntryList = postingEntry.split(",");
					//System.out.println("Term:\t"+term+"\tDocID:\t"+postingEntryList[0]+"\tFreq:\t"+postingEntryList[1]);
					if(index.containsKey(term))
					{
						TreeMap<Integer, Double> tempPostingList = index.get(term);
						tempPostingList.put(Integer.parseInt(postingEntryList[0]), Double.parseDouble(postingEntryList[1]));
						index.put(term, tempPostingList);
					}else
					{
						TreeMap<Integer, Double> tempPostingList = new TreeMap<Integer, Double>();
						tempPostingList.put(Integer.parseInt(postingEntryList[0]), Double.parseDouble(postingEntryList[1]));
						index.put(term, tempPostingList);
					}
				} 
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				dictFileHandel.close();
				postingFileHandel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



	}


	/**
	 * Function to sort Map by Value
	 * @param map any treeMap to be sorted by value
	 * @return SortedSet with key Value pairs of map sorted by value
	 */
	public static <K, V extends Comparable<V>> TreeMap<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0) return 1;
				else return compare;
			}
		};
		TreeMap<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	/***
	 * function fetches relevant documents as per query terms
	 * @param qTerms query terms along with it's weight
	 * @return
	 */
	public TreeMap<Integer, Double> search (TreeMap<String, Double> qTerms)
	{
		if(qTerms == null)
		{
			return null;
		}
		TreeMap<Integer,Double> resultSet = new TreeMap<Integer,Double>();
		for (Entry<String,Double> term : qTerms.entrySet()) {
			String currentTok = term.getKey();
			// fetch relevant documents 
			TreeMap<Integer,Double> termWeightRow = index.get(currentTok);

			if(termWeightRow != null)
			{
				for(Entry<Integer,Double> entry : termWeightRow.entrySet()) {
					int docId = entry.getKey();
					if(resultSet.containsKey(docId))
					{
						double newWeight = resultSet.get(docId)  + term.getValue() * entry.getValue() ;
						resultSet.put(docId, newWeight );
						//System.out.println("Score:"+newWeight);

					}else
					{
						// if more than 1 terms exist in same document check step 3 in section 3.3 of report
						resultSet.put(docId,term.getValue() * entry.getValue());
						//System.out.println("Term:"+currentTok+"\tlength:"+ docId );

					}

				}
			}

		}

		resultSet =   sortByValues(resultSet);




		return resultSet;
	}



	public static void main(String[] args) {


		DecimalFormat xFormat = new DecimalFormat("000");
		IndexSearcher is = new IndexSearcher();
		String [] qStrings ={"-retrieve diet","-retrieve diet","-retrieve international affairs","-retrieve Zimbabwe","-retrieve computer network","-retrieve hydrotherapy","-retrieve identity theft","-retrievewt computer 0.1 identity 0.7 theft 0.3","-retrievewt computer 0.9 identity 0.7 theft 0.3"};

		for (String string : qStrings) {
			System.out.println(string);

			long totalstartTime = System.currentTimeMillis();
			TreeMap<Integer, Double> resultSet = is.search(new QueryParser().parseQuery(string.split("\\s+")));
			System.out.println("Number of hits:"+resultSet.size());
			int loopCounter =0;
			if(resultSet.size() ==0 )
			{
				System.out.println("No Documents found");
			}
			else
			{
				if(resultSet != null)
				{
					for (Entry<Integer, Double> entry : resultSet.entrySet()) {
						if(loopCounter == 10)
							break;
						System.out.println(xFormat.format(entry.getKey())+".html\t"+entry.getValue());
						loopCounter++;
					}
				}

			}
			long totalEndTime = System.currentTimeMillis();
			System.out.println("Total Time: "+(totalEndTime - totalstartTime) + " ms"); 
		}
	}

}
