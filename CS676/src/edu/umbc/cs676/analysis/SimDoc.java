package edu.umbc.cs676.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SimDoc {

	private int doc1;
	private int doc2;
	private Double score; 
	private ArrayList<SimDoc> docList=new  ArrayList<SimDoc>();
	
	public SimDoc(int docId1, int docId2, double p_Score) {
		doc1 = docId1;
		doc2= docId2;
		score = p_Score;
	}

	public SimDoc() {
		// TODO Auto-generated constructor stub
	}

	public int getDoc1() {
		return doc1;
	}

	public void setDoc1(int doc1) {
		this.doc1 = doc1;
	}

	public int getDoc2() {
		return doc2;
	}

	public void setDoc2(int doc2) {
		this.doc2 = doc2;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	
	public void AddToList(SimDoc item)
	{
		docList.add(item);
	}
	
	public ArrayList<SimDoc> getList()
	{
		return docList;
	}
	

	public ArrayList<SimDoc> SortList(ArrayList<SimDoc> listT)
	{
		Collections.sort(listT, new CustomComparator() );
		return listT;
	}
}


class CustomComparator implements Comparator<SimDoc> {
    @Override
    public int compare(SimDoc o1, SimDoc o2) {
        return o1.getScore().compareTo(o2.getScore());
    }
}
