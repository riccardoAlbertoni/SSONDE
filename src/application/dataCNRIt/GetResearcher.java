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
package application.dataCNRIt;

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
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.tdb.TDB;
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
public class GetResearcher implements ListOfInputInstances {

	
	public ArrayList<String> getListOfInstanceURIs(OntologyModel om) {
		
		ArrayList <String> a;
		//TDB.getContext().set(TDB.symUnionDefaultGraph, true);
		Dataset dataset = TDBFactory.createDataset("data/CNRIT/TDB-0.8.9/CNRR/") ;
		
		Model omm= dataset.getNamedModel("urn:x-arq:UnionGraph");
		
		
		String query="Select distinct ?x  where { {"+
		" ?x <http://www.cnr.it/ontology/cnr/pubblicazioni.owl#autoreCNRDi>  ?y."+
			" ?x <http://www.w3.org/2000/01/rdf-schema#label> ?z" +
			 "}}";

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
		
//		String query="Select distinct ?x  where {  {"+
//		" ?x ?p  ?y"+
//			 "}}";

		
		
		
		
		
		//a=om.getListOfInstances(query);

		//test
		//a.add("http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226");
//a.add("http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleInterno/MATRICOLA9407");
		
		return a;
		

	}

	
}
