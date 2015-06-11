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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/** 
 * @author RiccardoAlbertoni
 * @date  November 5, 2012
 * 
 * It represents the subset of ontology entities that are relevant for a specific similarity assessment.
 * 
 * @version 1.2
 * - Contexts representations encoded in JSON are added
 * 
 */
public class ContextLayer  {
	/**
	 * It returns true if  the attribute given a Path of recursion has been defined as relevant for the context.   
	 * @throws Exception 
	 */
	@Deprecated
	public boolean isAttributeInContext(String attribute, String path) throws Exception{
		ArrayList <CIC>s=attributeMap.get(path);

		// if the path is not in the context by default the result if false, except in the case the context can be inherited.
		if (s==null) {
			// two distinct if because  addInhe.. has to be executed only in s==null. probably the optimization pertaining to the logical and would have had the same effect but I am not sure so ..   
			if (!addInheritaceToContext(path)) {
				//GC
				s=null;
				return false;
			}
			else s=attributeMap.get(path);
		}

		for (Iterator <CIC> i=s.iterator(); i.hasNext(); ){
			CIC attr= i.next();
			if (attr.getSlot()==attribute) {
				//GC
				s=null;
				return true;
			}
		}
		//GC
		s=null;
		return false;
	}



	/**
	 * It returns the criteria (ex. "Simil", "Count", "Iter")to be used during the  attributes' values comparison.
	 * @throws Exception in the case the  attribute is not contained in the context
	 */
	public String getAttributeCriterion(String attribute, String path) throws Exception{

		ArrayList <CIC> s=attributeMap.get(path);

		if (s==null)
			//			two distinct if because  addInhe.. has to be executed only in s==null. probably the optimization pertaining to the logical and would have had the same effect but I am not sure so ..   
			if (!addInheritaceToContext(path)){
				//throw new  Exception("There is not path "+ path+ " defined in the context");
			} else s=attributeMap.get(path);

		for (Iterator<CIC> i=s.iterator(); i.hasNext(); ){
			CIC attr= i.next();
			if (attr.getSlot()==attribute){
				//GC
				s=null;
				return attr.getCriterion();
			}
		}
		throw new  Exception(" the Attribute "+ attribute+" is not contained in the Context");

	}



	/** it returns true if the relation is relevant for the similarity asssessment considering a given path
	 * @throws Exception 
	 */
	public boolean isRelationInContext(String relation, String path) throws Exception{
		ArrayList <CIC> s=relationMap.get(path);

		// if the path is not in the context by default the result if false
		if (s==null){
			//			two dinstict if becouse  addInhe.. has to be executed only in s==null. probably the ottimization pertaning to the logical and would have had the same effect but I am not sure so ..   
			if (!addInheritaceToContext(path)){
				//GC
				s=null;
				return false;
			}
			else  s=relationMap.get(path);
		}

		for (Iterator <CIC> i=s.iterator(); i.hasNext(); ){
			CIC attr= i.next();
			if (attr.getSlot()==relation) {
				//				GC
				s=null;
				return true;
			}
		}
		//		GC
		s=null;
		return false;
	}	




	/**
	 *It returns the criteria (ex. "Simil", "Count", "Iter" )to be used during the relation' values comparison.
	 * @throws Exception in the case the  relation is not contained in the context
	 * 
	 */
	public String getRelationCriterion(String relation, String path) throws Exception{
		ArrayList<CIC> s= relationMap.get(path);

		if (s==null) {
			//			two distinct if because  addInhe.. has to be executed only in s==null. probably the optimization pertaining to the logical and would have had the same effect but I am not sure so ..   
			if (!addInheritaceToContext(path)) throw new  Exception("There is not path "+ path+ " defined in the context");
			else s= relationMap.get(path);
		}
		for (Iterator <CIC> i=s.iterator(); i.hasNext(); ){
			CIC attr= i.next();
			if (attr.getSlot()==relation) {
				//GC
				s=null;
				return  attr.getCriterion();
			}
		}
		throw new  Exception(" the Attribute "+ relation+" is not contained in the Context");

	}



	/** It returns a couple of arrayList of string representing respectively the set of relevant attribute or attribute
	 * @param path represent the path of recursion with respect to check which are the relevant slots  
	 * @throws Exception 
	 */
	public ArrayList<ArrayList <String>> getRelevantSlotForClass(String path) throws Exception{
		ArrayList<ArrayList <String>> rr= new ArrayList<ArrayList <String>>();
		ArrayList <String> r= new ArrayList <String>(); 
		ArrayList <CIC> ca,cr;

		ca=this.attributeMap.get(path);
		cr=this.relationMap.get(path);
		if (ca==null && cr==null){ // two distinct if because  addInhe.. has to be executed only in s==null. probably the optimization pertaining to the logical and would have had the same effect but I am not sure so ..   
			boolean test= addInheritaceToContext(path);
			if ( test){
				ca=this.attributeMap.get(path);
			}
			//throw new Exception("no attributes are defined as relevant for the  path "+path);
			// System.out.println("no attributes are defined as relevant for the  path "+path);
		}
		//
		if (ca!=null)
			for(CIC a: ca){
				r.add(a.getSlot());
			}
		rr.add(r);

		r= new ArrayList <String>();

		cr=this.relationMap.get(path);
		if (cr==null){
			boolean test= addInheritaceToContext(path);
			if (test){
				cr=this.relationMap.get(path); //throw new Exception("no attributes are defined as relevant for the  path "+path);
			}

			// System.out.println("no attributes are defined as relevant for the  path "+path);
			//System.out.println("no relations are defined as relevant for the  path "+path);
		}
		//throw new Exception("no relations are defined as relevant for the  path "+path);
		if (cr!=null)
			for(CIC a: cr){
				r.add(a.getSlot());
			}

		rr.add(r);
		//GC
		r=null;
		cr=null;
		ca=null;

		return rr;
	}



	/**
	 * @uml.property  name="om"
	 */
	protected OntologyModel om;
	/**
	 * @uml.property   name="AttributeMap"
	 */
	protected HashMap<String, ArrayList<CIC>> attributeMap;
	/**
	 * @uml.property   name="RelationMap"
	 */
	protected HashMap<String, ArrayList<CIC>> relationMap;

	/** 
	 * Prefix is to store the prefix abbreviations 
	 */
	HashMap<String, String>  prefix;

	/**
	 * @uml.property  name="wExtensionalSim"
	 */
	protected double wExtensionalSim;
	/**
	 * @uml.property  name="wExternalSim"
	 */
	protected double wExternalSim;

	/**
	 * Getter of the property <tt>om</tt>
	 * @return  Returns the om.
	 * @uml.property  name="om"
	 */

	/**
	 * Setter of the property <tt>om</tt>
	 * @param om  The om to set.
	 * @uml.property  name="om"
	 */

	/**
	 *  It initializes a context layer starting from a context path, referring to a context, which can be expressed in JSON or a in-house format setting JSONContext respectively as true or false 
	 */
	public ContextLayer(String contextPath, boolean JSONContext, double wExtensional, double  wExternal, OntologyModel omp){
		//this.similarityTool=similarityTool;
		this.relationMap =new HashMap <String,ArrayList<CIC>>();
		this.attributeMap= new HashMap<String,ArrayList<CIC>>();
		prefix  = new HashMap<String, String>();
		this.wExtensionalSim=wExtensional;
		this.wExternalSim=wExternal;
		if (JSONContext) this.loadContextJSON(contextPath);
		else this.loadContext(contextPath);
		this.om=omp;
	}

	/**
	 * Given a Path that has not a correspondent rage in the context, it check if there is a superclass which has a  well defined context, 
	 * in this  case it adds to the proper data structure the association and returns true, otherwise it returns false.  
	 * @param a Path that has not a correspondent rage in the context
	 * @return true in this  case it adds a context tied to the path starting with path 
	 * @throws Exception 
	 */
	protected boolean addInheritaceToContext(String path) throws Exception{
		ArrayList <String>sc; //list of superclasses
		String scString=new String("");
		boolean att=false,
		rel=false;
		// Check if the path is made of only a class
		if (!path.contains(".")){
			// if it is then look for the superclass
			sc=om.getSuperClasses(path);
			// until it finds out the first definition of a context ( relation or attribute) associate to a superclass of path, or it has checked all the context
			for(Iterator <String>i=sc.iterator(); (i.hasNext() && !att && !rel); ){
				//		Check if there is a superclass x having a context well defined
				scString= i.next();
				att=attributeMap.containsKey(scString);
				rel=relationMap.containsKey(scString);
			}
			//GC
			sc=null;


			//	then  starting by the path composed only by the superclass having a context welle defined, expand the definition of context adding the context staring with path 

			//If it is add the superclass context to the path
			if (att){
				//ask for all the keys
				Set<String> keysForAttributes=attributeMap.keySet();

				String  keys[]=new String[keysForAttributes.size()];
				keys =keysForAttributes.toArray(keys);

				for ( int i=0; i< keys.length; i++  ){
					if (Debug.printDebug) System.out.println( "path "+keys[i]+" " );
					if (keys[i].startsWith(scString)){
						//let us create a new path starting with path 
						String nextn= keys[i].replaceFirst(scString,path);	
						if (Debug.printDebug) System.out.println( "netxn "+nextn+" " );

						// if there is a context starting by Path then duplicate it for
						attributeMap.put(nextn, attributeMap.get(keys[i]));
					}

				}
				//GC
				keysForAttributes=null;
				keys=null;
			}
			if (rel){
				//ask for all the keys
				Set<String> keysForRelations=relationMap.keySet();
				
				String keys[]= new String [keysForRelations.size()];
				keys=  keysForRelations.toArray(keys);
				
				for ( int i=0; i< keys.length; i++  ){
					//next= (String) iAttributes.next();
					System.out.println( "path "+keys[i]+" " );
					if (keys[i].startsWith(scString)){
						//let us create a new path strating with path 
						String nextn= keys[i].replaceFirst(scString,path);	
						System.out.println( "netxn "+nextn+" " );

						// if there is a context starting by Path then dublicate it for
						relationMap.put(nextn, relationMap.get(keys[i]));
					}

				}
				//				GC
				keys=null;
				keysForRelations=null;
			}

			return (att || rel);
		} else return false;

	}



	public float getAttWeight(String att, String path) {

		ArrayList <CIC> list=this.attributeMap.get(path);
		for  (CIC e: list ){
			if (e.getSlot().equals(att)) return e.getWeight();
		}
		return 0;
	}



	public float getRelWeight(String rel, String path) {
		ArrayList <CIC> list=this.relationMap.get(path);
		for  (CIC e: list ){
			if (e.getSlot().equals(rel)) return e.getWeight();
		}
		return 0;
	}



	/** 
	 * Setter of the property <tt>wEstensional</tt>
	 * @param wEstensional  The estensional to set.
	 * @uml.property  name="wExtensionalSim"
	 */
	public void setWExtensionalSim(double extensionalSim) {
		wExtensionalSim = extensionalSim;
	}



	/** 
	 * Setter of the property <tt>wExternalSim</tt>
	 * @param wExternalSim  The externalSim to set.
	 * @uml.property  name="wExternalSim"
	 */
	public void setWExternalSim(double externalSim) {
		wExternalSim = externalSim;
	}



	/**
	 * It loads the Context  from file whose path is specified in uri 
	 */
	public void loadContext(String uri) {
		System.out.println(uri);

		// Reading input by lines:
		String s,part[], s2= new String("");
		// prefix is the  map where we can insert the prefix declared in the Context

		try {
			BufferedReader in = new BufferedReader(new FileReader(uri));
			ArrayList <String> preUri=new ArrayList <String>();
			// we add the prefix http resolved as http: , Otherwise when we write a context without any PREFIX, it mistaken http: as a prefix definition and replace http://xxx.yyy// with null://xxx.yyy//  
			prefix.put("http","http:");
			// it reads the file and add the Prefixes
			while ((s = in.readLine()) != null)
				if (s.length()!=0) 
					if (s.startsWith("PREFIX")){
						s=s.replaceFirst("PREFIX","");

						part=s.split(": ");
						part[1]=part[1].replace("<","").replace(">","");
						preUri.add(part[1]);
						prefix.put(part[0].trim(),part[1].trim());			
					}

					else {
						s2 += s + "\n";
					}
			in.close();
			System.out.println(s2);
			// lets load all the schema defined as context prefix 
			om.loadURIs(preUri);			
		}catch (FileNotFoundException e){
			System.out.println("The context file you have indicated can't be found!!  "+e.getMessage());
			System.exit(0);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		};


		// Rows contain all the rows of the context except for the line declaring the prefix 		
		String[] rows = s2.split("\\}\\}\n");
		String par = new String("}}");
		for (int i = 0; i < rows.length; i++) {
			// let's add the }} that have been removed by splitting
			rows[i] += par;
			// let's remove any black, carriage returns and so on..
			rows[i] = rows[i].replaceAll("\\s", "");
		};
		if (Debug.printDebugSparqlOntologyModel){
			System.out.println("Rows");
			for (int i = 0; i < rows.length; System.out.println(rows[i++]));
		}
		// Now each row is a different element of the function
		// let us retrive the different
		String[] recPath = new String[rows.length];
		Matcher[] attributes = new Matcher[rows.length];
		Matcher[] relations = new Matcher[rows.length];

		/*
		 * Pattern pRecPath = Pattern.compile("\\w+(,\\w+)*+"); Matcher recPath =
		 * pRecPath.matcher(rows[i]); while(recPath.find())
		 * System.out.println(recPath.group());
		 */
		Pattern pRecPath = Pattern.compile("\\w+:\\w+(,\\w+:\\w+)*+");
		//Pattern pAttrRel = Pattern.compile("\\(\\w+:\\w+,\\w+\\)");
		// I have modified the pattern to include also _- and all what can occurs in the rdf
		Pattern pAttrRel = Pattern.compile("\\([^\\)\\(\\[\\]\\<\\>]+:[^\\)\\(\\[\\]\\<\\>]+,\\w+\\)");
		//Pattern pAttrRelW = Pattern.compile("\\(\\w+:\\w+,\\w+,\\d+\\)");
		Pattern pAttrRelW = Pattern.compile("\\([^\\)\\(\\[\\]\\<\\>]+:[^\\)\\(\\[\\]\\<\\>]+\\w+,\\d+\\)");
		// Pattern pRelPath =
		// Pattern.compile("[\\w+,\\w+]+[\\w+,\\w+,[0-100]]");

		// Local string where to store part of the string processing
		String ls, ls1;
		int idx;
		for (int i = 0; i < rows.length; i++) {
			// String[] acc=rows[i].split("->");
			// find out the recursion path 
			try {
				Matcher m = pRecPath.matcher(rows[i]);
				if (m.find()) {
					// we have to translate the name space properly
					ls=m.group();
					idx=ls.indexOf(":");
					ls1= ls.substring(0,idx);
					recPath[i] = prefix.get(ls1).replace(" ","")+ls.substring(idx+1).replace("\\s","").replace(" ","");// (ls1,prefix.get(ls1));
					System.out.println();
					System.out.println(recPath[i]);
				}
			} catch (Exception e) {
				System.out.println("Wrong recursion path il context line " + i
						+ " " + e.getMessage());
			}
			// let remove the part concerning the recursion path
			String[] att = rows[i].split("->");
			// the interesting part is the second which will contain both attribute and relations 
			if (att != null) {
				att = att[1].split("\\}\\,\\{");
				float accWeight = 0;// it accumulates the weights that are
				// present
				ArrayList<CIC> el21 = new ArrayList<CIC>();
				ArrayList<CIC> elAtt2 = new ArrayList<CIC>();

				try {
					// first let take tose which have a weight
					attributes[i] = pAttrRelW.matcher(att[0]);
					if (Debug.printDebugSparqlOntologyModel) System.out.println("Attribute with weight");
					while (attributes[i].find()) {
						// it returns something like "Att,op,weight"
						String[] attrOpWe = attributes[i].group().replace("(","").replace(")", "").split(",");
						for (int ii = 0; ii < attrOpWe.length; System.out.println(attrOpWe[ii++]));
						// the namespace has to be made explicit
						ls=attrOpWe[0];
						idx=ls.indexOf(":");
						ls1= ls.substring(0,idx);

						elAtt2.add(new CIC( prefix.get(ls1)+ls.substring(idx+1), attrOpWe[1], Float.parseFloat(attrOpWe[2])));
						if (Debug.printDebugSparqlOntologyModel) 	System.out.println( prefix.get(ls1)+ls.substring(idx+1)+ "," + attrOpWe[1]+ "," + Float.parseFloat(attrOpWe[2]));
						accWeight += (Float.parseFloat(attrOpWe[2]));
					}

				} catch (Exception e) {
					System.out.println("Wrong attributes in context line " + i+ " " + e.getMessage());
				}

				try {
					// first let take those which have a weight
					relations[i] = pAttrRelW.matcher(att[1]);
					if (Debug.printDebugSparqlOntologyModel) System.out.println("Relation with weight");
					while (relations[i].find()) {
						// it returns something like "Att,op,weight"
						String[] relOpWe = relations[i].group().replace("(", "").replace(")", "").split(",");
						for (int ii = 0; ii < relOpWe.length; System.out.println(relOpWe[ii++]));
						// the namespace has to be made explicit
						ls=relOpWe[0];
						idx=ls.indexOf(":");
						ls1= ls.substring(0,idx);

						el21.add(new CIC( prefix.get(ls1)+ls.substring(idx+1), relOpWe[1], Float.parseFloat(relOpWe[2])));
						if (Debug.printDebugSparqlOntologyModel)  System.out.println( prefix.get(ls1)+ls.substring(idx+1) + "," + relOpWe[1] + ","+ Float.parseFloat(relOpWe[2]));
						accWeight += (Float.parseFloat(relOpWe[2]));
					}

				} catch (Exception e) {
					System.out.println("Wrong relationss in context line " + i + " " + e.getMessage());
				}

				if (Debug.printDebugSparqlOntologyModel) System.out.println("Attribute without weight");
				attributes[i] = pAttrRel.matcher(att[0]);
				int num = 0;
				while (attributes[i].find()) {
					num++;
				}
				attributes[i].reset();

				relations[i] = pAttrRel.matcher(att[1]);
				if (Debug.printDebugSparqlOntologyModel) System.out.println("Relation without weight");
				while (relations[i].find()) {
					num++;
				}

				while (attributes[i].find()) {
					// it returns something like "Att,op"
					String[] attrOpWe = attributes[i].group().replace("(", "").replace(")", "").split(",");
					for (int ii = 0; ii < attrOpWe.length; System.out.println(attrOpWe[ii++]));
					//					 the namespace has to be made explicit
					ls=attrOpWe[0];
					idx=ls.indexOf(":");
					ls1= ls.substring(0,idx);


					elAtt2.add(new CIC(prefix.get(ls1)+ls.substring(idx+1), attrOpWe[1],
							(100f - accWeight) / num));
					if (Debug.printDebugSparqlOntologyModel)
						System.out.println(prefix.get(ls1)+ls.substring(idx+1) + "," + attrOpWe[1] + "," + (100f - accWeight) / num);

				}
				// let's substitute the namespace in the recPath, in order to have always the extensive version in the context
				Set <String> set= prefix.keySet();
				for(String j : set ){
					while (recPath[i].contains(j+":") && j!="http")
						recPath[i]=recPath[i].replace(j+":", prefix.get(j));
				}
				this.attributeMap.put(recPath[i].replace(",", "."), elAtt2);

				relations[i].reset();
				while (relations[i].find()) {
					// it returns something like "Att,op"
					String[] relOpWe = relations[i].group().replace("(", "").replace(")", "").split(",");
					for (int ii = 0; ii < relOpWe.length; System.out.println(relOpWe[ii++]));
					//					 the namespace has to be made explicit
					ls=relOpWe[0];
					idx=ls.indexOf(":");
					ls1= ls.substring(0,idx);

					el21.add(new CIC(prefix.get(ls1)+ls.substring(idx+1), relOpWe[1], (100f - accWeight)
							/ num));
					System.out.println(prefix.get(ls1)+ls.substring(idx+1) + "," + relOpWe[1] + ","
							+ (100f - accWeight) / num);
				}
				ls=recPath[i].replace(",", ".");
				// let's substitute the namespace in the recPath, in order to have always the extensive version in the context
				set= prefix.keySet();
				for(String j : set ){
					while (recPath[i].contains(j+":") && j!="http")
						recPath[i]=recPath[i].replace(j+":", prefix.get(j));
				}
				//recPath[i]=acc;
				//ls1= ls.substring(0,idx);

				this.relationMap.put(recPath[i].replace(",", "."), el21);

			}
		}
	}

	/**
	 * It loads a Context from file whose path is specified in uri.
	 * The context is expected to be encoded as it follows
	 * 
	 * 
	 * Context are made of two arrayes of JSON OBJECTS
	 * prefix which  contains all the prefix that should be loaded
	 * pathRecursion  contains object and data properties
	 * 
	 * weight are optional, if indicated they are between 0 and 100,  and correctelly distributed, namely their sum must be consistently equal to 100 % 


{
   "prefix":[
      {
         "qname":"skos",
         "namespace":"http://www.w3.org/2004/02/skos/core#"
      },
      {
         "qname":"owl",
         "namespace":"http://www.w3.org/2002/07/owl#"
      },
      {
         "qname":"dc",
         "namespace":"http://purl.org/dc/terms/"
      }
   ],
   "pathRecursion":[
      {
         "path":"owl:Thing",
         "dataProperties":[
            {
               "property":"dc:example",
               "operation":"Count"
            }
         ],
         "objectProperties":[
            {
               "property":"dc:subject",
               "operation":"Simil",
               "weight":"50"
            }
         ]
      },
      {
         "path":"owl:Thing,dc:subject",
         "objectProperties":[
            {
               "property":"skos:broader",
               "operation":"Inter"
            }
         ]
      }
   ]
}
	 * @param uri
	 */
	public void loadContextJSON(String uri) {
		System.out.println(uri);

		try {

			JSONTokener inputFile = new JSONTokener(new FileInputStream(uri));
			JSONObject context=new JSONObject(inputFile);

			//	BufferedReader in = new BufferedReader(new FileReader(uri));
			ArrayList <String> preUri=new ArrayList <String>();
			// we add the prefix http resolved as http: , Otherwise when we write a context without any PREFIX, it mistaken http: as a prefix definition and replace http://xxx.yyy// with null://xxx.yyy//  
			prefix.put("http","http:");

			try{
				JSONArray prefixes = context.getJSONArray("prefix");
				int max=prefixes.length();
				for (int i=0;i <max; i++){
					JSONObject o= prefixes.getJSONObject(i);
					try{
						String namespace = o.getString("namespace");
						preUri.add(namespace.trim());
						prefix.put(o.getString("qname").trim(),namespace.trim());
					} catch ( JSONException e){
						// problem with namespace or qname
						throw new Exception ("Malformed context: problem with namespace or qname");
					}
				}
			}catch(JSONException e){
				// if there are not prefix is not a problem

			}
			
			// lets load all the schema defined as context prefix 
			// om.loadURIs(preUri); that should not be required anymore .. 

			JSONArray pathRecursion= context.getJSONArray("pathRecursion");

			for (int i=0; i< pathRecursion.length(); i++ ){
				JSONObject recusionItem = pathRecursion.getJSONObject(i);

				ArrayList<CIC> dataP = new ArrayList<CIC> ();
				ArrayList<CIC> objectP = new ArrayList<CIC> ();
				float sum=0;
				int dataPropertieslength=0, objectPropertieslength=0;

				int nww= 0; // number of properties  with weight
				String ls = recusionItem.getString("path");
				int idxd = 0;
				int beginning=0;
				int idxc=0;

				String alreadyConvertedPath="";
				while (beginning<= ls.length()){
					ls=ls.substring(beginning);
					idxd=ls.indexOf(":");
					String qname = ls.substring(0,idxd);
					idxc=ls.indexOf(",");
					if (idxc<0) idxc= ls.length();

					alreadyConvertedPath=alreadyConvertedPath+","+ prefix.get(qname)+ls.substring(idxd+1,idxc);
					beginning=idxc+1;
				}
				String path=alreadyConvertedPath.substring(1);
				JSONArray dataProperties = recusionItem.optJSONArray("dataProperties");
				JSONArray objectProperties = recusionItem.optJSONArray("objectProperties");
				if (dataProperties== null && objectProperties== null) throw new Exception("Uncorrect context: data and object properties are not specified for recursive path "+ path +" !!");
				if (dataProperties!= null)  dataPropertieslength=dataProperties.length();
				if (objectProperties!= null)  objectPropertieslength=objectProperties.length();			 

				float [] weights = new float [dataPropertieslength+objectPropertieslength]; 

				if (dataProperties!=null) {
					for (int ii= 0; ii < dataPropertieslength; ii++){
						JSONObject el= dataProperties.getJSONObject(ii);
						weights[ii]=  (float) el.optDouble("weight", 0d);
						if (weights[ii]!=0)  nww++;
						sum+= weights[ii];
					}
				}

				// let check the sum of weight indicated both into data and object property and in the case weight are defined only partially let to figure out how to automatically asigne weight for those not specified
				for (int ii= 0; ii < objectPropertieslength; ii++){
					JSONObject el= objectProperties.getJSONObject(ii);
					weights[dataPropertieslength+ii]=  (float) el.optDouble("weight", 0d);
					if (weights[dataPropertieslength+ii]!=0)  nww++;
					sum+= weights[dataPropertieslength+ii];
				}

				int nnw=   (dataPropertieslength + objectPropertieslength) -nww; //number of properties wiTh no weight
				//			boolean redistribute= !((sum==100)&&(nww==0)); // if the sum if more that 100 or we have properties without weight we must reditribute the weight 

				// some weight have been implicitly specified
				if (sum>=100 && nnw!=0)  throw new Exception("The weights in the context are not correctly specificed  their total sum is "+sum +" and "+ nnw +" properties do not have weights"  );
				if ((sum<100)){ 
					if (nnw!=0){
						// we should equally redistribuite the part of weight which  has not been explicity assigned 
						int numberOfShareAssigned=0;
						float shareToBeAssigned= (100-sum)/ nnw;
						for (int i1=0;i1< dataPropertieslength + objectPropertieslength; i1++){
							if (weights[i1]==0) {
								if (numberOfShareAssigned<nnw-1) {
									weights[i1]=shareToBeAssigned;
									numberOfShareAssigned++;
								} else weights[i1]= 100-sum-(shareToBeAssigned*(numberOfShareAssigned));
							}
						}
					}
				} else {
					float sumNew=0;
					// lets normalize to 100 
					for (int i1=0;i1< dataPropertieslength + objectPropertieslength-1; i1++){
						weights[i1]= weights[i1]/sum;
						sumNew=weights[i1];
					}
					weights[dataPropertieslength+ objectPropertieslength-1]= 100- sumNew;
				}




				// then let's redistribuite while we are reading other context info 
				for (int ii= 0; ii < dataPropertieslength; ii++){
					JSONObject el= dataProperties.getJSONObject(ii);
					//					 the namespace has to be made explicit
					ls=el.getString("property");
					int idx = ls.indexOf(":");
					String ls1 = ls.substring(0,idx);
					dataP.add(new CIC (prefix.get(ls1)+ls.substring(idx+1), el.getString("operation"), weights[ii]));
				}

				for (int ii= 0; ii < objectPropertieslength; ii++){
					JSONObject el= objectProperties.getJSONObject(ii);
					ls=el.getString("property");
					int idx = ls.indexOf(":");
					String ls1 = ls.substring(0,idx);
					objectP.add(new CIC (prefix.get(ls1)+ls.substring(idx+1), el.getString("operation"), weights[ii+dataPropertieslength]));
				}

				this.attributeMap.put(path.replace(",", "."), dataP);
				this.relationMap.put(path.replace(",", "."), objectP);




			}


		}catch (FileNotFoundException e){
			System.out.println("The context file you have indicated can't be found!!  "+e.getMessage());
			System.exit(0);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		};
	}

	/** 
	 * Getter of the property <tt>wExternalSim</tt>
	 * @return  Returns the externalSim.
	 * @uml.property  name="wExternalSim"
	 */
	public double getWExternalSim() {
		return wExternalSim;
	}

	/** 
	 * Getter of the property <tt>wEstensional</tt>
	 * @return  Returns the estensional.
	 * @uml.property  name="wExtensionalSim"
	 */
	public double getWExtensionalSim() {
		return wExtensionalSim;
	}






}



