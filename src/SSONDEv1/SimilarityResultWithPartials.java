package SSONDEv1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import SSONDEv1.SimilarityResults.Node;


/**
 * It manages the intermediate results storing for each path, relation and criteria the result obtained by the similarity. 
 *  
 *  
 * @author riccardoalbertoni
 *
 */
public class SimilarityResultWithPartials extends SimilarityResults {
	static String simTemplate="http://www.w3.org/2002/07/owl#Thing";
	protected HashMap <String, double[][]> resP= new HashMap <String, double[][]> (); // the key are formed as path+rel/att+applied criteria 

	SimilarityResultWithPartials(ArrayList<String> instances,
			ArrayList<String> instanceLabels, ContextLayer ctx) {
		super(instances, instanceLabels);

		ArrayList<ArrayList<String>> l;

		LinkedList <String> recursivePathToDealWith= new LinkedList <String>();
		recursivePathToDealWith.add(simTemplate);
		do{
			String path=recursivePathToDealWith.remove();
			try {

				l=ctx.getRelevantSlotForClass(path);
				// list of attribute and relations mentioned for the path 
				Iterator <String> attIter = l.get(0).iterator();
				Iterator <String> relIter = l.get(1).iterator();

				while( attIter.hasNext() ){
					//double [][] r= new double [instances.size()][instances.size()]; // that should also inizialize the array 

					// what critera for att.next();
					String s= attIter.next();
					String criteria= ctx.getAttributeCriterion(s, path);
					
					int si= instances.size();
					double[][] cres= new double[si][si];
					for(int x=0; x < si; x++) {cres[x][x]=1;}
					
					resP.put(path+"+"+s+"+"+criteria, cres);
				}

				while( relIter.hasNext() ){
					String s= relIter.next();
					String criteria= ctx.getRelationCriterion(s, path);
					
					int si= instances.size();
					double[][] cres= new double[si][si];
					for(int x=0; x < si; x++) {cres[x][x]=1;}
					
					resP.put(path+"+"+s+"+"+ criteria, cres);

					//what if criteria in recursive way  ?
					if (criteria.equals("Simil")){
						recursivePathToDealWith.add(path+ ","+s);

					}


				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(!recursivePathToDealWith.isEmpty());

	}

	public void put( String inst1, String inst2, String path, String property, String criteria, double val){
		int i=instances.indexOf(inst1);
		int ii=instances.indexOf(inst2);
       String s= path+"+"+property+"+"+criteria;
		double [][] res  =resP.get(s);
		//FIXIT when we are in the recursion we compare object connected to the subject of comparison 
		if ( i >=0 && ii >=0 )
		res[i][ii]=val;
	}



	/**
	 * It returns the first n    entitities's most similar to uri  according the stored  SimilarityResult
	 * @param uri the uri of the entity we are interested in 
	 * @return a JSONObject containing the ordered results in the following form
	 * 
	 * { 
	 * 	"URI": "uriInstance", 
	 *  "rdfs:label":  "xyz",
	 * 	"Result":[
	 * 				{ "URI": "uri1",
	 * 				  "rdfs:label":  "xyz", 
	 * 				 "Val": 1,
	 * 					"DrillDown":
	 * 						[{path: " ",
	 * 						 property: " ",
	 * 						 operation:" ",
	 *  					 val: " "},
	 *  					{path: " ",
	 * 						 property: " ",
	 * 						 operation:" ",
	 *  					 val: " "}]  			 
	 *                              },
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

		Collections.sort(sa);		
	
		//Iterator <Node> iter= sa.descendingIterator();
		int ii=0;
		JSONObject o=null;
		JSONArray ja= new JSONArray();

		for (Node no : sa){
			if (ii<n){
				o= new JSONObject();
				try {
					o.put("URI", no.uri);
					String label= getMapInstanceLabel().get(no.uri);
					o.put("rdfs:label", label!=null?label:"no label");
					o.put("Val", no.val);
					
					JSONArray jaNested= new JSONArray();
					//jaNested va riempito con i risultati parziali
					
					Set <String> features= resP.keySet();
					for (String f :features){
						JSONObject oNested= new JSONObject();
						String pathComplete=f;
						int lpi=f.lastIndexOf("+");
						oNested.put( "operation" ,f.substring(lpi+1));
						f=f.substring(0, lpi);
						lpi=f.lastIndexOf("+");
						oNested.put("property", f.substring(lpi+1));
						f=f.substring(0, lpi);
						oNested.put("path", f);
						int indexAfterOrdering= this.instances.indexOf(no.uri);
						oNested.put("val", resP.get(pathComplete)[i][indexAfterOrdering]);
						jaNested.put(oNested);
					}
					o.put("DrillDown", jaNested);
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

	public void writeResult(Configuration c){
		JSONObject outputConfiguration= c.outputConfiguration;

		try {
		
			if (!outputConfiguration.getString("KindOfOutput").equals("JSONOrderedResultPartials")){
				// if we don't want the partial result in the output we can use the methods provided by the superclass
				super.writeResult(c);
			}else{
				//otherwise let us serialize in json also the partial results.
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

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
