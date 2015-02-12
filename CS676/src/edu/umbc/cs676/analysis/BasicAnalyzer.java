package edu.umbc.cs676.analysis;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;





 
public class BasicAnalyzer {

	
	private static final HashSet<String> StopWord  = new HashSet<String>();
	private double termCount;
	private double docCount;
	private double k1;
	private double b;
	private TreeMap< String, Integer> dict;
	{
		// reading and creating stop word list to ignore 
		try
		{
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("\\com\\umbc\\cs676\\config\\stopWords.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			Boolean isEof = false;
		while(!isEof)
		{
			String input = br.readLine();
			if(input != null)
			{
				StopWord.add(input);
			}else
			{
				isEof = true;
				br.close();
				inputStream.close();
			}
		}
		}catch(Exception ex)
		{
		 System.err.println("Invalid Config file- Stopwords.txt");
		 ex.printStackTrace();
		}
		
		
		
	
	}
	
	public String preProcess(String line)
	{
		line = line.toLowerCase();
		// strip off HTML tags and special characters
		line = line.replaceAll("(<[^>]*>)", " ");
		line = line.replaceAll("[^\\w]|\\d+|_|(aa|zz|ab)+", " ");
		return line;
	}
	
	public BasicAnalyzer() {
		dict = new TreeMap<String, Integer>();
		k1=1.2;
		b = 0.75;
		termCount =0;
		docCount =0;
	}
	
	
	/***
	 * Function to check if input term resides in stop word list
	 * @param term
	 * @return
	 */
	public boolean isStopWord(String term)
	{
		if(StopWord.contains(term))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	
	
	
	/**
	 * 
	 * @param input textInput from file of String 
	 * @return array of tokens in form of string objects
	 */
	public String[] getTok(StringBuilder input)
	{
		
		return input.toString().split("\\s+");
	}
	
	/**
	 * Building global dictionary in which key is term and value is number of documents in which term is appeared 
	 * @param item
	 */
	private void buildDict(String item)
	{
		if(dict.containsKey(item))
		{
			int tempFrqCounter = dict.get(item);
			dict.put(item,++tempFrqCounter );
			
		}
		else
		{   
			dict.put(item, 1);
		}
	}
	
	/**
	 * 
	 * @param tokens token string array 
	 * @pram isBuildDict if true builds global dictionary 
	 * @return treeMap with unique token and it's frequency sorted by token(s) 
	 */
	public TreeMap<String,Integer> getFrequency(String[] tokens,boolean isBuildDict)
	{
		TreeMap<String,Integer> freq = new TreeMap<String, Integer>();
		int tempFrqCounter = 0;
		docCount ++;
		termCount = (termCount + tokens.length);
		for (String item : tokens) {
			item= item.trim();
			// avoid stop words and white spaces
			if(!StopWord.contains(item) && !item.matches("\\s+") && item.length() > 1 && item.length() < 24)
			{
				// We enumerated tokenized list to push data on map. 
				// If data already exists on map we increment term frequency by one
				if(freq.containsKey(item))
				{
					tempFrqCounter = freq.get(item);
					freq.put(item, ++tempFrqCounter);
				}else
				{
					freq.put(item, 1);
					buildDict(item);
				}
				
			}
		}
		
		return freq;
	}

	/**
	 * 
	 * @param tokFreq row term frequency treemap to calculate TF-IDF weight for each term
	 * @return TreeMap with term and it's weight.
	 */
	public TreeMap<String, Double> calCulateTfIdf(TreeMap< String, Integer> tokFreq)
	{
		int totalTerms = tokFreq.size();
		 TreeMap< String, Double> TFWeight = new TreeMap<String, Double>();
		for(Entry<String, Integer> entry : tokFreq.entrySet()) {
			  String key = entry.getKey();
			  Integer value = entry.getValue();
			  Double  tf = (double) value/totalTerms;
			  Double idf = Math.log((docCount/dict.get(key)));
			  //System.out.println("term"+entry.getKey()+"\ttf"+tf+"\tidf"+docCount);
			 TFWeight.put(key, tf*idf);
			}
		
		return TFWeight;
		
	}
	
	/**
	 * 
	 * @param tokFreq row term frequency treemap to calculate BM25 weight for each term
	 * @return TreeMap with term and it's weight.
	 */
	public TreeMap<String, Double> calCulateBM25(TreeMap< String, Integer> tokFreq)
	{
		
		 TreeMap< String, Double> TFWeight = new TreeMap<String, Double>();
		 Integer tokFreqSize =tokFreq.size();
		 Double  avgDocSize =getAvgDocLength();
		 DecimalFormat df = new DecimalFormat("#.####");
		for(Entry<String, Integer> entry : tokFreq.entrySet()) {
			  String key = entry.getKey();
			  Integer value = entry.getValue();
			  Double partA= ((k1+1) * value)/((k1*((1-b)+((b*tokFreqSize )/avgDocSize)))+value);
			  Double partB = Math.log((docCount-dict.get(key)+0.5)/(dict.get(key)+0.5));
			  TFWeight.put(key,Double.valueOf(df.format(partA*partB)) );
			}
		return TFWeight;
	}

	public TreeMap<String, Integer> getDict() {
		return dict;
	}
	

	
	public Double getAvgDocLength(){
		return (termCount/docCount);
	}
	
}
