package edu.umbc.cs676;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import edu.umbc.cs676.analysis.BasicAnalyzer;
import edu.umbc.cs676.analysis.IndexBuilder;

public class DocParser {

	public DocParser() {

	}

	/**
	 * Function to sort Map by Value
	 * @param map any treeMap to be sorted by value
	 * @return SortedSet with key Value pairs of map sorted by value
	 */
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	/**
	 * this function formats output string 
	 * @param input input unformatted string
	 * @return
	 */
	public static String formatOutput(String input)
	{
		String OutBuff =input.replaceAll(", ","\r\n");
		OutBuff =  OutBuff.replaceAll("=",",");
		OutBuff= OutBuff.replaceAll("\\{|\\}|\\[|\\]", "");
		return OutBuff;
	}

	/**
	 * 
	 * @param args
	 * args[0] input folder
	 * args[1] output folder
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		if(args.length <2)
		{
			System.out.println("java -jar <jarFile name> <input Directory> <output directory>");
		}
		else
		{
			try
			{
				
				File inputDir = new File(args[0]);
				String outputDir= args[1];
				File [] FilePtr = inputDir.listFiles();
				
				TreeMap< String, Integer> tokFreq=null;
				
				BufferedReader br = null;
				BasicAnalyzer an = new BasicAnalyzer();
				Vector<TreeMap<String,Integer>> storage = new Vector<TreeMap<String,Integer>>();
				long totalstartTime = System.currentTimeMillis();
				for (File file : FilePtr) {
					br = new BufferedReader(new FileReader(file));
					String line ="";
					boolean isEof= true;
					StringBuilder textData = new StringBuilder();
					// read file till it's end
					while(isEof)
					{
						line = br.readLine();
						if(line!=null)
						{
							line = an.preProcess(line);
							textData.append(line);
						}
						else
						{
							isEof= false;
						}
					}


					// generating one token output file per input file
					String [] tok =  an.getTok(textData);
					tokFreq = an.getFrequency(tok,true);
					
					// Pushing data to temporary in memory datastore
					storage.add(tokFreq);

				}
				int i=0;
				IndexBuilder indexer = new  IndexBuilder();
				tokFreq = an.getDict();
				//System.out.println(tokFreq.size());
				for (TreeMap<String, Integer> treeMap : storage) {
					// calculate term weight for each term in each document 
					//System.out.println("Time to calculate TF-IDF");
					
					 
					long endTime = System.currentTimeMillis();
					//System.out.println(FilePtr[i].getName()+","+(endTime - startTime));
					
					long startTime = System.currentTimeMillis();
					
					indexer.addToMatrix(i, an.calCulateBM25(treeMap));
					
					endTime = System.currentTimeMillis();
					System.out.println(FilePtr[i].getName()+","+(endTime - startTime));
					i++;
					if(i>500)
					{
						break;
					}
				}
				indexer.writeNewIndex(outputDir);
				long totalEndTime=  System.currentTimeMillis();
				System.out.println("Total Time: "+(totalEndTime - totalstartTime) + " ms"); 
			}catch(IOException ex)
			{
				System.out.println("Error in processing");
			}
		}
	}
}
