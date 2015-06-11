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
package application.EChallenges2011;

import java.util.ArrayList;
import java.util.Arrays;

import SSONDEv1.Debug;
import SSONDEv1.ListOfInputInstances;
import SSONDEv1.OntologyModel;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;



/**
 * 
 * @author riccardoalbertoni
 * 
 * This class implements the interface ListOfInputInstances, it is an example of how to dynamically download a list of URI instances on which to apply the semantic similarity.
 * 
 * This class is used in  the  VisionairSDB.json  as parameter for "InstanceURIsClass",
 * 
 *
 */
public class InstancesForEUNISSimilarityEchallenges implements ListOfInputInstances {

	
	public ArrayList<String> getListOfInstanceURIs(OntologyModel om) {
		
		ArrayList <String> a;
		
		
		// if you want a list of URI resulting from a sparql query
//		String query="PREFIX vocab: <http://linkeddata.ge.imati.cnr.it:2020/vocab/resource/> \n"+
//		"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"+
//		"SELECT DISTINCT ?x WHERE {"+
//		"{?x skos:prefLabel ?o}"+
//		"FILTER"+
//		"(regex(STR(?x),\"http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2\"))}" +
//		//"Order by ?s"+
//		"LIMIT 5";
//		
		ArrayList<String> firstLevel= new ArrayList<String>();
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.1");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.11");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.12");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.13");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.14");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.3");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.31");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.32");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.33");
		firstLevel.add("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2.34");
		
		
		

//		ArrayList<String> firstLevel=getSubTerms("http://linkeddata.ge.imati.cnr.it:2020/resource/habitat/EunisHabitat2006/B2", om);
//		ArrayList <String> secondLevel=new ArrayList <String>() ;
//		for (String x: firstLevel ){
//			secondLevel.addAll(getSubTerms(x,om));
//		}
//		ArrayList <String> thirdLevel=new ArrayList <String>() ;
//		
//		for (String x: secondLevel ){
//			thirdLevel.addAll(getSubTerms(x,om));
//		}
//		secondLevel.addAll(thirdLevel);
//		firstLevel.addAll(secondLevel);
		
		return firstLevel;
		

	}

	
	// let's find all the narrower concepts
	private   ArrayList <String> getSubTerms(String termUri, OntologyModel om){
		// this part is just to retrieve a list of uri to be compared.
		
		

		String q = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"+
		"PREFIX  owl:  <http://www.w3.org/2002/07/owl#> \n" + 
		"PREFIX  skos:  <http://www.w3.org/2004/02/skos/core#> \n" + 
		"SELECT DISTINCT ?x  WHERE { <"+termUri+"> skos:narrower ?x }";
		
		return om.getListOfInstances(q);
	}
}
