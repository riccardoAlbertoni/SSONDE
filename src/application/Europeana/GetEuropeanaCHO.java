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

package application.Europeana;
import java.util.ArrayList;
import java.util.Arrays;

import SSONDEv1.Debug;
import SSONDEv1.ListOfInputInstances;
import SSONDEv1.OntologyModel;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;



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
public class GetEuropeanaCHO implements ListOfInputInstances {


	public ArrayList<String> getListOfInstanceURIs(OntologyModel om) {
	
		
		ArrayList <String> a;
		//TDB.getContext().set(TDB.symUnionDefaultGraph, true);
		Dataset dataset = TDBFactory.createDataset("data/Europeana/TDB/") ;
		
		Model omm= dataset.getNamedModel("urn:x-arq:UnionGraph");
		
		// if you want a list of URI resulting from a sparql query
		String query=
				"PREFIX edm: <http://www.europeana.eu/schemas/edm/> "+
			"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"+
			"SELECT DISTINCT ?x WHERE { {{?x ?p ?o} FILTER(regex(STR(?x),\"^http://data.europeana.eu/proxy/europeana/\"))}}";
		System.out.println("Query to get instances on which to apply semSim:\n "+query);
		
		Query sparql = QueryFactory.create(query);
		QueryExecution qe;
		qe = QueryExecutionFactory.create(sparql, omm);
		
		ResultSet results = qe.execSelect();

		a=new ArrayList<String>();		
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode x = result.get("x");
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(x.toString());
			}
			a.add(x.toString());
		}
				
	/**
		ArrayList<String> firstLevel= new ArrayList<String>();

		firstLevel.add("http://data.europeana.eu/proxy/europeana/00902/E08325B1F2649D261AC2B8A7F81A68075E787474");
		firstLevel.add("http://data.europeana.eu/proxy/europeana/09801/EC377CB4D6DBF379E5DDE8DA81D940F652A670EA");
		firstLevel.add("http://data.europeana.eu/proxy/europeana/90901/BD5511A50FF7A4B8DD30C87C9DFB7FCFD59E1C00");
		firstLevel.add("http://data.europeana.eu/proxy/europeana/90901/147BDC0E6B4F2F6384ADBA6B5B180CE277328E36");
		firstLevel.add("http://data.europeana.eu/proxy/europeana/90901/0020657A87C417052DA34C34AA225E122076AF67");
*/

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

		return a;


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
