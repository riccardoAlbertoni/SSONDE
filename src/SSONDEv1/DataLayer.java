/** 
 * SSONDE, a framework providing an instance similarity enabling in a detailed comparison, 
 * ranking of resources through the comparison of their RDF ontology driven metadata.
 * From the theoretical point of view SSONDE adapts the instance similarity presented in 
 * "Asymmetric and Context-Dependent Semantic Similarity among Ontology Instances. J. Data 
 * Semantics 10: 1-30 (2008)" into a linked data settings.
 *
 * Copyright (C) 2012  Riccardo Albertoni,  Monica De Martino 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
**/
package SSONDEv1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;


public  class DataLayer {


	public double simStringNormalizedLevenshtein(String s, String t ){
		int [][] d=new int [s.length()][t.length()];

		//		for (int i=0;i<s.length();i++){
		//			for(int j=0;j<t.length();j++){
		//				d[i][j]=new int();
		//			}
		//		}

		for (int i=0; i<s.length();i++) {
			d[i][0]=i;
		}  
		for (int j=0; j<t.length();j++) {
			d[0][j]=j;
		}

		for(int j=1; j< t.length(); j++){
			for (int i=1; i< s.length(); i++){
				if (s.charAt(i)== t.charAt(j)){ 
					d[i][j]=d[i-1][j-1];
				}
				else
				{ 
					d[i][j]= Math.min(d[i-1][j]+1 ,d[i][j-1]+1); /* delection and Insertion*/
				}
			}
		}
		if (Debug.printDebug) {
			for (int i=0; i<s.length();i++ )
			{
				
			System.out.println(" ");
				for(int ii=0; ii< t.length();ii++)
				 System.out.print(d[i][ii] + "\t");
			}
		}
		return 1-(d[s.length()-1][t.length()-1])/(double) (Math.max(s.length(),t.length()));
	}

	/**
	 */
	public double simNumber(double x, double y){
		
		// this was symmetric 
		//return (1-Math.abs(y-x)/(y+x));
		//the asymmetric version follows
		return (x<y ? 1 : (1-(Math.abs(x-y)/(x))));
	}


	/**
	 */
	public double simString(String x, String y){
		//TODO are we interesete in implementing more sofisticated similarity?
		//TODO that should be made more efficient
		//if (x.equals(y)) return 1;
		//else return 0;
		AbstractStringMetric oc = new textSimilarityQuasiMetrics.AsymmetricOverlapCoefficient();
		return oc.getSimilarity(x, y); 

	}



	/** It takes two arrays of elements 
	 * @param firstSetValues, 
	 * @param secondSetVAlues
	 * @param first the index that shouldn't be considered for the first set to define the accoplishment during the recursion
	 * @param second the index that shouldn't be considered for the second set to define the accoplishment during the recursion
	 * @return the list of ways  the two set can combine by a bijective function  
	 */
	public static <T> ArrayList <ArrayList<Couple<T>>> generateSetAccomplishement(ArrayList <T> firstSetValues, ArrayList <T> secondSetValues, Collection <Integer> first, Collection <Integer>second){
		//TEST such procedure with a specific application
		int max,min,a=0,b=0;
		ArrayList <ArrayList<Couple<T>>> resultingWays=new ArrayList<ArrayList<Couple<T>>>();
		ArrayList<Couple<T>> p= new ArrayList<Couple<T>>();
		if (firstSetValues.size()<= secondSetValues.size()) {
			min = secondSetValues.size();
			max = firstSetValues.size();
			b= first.size();//new Integer(i);
			int i=first.size();		
			for (int ii=0; ii< min; ii++){
				a= new Integer(ii);	
				if (!first.contains(a)&&!second.contains(b)&& second.size()< max ){
					first.add(a);
					second.add(b);
					ArrayList <ArrayList <Couple<T>>> subPerm= generateSetAccomplishement(firstSetValues,secondSetValues,first,second);
					p= new ArrayList<Couple<T>>();
					if (subPerm.isEmpty()) { 
						p.add(new Couple<T>(firstSetValues.get(i),secondSetValues.get(ii)));
						resultingWays.add(p);
						first.remove(a);
						second.remove(b);
					}
					else for(Iterator <ArrayList <Couple<T>>> it=subPerm.iterator(); it.hasNext(); ){
						p = it.next();
						p.add(new Couple<T>(firstSetValues.get(i),secondSetValues.get(ii)));
						resultingWays.add(p);
						first.remove(a);
						second.remove(b);
					}
					//GC
					subPerm=null;
				}

			}								

		}
		else
			if (firstSetValues.size()> secondSetValues.size()) {
				max = secondSetValues.size();
				min = firstSetValues.size();
				b= second.size();//new Integer(i);
				int i=second.size();		
				for (int ii=0; ii< min; ii++){
					a= new Integer(ii);	
					if (!second.contains(a)&&!first.contains(b)&& first.size()< max ){
						first.add(b);
						second.add(a);
						ArrayList <ArrayList <Couple<T>>> subPerm= generateSetAccomplishement(firstSetValues,secondSetValues,first,second);
						p= new ArrayList<Couple<T>>();
						if (subPerm.isEmpty()) { 
							p.add(new Couple<T>(firstSetValues.get(ii),secondSetValues.get(i)));
							resultingWays.add(p);
							first.remove(b);
							second.remove(a);
						}
						else for(Iterator <ArrayList <Couple<T>>> it=subPerm.iterator(); it.hasNext(); ){
							p = it.next();
							p.add(new Couple<T>(firstSetValues.get(ii),secondSetValues.get(i)));
							resultingWays.add(p);
							first.remove(b);
							second.remove(a);
						}	
						//GC
						subPerm=null;
					}
				}								

			}
		//GC
		p=null;
		return resultingWays;	
	}
	
	
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	 // code  taken as it is from http://stackoverflow.com/questions/120283/working-with-latitude-longitude-values-in-java
		// double earthRadius = 3958.75; // in miles
	    double earthRadius= 6371; //in kilometers
	    
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }

}
