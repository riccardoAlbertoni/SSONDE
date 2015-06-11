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

import com.hp.hpl.jena.query.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
//import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
//import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
//import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
//import de.fuberlin.wiwiss.ng4j.semwebclient.SemanticWebClient;
//import de.fuberlin.wiwiss.ng4j.sparql.NamedGraphDataset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
//import java.util.Collection;
import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Map;
import java.util.Set;


//package org.sindice.samples;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
//import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;


public class OntologyModelSparqlSindiceCache implements OntologyModel {
	private static final String SEARCH_LINK = "http://sindice.com/vocab/search#link";
	//SemanticWebClient semweb;
	Model	model;	
	InfModel infModel;
	ContextLayer  context;
	//static String maxsteps="4";
//this is a cache to avoid to dereference the same uri to many times
	private  Hashtable<String,Object> cachedURI;
	
private final String baseURL = "http://sindice.com/api/v2/search?qt=term&q=";
	  private final String baseCacheURL= "http://api.sindice.com/v2/cache?url=";
// added for accessing sindice
	  private String getURL(String query) {
	    try {
	      return baseURL + URLEncoder.encode(query, "utf-8");
	    } catch (UnsupportedEncodingException e) {
	      throw new RuntimeException(e);
	    }
	  }
	  private String getCachedURL(String query) {
		    try {
		      
		      return baseCacheURL + URLEncoder.encode(query, "utf-8") + "&implicit=1";
		    } catch (UnsupportedEncodingException e) {
		      throw new RuntimeException(e);
		    }
		  }

	// @Qname the model is useful in order to manage the QNames
	// NamedGraphModel model;

	/*
	 * it loads the graph at uri, extending them by sindice search
	 */
	  public OntologyModelSparqlSindiceCache(String uri/*, ContextLayer context*/) {

		//  String query = "* <http://xmlns.com/foaf/0.1/workplaceHomepage> <http://www.deri.ie/> AND  " + "* <http://xmlns.com/foaf/0.1/knows> *";
		//String url = getURL(query);
		  String url= getURL(uri);
		  this.cachedURI=new Hashtable<String,Object>();
		  model = ModelFactory.createDefaultModel();
		  
		  model.read(url);
		  //cachedURI.put(url, null);
		  StmtIterator it = model.listStatements(null, model.createProperty(SEARCH_LINK), (Resource)null);

		  while (it.hasNext()) {
			  Statement stmt = it.nextStatement();

 			  Resource link = stmt.getResource();
 			  try{
				  String lnk=(link.getURI());
				  if (!this.cachedURI.containsKey(lnk)){
				  model.read(getCachedURL(lnk));
				  this.cachedURI.put(lnk, null);
				  if (Debug.printDebugSparqlOntologyModel)
					  System.out.println("The " + lnk + " has been retrieved from the sindice cache!");
				  }
			  }
			  catch (Exception e) {
				  // just warn it has not accessible anymore
				  System.out.println("File not found:"+ e.getMessage());

			  }
		  }
		  if (Debug.JENA_REASONING){
		  Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
		   // reasoner = reasoner.bindSchema(model);
		  infModel = ModelFactory.createInfModel(reasoner, model);
		  }
	  }




	/**
	 * It loads all the graphs that are available in the uris contained in the
	 * array
	 * @param URIs  list of URIs to be uploaded
	 */
	/**
	 * @param 
	 */
	  public OntologyModelSparqlSindiceCache(ArrayList<String> URIs /*, ContextLayer context*/) {
		  this.cachedURI=new Hashtable<String,Object>();
		  model = ModelFactory.createDefaultModel();
		// model.read("http://xmlns.com/foaf/0.1/maker");
		  for (String uri : URIs) {
			  if (Debug.SINDICE_EXPANSION_CACHE){
				  String url= getURL(uri);

				  model.read(url);
				  StmtIterator it = model.listStatements(null, model.createProperty(SEARCH_LINK), (Resource)null);

				  while (it.hasNext()) {
					  Statement stmt = it.nextStatement();

					  Resource link = stmt.getResource();
					  
					  try{
						  String lnk=(link.getURI());
						  if (!this.cachedURI.containsKey(lnk)){
						  model.read(getCachedURL(lnk));
						  this.cachedURI.put(lnk, null);
						  if (Debug.printDebugSparqlOntologyModel)
							  System.out.println("The " + lnk + " has been retrieved from the sindice cache!");
						  }			  
						  
					  }
					  catch (Exception e) {
						  // just warn it has not accessible anymore
						  System.out.println("File not found:"+ e.getMessage());

					  }
				  }
			  }
			  else if (Debug.SINDICE_CACHE){
				  String url= getCachedURL(uri);
				  try{
					  if (!this.cachedURI.containsKey(url)){
				  
					  model.read(url);
					  this.cachedURI.put(url, null);
					  }
				  }
				  catch (Exception e) {
					  // just warn it has not accessible anymore
					  System.out.println("File not found:"+ e.getMessage());

				  }
			  } 
			  else {
				  try{
					  
					  if (!this.cachedURI.containsKey(uri)){
						  model.read(  URLEncoder.encode(uri, "utf-8"));
						  this.cachedURI.put(uri, null);
					  }
					  
					  if (Debug.printDebugSparqlOntologyModel)
						  System.out.println("The " + URLEncoder.encode(uri, "utf-8") + " has been retrieved!");
				  }
				  catch (Exception e) {
					  // just warn it has not accessible anymore
					  System.out.println("File not found:"+ e.getMessage());

				  }

			  }

		  }
		  if (Debug.JENA_REASONING){
		  Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
		   // reasoner = reasoner.bindSchema(model);
		  infModel = ModelFactory.createInfModel(reasoner, model);
		  }
	  }
		
	

	/**
	 * It loads all the graphs that are available in the uris contained in the
	 * array
	 * @param URIs list of URIs to be uploaded
	 */
	  public void loadURIs(ArrayList<String> URIs) {
		  //super();
		  // load the graph
		  for (String uri : URIs) {
			  if(Debug.SINDICE_EXPANSION_CACHE){
				  String url= getURL(uri);
				  //model = ModelFactory.createDefaultModel();
				  model.read(url);
				  StmtIterator it = model.listStatements(null, model.createProperty(SEARCH_LINK), (Resource)null);

				  while (it.hasNext()) {
					  Statement stmt = it.nextStatement();

					  Resource link = stmt.getResource();
					  // added by riccardo, some time the URL returned by sindice are not available anymore 
					  // I should probably  read the Sindice cache, but let first to skip those RDF files  that are not available anymore  
					  try{
						  String lnk=(link.getURI());
						  if (!this.cachedURI.containsKey(lnk)){
						  model.read(getCachedURL(lnk));
						  this.cachedURI.put(lnk, null);
						  
						 // model.read(getCachedURL(link.getURI()));
						  if (Debug.printDebugSparqlOntologyModel)
							  System.out.println("The " + lnk + " has been retrieved by sindice cache!");
						  }
						  }
					  catch (Exception e) {
						  // just warn it has not accessible anymore
						  System.out.println("File not found:"+ e.getMessage());

					  }
				  }
			  }
			  else if (Debug.SINDICE_CACHE){
				  String url= getCachedURL(uri);
				  try{
					  model.read(url);
				  }
				  catch (Exception e) {
					  // just warn it has not accessible anymore
					  System.out.println("File not found:"+ e.getMessage());

				  }
			  } 
			  else {
				  try{
					  model.read(  uri);
					  if (Debug.printDebugSparqlOntologyModel)
						  System.out.println("The " + /*URLEncoder.encode(*/uri/*, "utf-8")*/ + " has been retrieved!");
				  }
				  catch (Exception e) {
					  // just warn it has not accessible anymore
					  System.out.println("File not found:"+ e.getMessage());

				  }

			  }
			  if (Debug.JENA_REASONING){
			  Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
			   // reasoner = reasoner.bindSchema(model);
			   infModel = ModelFactory.createInfModel(reasoner, model);
			   try{
					  this.SerializeTheModel(infModel);
				  }
			
				  catch (Exception e){
					  System.out.println(e.getMessage());
				  }
			  }else{
			  try{
				  this.SerializeTheModel(model);
			  }
		
			  catch (Exception e){
				  System.out.println(e.getMessage());
			  }
			  }
		  }
	  }
			  


			/*  
			if (uri.startsWith("http://") || uri.startsWith("file://")) {
				// SW graph.read(uri, "RDF/XML");
				this.semweb.read(uri, "RDF/XML");
				// SW model=graph.asJenaModel(uri);
				// SW if (Debug.printDebugSparqlOntologyModel){
				// SW Map prefix=model.getNsPrefixMap();
				// SW System.out.println(" Loading "+ uri+" the following
				// prefixes have been set");
				// SW System.out.println(prefix.toString());
				// SW }
			} else {
				// TODO cosa succede se l'uri inizia per qualcosa di diverso da
				// http o file
				// we have to translate the Qname into a URI
				// @Qname
				// SW int commaidx=uri.indexOf(":");
				// SW String
				// prefix=model.getNsPrefixURI(uri.substring(commaidx));
				// SW if (Debug.printDebugSparqlOntologyModel){
				// SW System.out.println(" The prefix "+uri +"has uri " +prefix+
				// uri.subSequence(commaidx,uri.length()));
				// SW }
				// SW graph.read(prefix+uri, "RDF/XML");

			}

			if (Debug.printDebugSparqlOntologyModel)
				System.out.println("The " + uri + " has been retrieved!");
		}
			 */


	/**
	 * It prints all ontology classes and the number of instances which are
	 * available.
	 */
	public void printAllClasses() {
		// TODO Auto-generated method stub

	}

	/**
	 * It returns all the classes in the ontology.
	 */
	public Set<String> getAllClasses() {
		// TODO Auto-generated method stub
		// //sparql query su protege 3.3
		String q = this.Prefix + "SELECT ?subject ?object "
		+ "WHERE {{ ?subject a rdfs:Class.} "
		+ "UNION {?subject a owl:Class.} "
		+ "FILTER (!isBlank(?subject))}";
		// }
		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();
		// let prepare the results
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql.toString());
		}

		HashSet<String> r = new HashSet<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode subject = result.get("subject");
			r.add(subject.toString());
		}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getAllClasses returns");
			for (String e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
		qe.close();
		return r;
	}

	/* (non-Javadoc)
	 * @see similarity.OntologyModel#getAllAttributesForClass(java.lang.String)
	 */
	public ArrayList<String> getAllAttributesForClass(String c) {

		// //sparql query su protege 3.3 and ngj but it return only the
		// attribute that are defined for one class
		//  the case ODBASENAMED the attribute title cannot be retrieved
		// because it is defined as ��
		// rdfs:domain owl:unionOf (:Publication :Project);

		String q, c1;
		ArrayList <ArrayList<String>> upwc = this.getUpwardCotocopy(c);
		ArrayList<String> r = new ArrayList<String>();
		ArrayList <String> classBuffer=new ArrayList <String>();


		for (ArrayList<String> l : upwc) {
			for (String e: l) {
				if (!classBuffer.contains(e)) {
					classBuffer.add(e);
					c1 = e;

					// the next query takes the attributes checking also for attributes that are defined 
					// for more than one domain. it works till attributes that have four domain 
					q = this.Prefix
					+ "SELECT ?x WHERE {"
					+ "{?x a owl:DatatypeProperty; rdfs:domain <"
					+ c1
					+ ">.} "
					+ "UNION {?x a owl:DatatypeProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ). } "
					+ "UNION {?x a owl:DatatypeProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ?h).} "
					+ "UNION {?x a owl:DatatypeProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ?h ?p).} "
					+ "UNION {?x a owl:DatatypeProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1 + "> ?z ?h ?p ?o).}}";

					// the we can have a cycle that retrieve all the super class of x

					Query sparql = QueryFactory.create(q);
					// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
					// NamedGraphDataset(graph));
					QueryExecution qe;
					if (Debug.JENA_REASONING){
					  qe = QueryExecutionFactory.create(sparql, infModel);
					}  else qe = QueryExecutionFactory.create(sparql, model);
					ResultSet results = qe.execSelect();
					if (Debug.printDebugSparqlqueryInTheOntologyModel) {
						System.out.println(sparql);
					}

					while (results.hasNext()) {
						QuerySolution result = results.nextSolution();
						// RDFNode graph = result.get("graph");
						RDFNode x = result.get("x");
						String xx=x.toString();
						// this if is to avoid that two relation are considered twice
						if (!r.contains(xx))r.add(xx);
					}
				qe.close();
				}
			}
		}

		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getAttributeForClass");
			for (String e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
		
		return r;

	}

	/**
	 * It returns all the relations for a class as collection of string
	 */
	public ArrayList<String> getAllRelationsForClass(String c) {
		// TODO Auto-generated method stub
		// PREFIX owl: <http://www.w3.org/2002/07/owl#>
		// PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		// PREFIX :
		// <http://www.ge.imati.cnr.it/ima/personal/albertoni/ODBASE06RIRNAmed.owl#>
		// SELECT ?x
		// WHERE {
		// {?x a owl:ObjectProperty; rdfs:domain :Project.}
		// UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf
		// (:Project ?z ). }
		// UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf
		// (:Project ?z ?h).}
		// UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf
		// (:Project ?z ?h ?p).}
		// UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf
		// (:Project ?z ?h ?p ?o).}

		String q, c1;
		ArrayList<String> r = new ArrayList<String>();
		ArrayList <ArrayList<String>> uwcp = this.getUpwardCotocopy(c);
		ArrayList<String> classBuffer =new ArrayList<String>();

		for(ArrayList <String> l:uwcp)
			for (String e : l) {
				if (!classBuffer.contains(e)){
					classBuffer.add(e);	

					c1 = e;
					q = this.Prefix
					+ " SELECT ?x "
					+ "WHERE {"
					+ "{?x a owl:ObjectProperty; rdfs:domain <"
					+ c
					+ ">.}"
					+ "UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ). } "
					+ "UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ?h).} "
					+ "UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1
					+ "> ?z ?h ?p).} "
					+ "UNION {?x a owl:ObjectProperty; rdfs:domain ?y. ?y owl:unionOf (<"
					+ c1 + "> ?z ?h ?p ?o).}}"; 

					// the we can have a cycle that retrieve all the super class of x

					Query sparql = QueryFactory.create(q);
					// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
					// NamedGraphDataset(graph));
					QueryExecution qe;
					if (Debug.JENA_REASONING){
					  qe = QueryExecutionFactory.create(sparql, infModel);
					}  else  qe = QueryExecutionFactory.create(sparql, model);
					ResultSet results = qe.execSelect();
					if (Debug.printDebugSparqlqueryInTheOntologyModel) {
						System.out.println(sparql);
						//ResultSetFormatter.out(System.out, results);
					}

					while (results.hasNext()) {
						QuerySolution result = results.nextSolution();
						// RDFNode graph = result.get("graph");
						RDFNode x = result.get("x");
						r.add(x.toString());
						if (Debug.printDebugSparqlqueryInTheOntologyModel) {
							System.out.println(x.toString());
						
						}
					}
					qe.close();
				}
				// The sparql engine does not reason neither it support the Formal
				// IS-A
				// Then we have to manually retrieve all the relation/attribute that
				// have been defined for the superclass

				// let us retrieve all the super-classes
			
			}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getClassesForInstance");
			for (String e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
		
		
		return r;

	}

	/**
	 * @throws Exception
	 */
	public String lub(String first, String second) throws Exception {
		// TODO what if the strings do not corresponding to class in the ontology
		// ONTOMODEL what if the lub is not unique... ???
		// NOLUBVERSION this method has to be re-thought
		return "owl:Thing";
		/*
		String r="";
		ArrayList<String> up;
		ArrayList<String> fsc;
		ArrayList<String> ssc;
		ArrayList <ArrayList<String>> listFirst, listSecond;
		Iterator <ArrayList<String>> is,ifi;
		if (first == null || second == null)
			throw new Exception("the parameter of a lub are not classes");
		// are first and second equal?
		if (first.equals(second)) {
			r = first;
		}
		// is First superClass of second?
		else {
			//TODO This code can be make faster relying on a local copy of classes-superclasses  
			// getUpwardcotocopy it should be limited to the branch which have some relation to the path of recursion defined in the context

			listSecond=this.getUpwardCotocopy(second);
			is=listSecond.iterator();
			boolean notfound=true;
			while ( is.hasNext()&& notfound ){
				up = is.next();
				ssc = up;
				if (up.contains(first)) {
					notfound=false;
					r = first;
				}
			}
		
			// or is Second SuperClass Of first?
			if (notfound ){

				listFirst=this.getUpwardCotocopy(first);
				ifi=listFirst.iterator();
				while ( ifi.hasNext()&& notfound ){
					up = ifi.next();
					fsc = up;
					if (up.contains(second)) {
						notfound=true;
						r = second;
					} 
				}
			}
			
				// first and second are not in is-a relationships 	
				//TODO -- just for the moment it returns owl:thing
			if (notfound) { 

					r="owl:thing";

				}

			}
				// let us find out the common superclass
				// the super class can belong also to another terminology

				
				    String fc, sc;
					ArrayList <String> l = new ArrayList<String>();

					// we should limit the search to classes that have a is-a relation with both 
					// some consideration that can help
					// first remark:the lub is one of the classes that are contained in the brances for first and for second
					// second remark: when we have more that one we should take the minimum
					// third remark: it is one of the classes for which the context is defined ( not necessarly for the external similarity but let us suppose it is true )

					// lets have the list of  classes in the branches of the first

					//in fsc there are the list of all the classes contained in the branches of first 




					fsc= new ArrayList<String>();
					for (ifi=listFirst.iterator(); ifi.hasNext();){
						ArrayList <String> branchFirst=ifi.next();
						for ( String e: branchFirst){
							if (! fsc.contains(e)) fsc.add(e);
						}
					}

					// that implements first remark
					// pay attention only the first common classes for each Branch should be returned
					for (Iterator<String> i = fsc.iterator(); i.hasNext();) {
						fc = i.next();
						for (Iterator<String> ii = ssc.iterator(); ii.hasNext();) {
							sc = ii.next();
							if (sc.contains(fc)) {
								// then we get the lub
								l.add(fc);
							}
						}
					}

					//That implement the third remark

					// that implements the second remark
					if (l.size()>1){
						//TODO lets select which is the closer

					}


				// end just for the moment it returns Owl:thing
					//r = l;

				
		
			
			if (Debug.printDebugSparqlOntologyModel) {
				System.out.println("the lub between " + first + " and " + second
						+ " is " + r);
				System.out.println();
			}
			return r;
*/		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see similarity.FirstOntologyModel#getUpwardCotocopy(java.lang.String) it
	 *      returns the UpwardsCoptocopy of the class c which includes c as the
	 *      first element and owl:Thing as then last
	 */
	/*public ArrayList<String> getUpwardCotocopyOLD(String c) {
		String q = this.Prefix + "SELECT ?x WHERE {{ <" + c
				+ ">  rdfs:subClassOf ?x}	}";
		// the we can have a cycle that retrieve all the super class of x

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe = QueryExecutionFactory.create(sparql, semweb
				.asJenaModel("default")); 
		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);

		}

		ArrayList<String> r = new ArrayList<String>();
		r.add(c); // the UpwardCotocopy contains the class on which it is
					// calculated

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode x = result.get("x");
			r.add(x.toString());
			q = this.Prefix + "SELECT ?x WHERE {{ <" + x.toString()
					+ ">  rdfs:subClassOf ?x}	}";
			// the we can have a cicle that retrieve alla the supre class of x
			sparql = QueryFactory.create(q);
			// SW qe = QueryExecutionFactory.create(sparql, new
			// NamedGraphDataset(graph));
			qe = QueryExecutionFactory.create(sparql, semweb
					.asJenaModel("default"));

			 results= qe.execSelect();
		}
		r.add("owl:Thing");
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getUpwardCotocopy " + c + " :");
			for (String e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
		return r;
	}*/
	public  ArrayList<ArrayList<String>> getUpwardCotocopy(String c) {
// NOLUBVERSION  This method has to be re-though, now it returns [[owl:Thing]] 
	ArrayList <ArrayList<String>> ll= new ArrayList <ArrayList<String>>();
	ArrayList  <String> l=new ArrayList <String>();
	l.add("owl:Thing");
	ll.add(l);
	
 	return ll;
		/*
		// lets define 
		String q = this.Prefix + "SELECT ?x WHERE {{ <" + c
		+ ">  rdfs:subClassOf ?x}	}";

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe = QueryExecutionFactory.create(sparql, semweb
				.asJenaModel("default")); 
		//here we are retrieving all the c super classes
		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);

		}

		ArrayList<ArrayList<String>> r = new ArrayList<ArrayList<String>>();

		if (results.getRowNumber()==0){ 
			ArrayList<String> e= new ArrayList<String>();
			e.add(c);
			e.add("owl:Thing");
			r.add(e);
		}
		else{
			while (results.hasNext()){
				//let add 
				//lets create the first level of solutions
				// It is possible that there are more than one class 
				QuerySolution result = results.nextSolution();
				// RDFNode graph = result.get("graph");
				RDFNode x = result.get("x");
				ArrayList<ArrayList<String>> a=this.getUpwardCotocopy(x.toString());
				for (ArrayList<String> l :a ){
					ArrayList<String> e= new ArrayList<String>();
					e.add(c);
					e.addAll(l);
					r.add(e);

				}
			}
		}


		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getUpwardCotocopy " + c + " :");
			for (ArrayList<String> l : r) {
				System.out.println("branch");
				for(String e :l)
					System.out.print(e + " ");
			}
			System.out.println();
		}
		return r;
*/	}
	public ArrayList<String> getClassesForInstance(String instance)
	throws Exception {

		String q = this.Prefix + "SELECT ?x " + "WHERE {" + " <" + instance
		+ "> a ?x	}";
		// the we can have a cycle that retrieve all the super class of x

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else qe = QueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();

		// if result is empty it means that no classes have been identified,
		// that could be a consequence of wrong redirection,
		// for example we had to load the URI indicated by rdfs:seeAlso
		// or maybe something else goes wrong

		ArrayList<String> r = new ArrayList<String>();

		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode x = result.get("x");
			r.add(x.toString());
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(x.toString());
			}

		}




		// PREFIX owl: <http://www.w3.org/2002/07/owl#>
		// PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		// PREFIX :
		// <http://www.ge.imati.cnr.it/ima/personal/albertoni/ODBASE06RIRNAmed.owl#>
		// SELECT ?x
		// WHERE {
		// {:AntonellaGalizia a ?x}
		// }
		// the we can have a cycle that retrieve all the super class of x
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getClassesForInstance " + instance + ":");
			for (String e : r) {
				System.out.print(e + " ");
			}
			System.out.println();
		}
		qe.close();
		return r;
	}

	public Set getIntancesForClass(String c) {

		// TODO Auto-generated method stub
		// in thi way it returns only the direct instances
		// PREFIX owl: <http://www.w3.org/2002/07/owl#>
		// PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		// PREFIX :
		// <http://www.ge.imati.cnr.it/ima/personal/albertoni/ODBASE06RIRNAmed.owl#>
		// SELECT ?x
		// WHERE {
		// {?x a :Researcher}
		// }

		return null;
	}

	public String getAttributeType(String attribute) throws Exception {
		String q = this.Prefix + "SELECT ?object WHERE { <" + attribute
		+ "> rdfs:range ?object }";
		// the we can have a cicle that retrieve alla the supre class of x

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else   qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		String r = "";
		
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode x = result.get("x");
			r = x.toString();
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(r);
			}

		}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getClassesForInstance: attribute " + attribute
					+ ": " + r);
			System.out.println();
		}
		qe.close();
		return r;

	}

	public String getRelationType(String relation)
	throws IncompatibleOntologyModel, Exception {
		String q = this.Prefix + "SELECT ?object WHERE { <" + relation
		+ "> rdfs:range ?object }";
		// the we can have a cycle that retrieve all the super classes of x

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		String r = "";
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode x = result.get("x");
			r = x.toString();
			if (Debug.printDebugSparqlOntologyModel) {
				System.out.println("getRelationType returns");
				System.out.print(x + " ");
			}
		}
		qe.close();
		return r;

	}

	public ArrayList<String> getAttributeValue(String instance, String attribute)
	throws Exception {
		String q = this.Prefix + "SELECT ?x WHERE { <" + instance + ">  <"
		+ attribute + "> ?x }";

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		
		
			System.out.println();
		// let prepare the results
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		ArrayList<String> r = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode subject = result.get("x");
			r.add(subject.toString());
		}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getAttributeValue returns");
			for (String e : r) {
				System.out.println(e + " ");
			}
//			ResultSet resprint= qe.execSelect();
//			
//			 ResultSetFormatter.out(System.out, resprint);
			System.out.println();
		
		}
		qe.close();
		return r;
	}

// renauld	
//	public ArrayList<String> getRelationValues(String instance, String relation) {
//		String q = this.Prefix + "SELECT DISTINCT ?p ?o WHERE { " + "<"
//		+ instance + ">  ?p ?o . }";
//
//		// / SELECT ?inv ?x
//		// WHERE
//		// {
//		// ?inv <http://www.w3.org/2002/07/owl#inverseOf>
//		// <http://xmlns.com/foaf/0.1/made> .
//		// OPTIONAL {
//		// ?x ?inv <http://dblp.l3s.de/d2r/resource/authors/Riccardo_Albertoni>
//		// .
//		// }
//		// }
//		Query sparql = QueryFactory.create(q);
//		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
//		// NamedGraphDataset(graph));
//		QueryExecution qe = QueryExecutionFactory.create(sparql, model);
//
//		ResultSet results = qe.execSelect();
//		// let prepare the results
//		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
//			System.out.println(sparql);
//		}
//		
//		ArrayList<String> r = new ArrayList<String>();
//		while (results.hasNext()) {
//			QuerySolution result = results.nextSolution();
//			// RDFNode graph = result.get("graph");
//			RDFNode predicate = result.get("p");
//			RDFNode object = result.get("o");
//			r.add(instance + " " + predicate + " " + object);
//		}
//		if (Debug.printDebugSparqlOntologyModel) {
//			System.out.println("getrelationValue returns");
//			for (String e : r) {
//				System.out.print(e + " ");
//			}
//			System.out.println();
//		}
//		if (Debug.printDebugSparqlOntologyModel) {
//			System.out.println("getrelationValue returns");
//			ResultSetFormatter.out(System.out, results);
//		}
//		qe.close();
//		return r;
//	}
	
	public ArrayList<String> getRelationValues(String instance, String relation) {
		String q = this.Prefix + "SELECT DISTINCT ?x WHERE { " + "{<"
		+ instance + ">  <" + relation + "> ?x .}" +
		// query to find the inverse relation
		//" UNION {?inv  <http://www.w3.org/2002/07/owl#inverseOf> <"
		//+ relation + ">. " + "?x ?inv <" + instance + ">.} " + 
		"}";

		// / SELECT ?inv ?x
		// WHERE
		// {
		// ?inv <http://www.w3.org/2002/07/owl#inverseOf>
		// <http://xmlns.com/foaf/0.1/made> .
		// OPTIONAL {
		// ?x ?inv <http://dblp.l3s.de/d2r/resource/authors/Riccardo_Albertoni>
		// .
		// }
		// }
		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		// let prepare the results
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}
		
		ArrayList<String> r = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode subject = result.get("x");
			r.add(subject.toString());
		}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getrelationValue returns");
			for (String e : r) {
				System.out.println(e + " ");
			}
			System.out.println();
		}
//		if (Debug.printDebugSparqlOntologyModel) {
//			System.out.println("getrelationValue returns");
//			ResultSet resprint= qe.execSelect();
//			
//			 ResultSetFormatter.out(System.out, resprint);
//			
//		}
		qe.close();
		
		/// let clean r from duplicated instance with respect to owl:SameAs
	   r= cleanDuplicates(r);
		return r;
	}

	private ArrayList<String> cleanDuplicates(ArrayList<String> r) {
	// TODO Auto-generated method stub
	int i=0;
	boolean goOn=true;
	while(goOn && (r.size()>0)) {
		
	String x=	r.get(i);
		
		String q = this.Prefix + "SELECT DISTINCT ?x WHERE { " + "{<"
		+ x + ">  <http://www.w3.org/2002/07/owl#sameAs> ?x .}" +
		// query to find the inverse relation
		//" UNION {?inv  <http://www.w3.org/2002/07/owl#inverseOf> <"
		//+ relation + ">. " + "?x ?inv <" + instance + ">.} " + 
		"}";
		
	//Iterator <String> i=	r.iterator();
		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		// let prepare the results
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}
		
		//ArrayList<String> dup = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode subject = result.get("x");
			
			r.remove(subject.toString());
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(" I am removing sparql"+ subject.toString());
			}
		}
		if (i< (r.size()-1)) i++;
		else goOn=false;
	}
		
	return r;
}
	/**
	 * Given two classes it works out their distance with repect to the is-a
	 * Hierarchy.
	 */
	/**
	 * Considering two classes in an ontology it works out the distance between
	 * them according to the is-A Hierarchy
	 */
	public double classDistance(String firstClass, String secondClass) {
		//NOLUBVERSION this method has to be revised
		return 1;
		/*// the classes are the same so no distance at all
		float d;

		if (firstClass.equals(secondClass))
			return 0;
		else {
			// OWLNamedClass f= owlModel.getOWLNamedClass(firstClass);
			// OWLNamedClass s= owlModel.getOWLNamedClass(secondClass);

			ArrayList<String> superClassesOfFirst = this.getUpwardCotocopy(firstClass), 
			superClassesOfSecond = this.getUpwardCotocopy(secondClass);

			// if second is super class of first or viceversa the distance is
			// the number of edge separating the two classes
			int i = superClassesOfFirst.indexOf(secondClass);
			int ii = superClassesOfSecond.indexOf(firstClass);
			int v = Math.max(i, ii);
			if (v > 0)
				return v;
			// if second and first are not super or subclasses of each other
			// then the distance is the dustancta among them and thing
			else {
				d = superClassesOfSecond.size() + superClassesOfFirst.size()
				- 2;
			}
		}
		return d;
*/
	}

	/**
	 * It returns the super classes of class1, in the case no superclasses are
	 * available it returns owl:Thing
	 */
	public ArrayList<String> getSuperClasses(String class1) throws Exception {

		String q = this.Prefix + "SELECT ?y ?x WHERE {{ <" + class1
		+ ">  rdfs:subClassOf ?x}	}";
		// the we can have a cicle that retrieve alla the supre class of x

		Query sparql = QueryFactory.create(q);
		// SW QueryExecution qe = QueryExecutionFactory.create(sparql, new
		// NamedGraphDataset(graph));
		QueryExecution qe;
		if (Debug.JENA_REASONING){
		  qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		if (results.getRowNumber() > 1)
			throw new Exception(" The class has more than one super class");
		ArrayList<String> r = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			// RDFNode graph = result.get("graph");
			RDFNode x = result.get("x");
			r.add(x.toString());
		}
		if (r.size() == 0)
			r.add("http://www.w3.org/2002/07/owl#Thing");
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getSuperClasses");
			for (String e : r)
				System.out.print(e + " ");
		}
		qe.close();
		return r;
	}

	/**
	 * @uml.property name="rdfs"
	 */
	private String Prefix = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>	\n";

	/**
	 * it loads in the graph all the uri indicated in rdfs:seeAlso
	 * 
	 * @param uri
	 *            to be deferenced
	 * @param recursive
	 *            you can set it as true if you want follows the rdfs:seeAlso
	 *            reucrsivelly
	 */
	private void seeAlsoExpansion(String uri, boolean recursive) {

		/*
		 * String q=this.Prefix + "SELECT ?x WHERE { <"+uri+"> <"+rdfs:seeAlso
		 * +"> ?x }";
		 * 
		 * Query sparql = QueryFactory.create(q); QueryExecution qe =
		 * QueryExecutionFactory.create(sparql, new NamedGraphDataset(graph));
		 * ResultSet results = qe.execSelect(); // let prepare the results
		 * ArrayList <String> r= new ArrayList<String>(); while
		 * (results.hasNext()) { QuerySolution result = results.nextSolution(); //
		 * RDFNode graph = result.get("graph"); RDFNode subject =
		 * result.get("x"); r.add(subject.toString()); } if
		 * (Debug.printDebugSparqlOntologyModel) { System.out.println(
		 * "getAttributeValue returns"); for (String e: r){ System.out.print(e +"
		 * "); } System.out.println(); }
		 */

	}
	
	private void SerializeTheModel(Model model) throws IOException {
		
		String fileName="/Users/riccardoalbertoni/SIM/models/testLod"+ java.lang.System.currentTimeMillis();
		
		//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
		model.write(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"), "N-TRIPLE");
		model.write(new OutputStreamWriter(new FileOutputStream(fileName+".rdf"),"UTF-8" ), "RDF/XML");
	}
	
	public ArrayList<String> getAllInstancesForClass(String className) {

		String q =  "SELECT distinct ?x WHERE {  ?x a <"+className+"> }";
		// the we can have a cicle that retrieve alla the supre class of x
		ArrayList<String> r= new ArrayList<String>();
		Query sparql = QueryFactory.create(q);
		QueryExecution qe;
		if (Debug.JENA_REASONING){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();

		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
			ResultSetFormatter.out(System.out, results);
		}

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode x = result.get("x");
			r.add(x.toString());
		}
		
		qe.close();

		return r;
	}
	@Override
	public ArrayList<String> getListOfInstances(String sparqlQuery) {
		ArrayList<String> r= new ArrayList<String>();
		Query sparql = QueryFactory.create(sparqlQuery);
		QueryExecution qe;
		if (Debug.JENA_REASONING){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();

		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
			ResultSetFormatter.out(System.out, results);
		}

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode x = result.get("x");
			r.add(x.toString());
		}
		
		qe.close();

		return r;
	}

}
