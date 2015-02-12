package edu.umbc.cs676.analysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;





public class IndexBuilder {

	TreeMap<String,TreeMap<Integer, Double>> dataStore ;
	
	public IndexBuilder() {
		dataStore = new  TreeMap<String, TreeMap<Integer,Double>>();
	}
	
	
	/***
	 * This function builds term matrix for corpus. this is  sparse matrix implemented using treemaps  
	 * @param DociD Document id 
	 * @param docTerms tterms in document specified by doc id 
	 */
	public void addToMatrix(int DociD, TreeMap<String,Double> docTerms)
	{
		for(Entry<String, Double> entry : docTerms.entrySet()) {
			  String key = entry.getKey();
			  TreeMap<Integer, Double> temp = null;
			  if(dataStore.containsKey(key))
			  {
				 temp = dataStore.get(key);
				 temp.put(DociD,entry.getValue());
				 dataStore.put(key, temp);
			  }
			  else
			  {
				  temp = new TreeMap<Integer, Double>();
				  temp.put(DociD, entry.getValue());
				  dataStore.put(key, temp);
			  }
			 
			}
		
		
		
	}
	
	
	/***
	 * This function crates new index/ re-indexes  from corpus by deleting older version
	 * @param outputFolderPath Output Folder path for index files
	 * @throws IOException
	 */
	public void writeNewIndex(String outputFolderPath) throws IOException
	{
		int termDocLineNum =0;
		FileWriter dictFileHandle = new FileWriter(outputFolderPath+"\\dict.txt",false);
		FileWriter termFileHandle = new FileWriter(outputFolderPath+"\\postings.txt",false);
		for(Entry<String, TreeMap<Integer, Double>> entry : dataStore.entrySet()) {
			// write to dict file
			dictFileHandle.append(entry.getKey()+"\n"+entry.getValue().size()+"\n"+termDocLineNum+"\n");
			dictFileHandle.flush();
			// write to term file
			TreeMap<Integer, Double> tempTermStore = entry.getValue();
			for(Entry<Integer, Double> termData : tempTermStore.entrySet() ) {
				termFileHandle.append((termData.getKey()+1)+","+termData.getValue()+"\n");
				termDocLineNum++;
				termFileHandle.flush();
			}
		}
		dictFileHandle.close();
		termFileHandle.close();
		
	}
}
