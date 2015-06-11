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
package test.ToDelete;

//import edu.stanford.smi.protegex.owl.model.OWLIndividual;
//import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
//import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import java.util.ArrayList;
import java.net.URI;
import java.util.Set;

import SSONDEv1.IncompatibleOntologyModel;

import com.hp.hpl.jena.query.ResultSet;


public interface OntologyModel_uri {



	/** 
	 * It prints all ontology classes and the number of instances which are available.
	 */
	//public abstract void printAllClasses();




	/** 
	 * It returns all the classes in the ontology.
	 */
	public abstract Set getAllClasses();





	/** 
	 * It returns all the attributes for a class
	 */
	public abstract ArrayList<String> getAllAttributesForClass(String c);




	/** 
	 * It returns all the relations for a class as collection of string
	 */
	public abstract ArrayList<String> getAllRelationsForClass(String c);




	/**
	 * @throws Exception 
	 */
	public abstract String lub(String firstClass, String secondClass) throws Exception;



	/**
	 */
	public abstract ArrayList<ArrayList<String>> getUpwardCotocopy(String c);







	/** 
	 * It returns the classes  an instance belong to.
	 * ESWC2006 it is equivalent to the inverse of instance=l_c(class)
	 * @throws Exception 
	 */
	public abstract ArrayList<String> getClassesForInstance(String instance) throws Exception;





	//											/**
	//											 * It return the intances for a given class.
	//											 * ESWC2006 it is equivalent to l_c(class). 
	//											 * @param c TODO
	//											 */
	//											public abstract Set getIntancesForClass(String c);
	//




	/**
	 * @throws Exception 
	 */
	//public abstract RDFSDatatype getAttributeType(String attribute) throws Exception;
	public abstract String getAttributeType(String attribute) throws Exception;




	/**
	 * @throws IncompatibleOntologyModel 
	 * @throws Exception 
	 */
	public  String getRelationType(String relation) throws IncompatibleOntologyModel, Exception;





	/**
	 * @throws Exception 
	 */
	public abstract ArrayList<String> getAttributeValue(String instance, String attribute) throws Exception;





	/**
	 */
	public abstract ArrayList <URI> getRelationValues(String instance, String relation);



	/**
	 * Given two classes it works out their distance with repect to the is-a Hierarchy. 
	 */
	public abstract double classDistance(String firstClass, String secondClass);





	/**
	 * @throws Exception 
	 */
	public abstract ArrayList<String> getSuperClasses(String class1) throws Exception;



	public void loadURIs(ArrayList<String> URIs);
	/**
	 * 
	 * @param query a select sparql query, where the prefix are specified
	 * @return it returns the sparql query result against the ontology module
	 */
//	public ResultSet executeSelectSparqlQuery(String query);


	/**
	 * it returns the list of instance URIs for a class
	 * @param className the Uri for the class
	 * @return  instance URI ArrayList 
	 */
	public abstract ArrayList <String> getAllInstancesForClass(String className);
	
	/**
	 *  it returns a list of URI given a sparql query Select x  where {...  }
	 *  It might  used for extract a list of elements to compare in the similarity assessment
	*/
	public abstract ArrayList<String> getListOfInstances( String sparqlQuery); 


}
