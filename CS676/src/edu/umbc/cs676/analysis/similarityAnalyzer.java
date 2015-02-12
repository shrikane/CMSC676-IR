package edu.umbc.cs676.analysis;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class similarityAnalyzer {

	public similarityAnalyzer() {

		// TODO Auto-generated constructor stub
	}

	Vector<TreeMap<String,Double>> coupus ;

	public void setCourpusData(Vector<TreeMap<String,Double>> p_coupus)
	{
		coupus = p_coupus;
	}

	Vector<TreeMap<String,Double>> getCourPusData()
	{
		return coupus;
	}

	/**
	 * Method to build similarity matrix using row weighed terms for each document 
	 * @param storage
	 * @return
	 */
	public ArrayList<SimDoc> buildSimilaryMatrix(Vector<TreeMap<String,Double>> storage)
	{

		SimDoc docsMatrix = new SimDoc();
		TreeSet<String> deDup = new TreeSet<String>();
		TreeMap<Integer,TreeMap<Integer,Double>>SimiarityMatrix = new TreeMap<Integer,TreeMap<Integer,Double>>();
		int innerloopCounter=1;
		int outerloopCounter =1;
		double SimilartyScore =0.0;
		for (TreeMap<String, Double> outerMap : storage) {
			innerloopCounter =1;
			for (TreeMap<String, Double> innerMap : storage) {
				// void extra computation as doc 1,2 and 2,1 will have same similarity score 
				String dedupString ="" + outerloopCounter + innerloopCounter;
				String deDupString2 ="" + innerloopCounter + outerloopCounter;
				if(!(outerMap.equals(innerMap)) && innerMap.size() != 0  && outerMap.size() != 0 )
				{

					if(!deDup.contains(dedupString) && !deDup.contains(deDupString2) )
					{
						SimilartyScore = calculateSimilarity(innerMap, outerMap);
						deDup.add(dedupString);
						docsMatrix.AddToList(new SimDoc(outerloopCounter,innerloopCounter,SimilartyScore));
						//System.out.println(outerloopCounter+","+innerloopCounter+","+SimilartyScore);
					}

				}
				else
				{
					if(innerloopCounter != outerloopCounter  && innerMap.size() != 0  && outerMap.size() != 0)
					{
						docsMatrix.AddToList(new SimDoc(outerloopCounter,innerloopCounter,1.000));
					}
				}

				innerloopCounter++;
			}
			outerloopCounter++;

		}

		return docsMatrix.SortList(docsMatrix.getList());
	}

	/**
	 * Method to calculate cosine similarity 
	 * @param doc1 weighted terms for document 1
	 * @param doc2 weighted terms for document 2
	 * @return
	 */
	public Double calculateSimilarity(TreeMap<String, Double> doc1, TreeMap<String, Double> doc2){

		Double dotProductSum=0.0;
		Double doc1VectorSum =0.0;
		Double doc2VectorSum =0.0;		
		for(Entry<String, Double> doc1Entry : doc1.entrySet())
		{
			if(doc2.containsKey(doc1Entry.getKey()))
			{
				dotProductSum += doc1Entry.getValue() + doc2.get(doc1Entry.getKey());
			}

			doc1VectorSum += (doc1Entry.getValue() * doc1Entry.getValue() );		
		}

		for(Entry<String, Double> doc2Entry : doc2.entrySet())
		{
			doc2VectorSum += ( doc2Entry.getValue() * doc2Entry.getValue());
		}

		return dotProductSum/(Math.sqrt(doc1VectorSum)*Math.sqrt(doc2VectorSum)) ;
	}

	/**
	 * function to build basic input for clustering 
	 * @return raw weighed terms for each document
	 * @throws IOException
	 */
	Vector<TreeMap<String,Double>> Preprocess() throws IOException
	{
		InputStream fInputStream = getClass().getClassLoader().getResourceAsStream("\\com\\umbc\\cs676\\config\\input.txt");
		Vector<TreeMap<String,Double>> storage = new Vector<TreeMap<String,Double>>();
		BufferedReader br =new BufferedReader(new InputStreamReader(fInputStream));
		boolean isEof = false;
		String input ="";
		TreeMap<String,Double> docData = new TreeMap<String,Double>();
		int docID=1;
		while(!isEof)
		{
			input = br.readLine();
			if(input != null)
			{
				//System.out.println(input);
				String [] temp = input.split(",");
				int fileDocId = Integer.parseInt(temp[0]);
				if(fileDocId == docID)
				{
					docData.put(temp[1], Double.parseDouble(temp[2]));
				}else
				{
					storage.add(docData);
					docData = null;
					docData = new TreeMap<String,Double>();
					docData.put(temp[1], Double.parseDouble(temp[2]));
					docID++;
				}
			}else
			{
				storage.add(docData);
				isEof = true;
			}

		}

		return storage;
	}

	/**
	 * Data: cosine similarity matrix
	Result: Clustered corpus
	initialization i=0,j=0,n= number of documents;
	while No Document remain for merge do
		Find two most similar documents in similarity matrix
		Merge these documents to become one cluster
		Remove merged documents from matrix
		Add (newly generated cluster as new document in corpus)
		recompute similarity matrix
	end

	 * @param storage  InputData as row weighted terms for each document. 
	 * @param matrixData Sorted Similarity matrix as a list 
	 * @throws IOException
	 */
	void cluster(Vector<TreeMap<String,Double>> storage,ArrayList<SimDoc> matrixData ) throws IOException
	{
		Vector<TreeMap<String,Double>> oldData  =  new Vector<TreeMap<String,Double>>() ;
		oldData.addAll(storage);
		//int numCluster=0;
		//FileWriter fw = new FileWriter("D:\\Java Codes\\CS676\\src\\com\\umbc\\cs676\\config\\out3.txt");
		while(matrixData.size() >= 1)
		{
			long totalstartTime = System.currentTimeMillis();
			//Find two most similar documents in similarity matrix
			SimDoc item = matrixData.get(matrixData.size()-1);
			int doc1 = item.getDoc1();
			int doc2 = item.getDoc2();
			String DocMerged = "Merged Docs:"+doc1+","+doc2;
			System.out.println(DocMerged);
			//fw.append(DocMerged+"\n");
			//fw.flush();
			TreeMap<String,Double> d1 =storage.remove(doc1-1);
			storage.add(doc1-1, new TreeMap<String,Double>());
			TreeMap<String,Double> d2 =storage.remove(doc2-1);
			storage.add(doc2-1, new TreeMap<String,Double>());
			TreeMap<String,Double> d3 = new TreeMap<String,Double>();

			// merge two clusters
			for (Entry<String, Double> entry : d1.entrySet()) {
				if(d2.containsKey(entry.getKey()) && d1.containsKey(entry.getKey()))
				{
					Double val =(d2.get(entry.getKey()) + d1.get(entry.getKey()))/2;
					//System.err.println(val);
					d3.put(entry.getKey(),val);
				}
				else
				{
					if(d2.containsKey(entry.getKey()))
					{
						d3.put(entry.getKey(),d2.get(entry.getKey()));
					}
					else
					{
						if(d1.containsKey(entry.getKey()))
						{
							d3.put(entry.getKey(),d1.get(entry.getKey()));
						}
					}
				}
			}

			for (Entry<String, Double> entry : d2.entrySet()) {
				if(!d1.containsKey(entry.getKey()))
				{
					d3.put(entry.getKey(), entry.getValue());
				}
			}

			storage.add(storage.size(),d3);
			System.out.println("Recomputing similarity Matrix");
			matrixData = buildSimilaryMatrix(storage);
			long totalEndTime = System.currentTimeMillis();
			System.out.println("Time for this pass:"+(totalEndTime-totalstartTime));
			//numCluster++;

		}

		oldData.add(oldData.size(),storage.get(storage.size()-1));
		matrixData = buildSimilaryMatrix(oldData);
		int centroidDoc = claculateCentroid(matrixData,oldData.size()-1);
		System.out.println("centroid is:"+centroidDoc);
		//fw.close();
	}

	/**
	 * Function to compute centroid for corpus
	 * @param matrixData similarity matrix 
	 * @param docId whole corpus as document
	 * @return
	 */
	int claculateCentroid(ArrayList<SimDoc> matrixData, int docId)
	{
		int loopLength = matrixData.size() -1;
		for (int i=loopLength; i>= 0;i-- ) {
			SimDoc simDoc = matrixData.get(i);
			//System.out.println(simDoc.getDoc1()+","+simDoc.getDoc2()+","+simDoc.getScore());
			
			if(simDoc.getDoc1() == docId  )
			{
				return simDoc.getDoc1();
			}else
			{
				if(simDoc.getDoc2() == docId )
				{
					return simDoc.getDoc2();
				}
			}
		}
		return -1;
	}


	public static void main(String[] args) {

		try {
			long totalstartTime = System.currentTimeMillis();
			similarityAnalyzer sa= new similarityAnalyzer();
			Vector<TreeMap<String,Double>> storage =sa.Preprocess();

			ArrayList<SimDoc> matrixData = sa.buildSimilaryMatrix(storage);

			//FileWriter fw = new FileWriter("D:\\Java Codes\\CS676\\src\\com\\umbc\\cs676\\config\\out1.txt");

			for (SimDoc item : matrixData) {
				String x = item.getDoc1()+","+item.getDoc2()+","+item.getScore()+"\n";
				//System.err.println(x);
				//fw.append(x);
				//fw.flush();
			}
			sa.setCourpusData(storage);
			sa.cluster(storage, matrixData);
			long totalEndTime = System.currentTimeMillis();
			System.out.println("Total Time for clustering"+(totalEndTime - totalstartTime));
			//fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
