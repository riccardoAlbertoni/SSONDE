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

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * A class to store and manda the similarity results  
 * @author Riccardo Albertoni
 *
 */
public class SimilarityResults {
	private double [][] res;
	private int size=0;
	//list of instances represented in the result set
	protected ArrayList <String> instances = new ArrayList <String>(); 

	//Mapping instances uri - rdfs:label represented in the result set
	private HashMap<String, String> mapInstanceLabel= new HashMap<String, String>();

	/**
	 * Constructors, it initializes a result set nxn where n is the length of the instances 
	 * @param instances a ArrayList containing the URI's instances that have to be assessed by similarity
	 * @param instanceLabels an ArrayList containing the URI's instances labels
	 */
	SimilarityResults(ArrayList <String>instances, ArrayList<String> instanceLabels){
		this.instances=instances;
		size=instances.size();
		for (int i=0; i<instances.size(); i++){
			String label=instanceLabels.get(i);
			getMapInstanceLabel().put(instances.get(i),  label!=null? label: " ");
		}
		setRes(new double [size][size]);
		//		for (int i=0; i<this.size; i++  ){
		//			for (int ii=0; ii<this.size; ii++  )
		//				res[i][ii]=0;
		//		}

	}
	/**
	 * It associate a value val as result of the inst1 and inst2 similatity assessment
	 * @param inst1 first  instance's URI
	 * @param inst2 second instance's URI
	 * @param val value to be associated as result
	 */
	public void put( String inst1, String inst2, double val){
		int i=instances.indexOf(inst1);
		int ii=instances.indexOf(inst2);
		getRes()[i][ii]=val;
	};

	public String toString(){
		String r=new String("");
		//for( int ii=0; ii<instances.size();ii++) r.concat("\t "+instances.get(ii));
		for(int i=0; i<instances.size(); i++){
			r=r.concat("\n"+ instances.get(i));
			for(int ii=0; ii<instances.size(); ii++){
				r=r.concat("\t "+ getRes()[i][ii]);
			}	
		}

		return r;	
	}
	/**
	 * It writes the resultSet as indicated in the configuration
	 * @param c configuration 
	 */
	public void writeResult(Configuration c){
		JSONObject outputConfiguration= c.outputConfiguration;
		try {
			switch (KindOfOutput.valueOf(outputConfiguration.getString("KindOfOutput"))) {
			case CVSFile: 
				CSVWriter writer = null;
				try {
					writer = new CSVWriter(new FileWriter(outputConfiguration.getString("FilePath")), '\t');
					String r="";
					String[] entries;
					for(int i=0; i<instances.size(); i++){
						String scomm=instances.get(i);
						r=r.concat(scomm);
						//add the label to the result set
						scomm=this.getMapInstanceLabel().get(scomm);
						r=r.concat("#"+(scomm!=null?scomm:"no label"));
						for(int ii=0; ii<instances.size(); ii++){
							r=r.concat("#"+getRes()[i][ii]);

						}
						entries=r.split("#");
						writer.writeNext(entries);
						r="";
					}
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case JSONOrderedResult:
				JSONArray a= new JSONArray();
				for(int i=0; i<instances.size(); i++){
					a.put(getJSONOrderResultFor(instances.get(i), outputConfiguration.getInt("NumberOfOrderedResult")));
				}
				JSONWriter w;
				try {
					FileWriter fw=new FileWriter(outputConfiguration.getString("FilePath"));
					w = new JSONWriter(fw);
					w.object().key("ResultArray").value(a).endObject();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * It returns the first n    entitities's most similar to uri  according the stored  SimilarityResult
	 * @param uri the uri of the entity we are interested in 
	 * @return a JSONObject containing the ordered results in the following form
	 * 
	 * { 
	 * 	"URI": "uriInstance", 
	 * 	"Result":[
	 * 				{ "URI": "uri1", 
	 * 				 "Val": 1 },
	 * 				{ URI: "uri2",
	 *  			  "Val": 0.987  },
	 * 					...
	 * 				{ URI: "urin",
	 * 				"Val": 0.987  }, 
	 * 
	 * 			 ]
	 * }
	 * 
	 */
	public JSONObject getJSONOrderResultFor(String uri, int n){

		// let's order the first n results
		int i= instances.indexOf(uri);
		double [] a= getRes()[i]; 
		// if we use a treeSet 
		//TreeSet <Node> sa=new TreeSet<Node>(new NodeComparator());
		ArrayList<Node> sa= new ArrayList<Node>();
		for (int ii=0; ii< a.length;ii++){
			sa.add(new Node(instances.get(ii), a[ii]));

		}

		Collections.sort(sa);		//Iterator <Node> iter= sa.descendingIterator();
		int ii=1;
		JSONObject o=null;
		JSONArray ja= new JSONArray();

		for (Node no : sa){
			if (ii<=n){
				o= new JSONObject();
				try {
					o.put("URI", no.uri);
					String label= getMapInstanceLabel().get(no.uri);
					o.put("rdfs:label", label!=null?label:"no label");
					o.put("Val", no.val);
					ja.put(o);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ii++;
			} else break;
		}
		JSONObject r=null;
		try {
			// retrieve the rdf label for the uri
			String label= getMapInstanceLabel().get(uri);
			r = new JSONObject().put("URI", uri).put("rdfs:label", label!=null? label: "no label" ).put("Result", ja);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;

	}

	public void setRes(double [][] res) {
		this.res = res;
	}
	public double [][] getRes() {
		return res;
	}

	public void setMapInstanceLabel(HashMap<String, String> mapInstanceLabel) {
		this.mapInstanceLabel = mapInstanceLabel;
	}
	public HashMap<String, String> getMapInstanceLabel() {
		return mapInstanceLabel;
	}

	class Node implements Comparable{
		public String uri;
		public double val;
		public Node (String u, double v ){
			uri=u;
			val=v;
		}

		public int compareTo(Object rv){
			double rvval= ((Node )rv).val;
			return (val < rvval ? 1 :( val==rvval ? 0:-1));

		}
	};
	public  class NodeComparator implements Comparator{
		public int compare(Object o1, Object o2){
			double valo1=((Node)o1).val;
			double valo2=((Node)o2).val;
			return (valo1 < valo2 ? 1 :( valo1==valo2 ? 0:-1));

		}
	}

}
