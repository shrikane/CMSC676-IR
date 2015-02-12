package edu.umbc.cs676.runtime;

import java.util.TreeMap;

import edu.umbc.cs676.analysis.BasicAnalyzer;

public class QueryParser {
	
	public TreeMap<String,Double> parseQuery(String[] qargs)
	{
		TreeMap<String,Double> Weightquery = new TreeMap<String,Double>();
		int loopLength = qargs.length;
		BasicAnalyzer analyzer = new BasicAnalyzer();
		
		try
		{
		if( qargs[0].equalsIgnoreCase("-retrievewt"))
		{
			for (int i = 1; i < loopLength; i= i+2) {
				qargs[i] = analyzer.preProcess(qargs[i]);
				Weightquery.put(qargs[i], Double.parseDouble(qargs[i+1]));
				//System.out.println("Query:"+qargs[i]+"\tWeight:"+Double.parseDouble(qargs[i+1]));
			}
			return Weightquery;
		}else
		{
			if(qargs[0].equalsIgnoreCase("-retrieve"))
			{
				for (int i = 1; i < loopLength; i++) {
					qargs[i] = analyzer.preProcess(qargs[i]);
					Weightquery.put(qargs[i], (double) 1 / (loopLength-1) );
					//System.out.println("Query:"+qargs[i]+"\tWeight:"+(double) 1 / (loopLength-1));
				}
				return Weightquery;
			}else
			{
				System.out.println("invalid option possible options are -retrievewt or -retrieve folowd by query terms (weights if you use retrievewt switch) sperated by space\n for e.g. -retrievewt live 0.2 stram 0.4 or \n retrieve live stream ");
				return null;
			}
		}
		}catch(Exception ex)
		{
			System.out.println("invalid option possible options are -retrievewt or -retrieve folowd by query terms (weights if you use retrievewt switch) sperated by space\n for e.g. -retrievewt live 0.2 stram 0.4 or \n -retrieve live stream ");
			return null;
		}
		
	}

}
