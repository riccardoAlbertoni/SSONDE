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
import java.util.Iterator;

import com.hp.hpl.jena.query.ResultSet;


/** 
 * The main class in the library.
 */

//FUTUREWORKS to clean this class, (a) why we have provided so many constructors are all needed, ( b) add a method to work out the similarity among a set of instances and store the results into a Result Class  

public class SimilarityTool {

	/**
	 * It assess the similarity among all the instances of a given Ontology
	 * @param args
	 */

	/**
	 * It sets up the similarity tool. It relies on an ontology model considering Graph encoded in Rdf-xml by default.  
	 * @param uriOntology list of string representing the URI of instances to consider
	 * @param uriContext URI of the context specification 
	 * @param wExtensional weight of extensional  similarity (comparing data represented as instances) with respect the overall similarity
	 * @param wExternal weight of the external similarity (comparing classes in the ontology schema)   with respect the overall similarity
	 * @param rulesURI 
	 * @throws Exception
	 */
	public SimilarityTool(ArrayList <String> uriOntology, String uriContext, double wExtensional, double wExternal, String uriRules) throws Exception{
		
		om=new OntologyModelSparqlSindice(uriOntology, uriRules);
		this.ontologyLayer= new OntologyLayer(this,om);
		this.contextLayer = new ContextLayer(uriContext,false,wExtensional, wExternal, om);
		this.dataLayer= new DataLayer();

	}
	/**
	 * It sets up the similarity tool starting from an already initialized ontology model. 
	 * It can be useful when the same data are envaluate by different context, or whenever a different data encoding is needed. 
	 * @param om an instantiated OntologyModel,
	 * @param uriContext URI of the context specification 
	 * @param wExtensional weight of extensional  similarity (comparing data represented as instances) with respect the overall similarity
	 * @param wExternal weight of the external similarity (comparing classes in the ontology schema)   with respect the overall similarity
	 * @throws Exception*/
	public SimilarityTool(OntologyModel om, String uriContext, double wExtensional, double wExternal) throws Exception{
		this.om=om;
		this.ontologyLayer= new OntologyLayer(this,om);
		this.contextLayer = new ContextLayer(uriContext,false,wExtensional, wExternal, om);
		this.dataLayer= new DataLayer();
		

	}



	/**
	 * @param symmetrical true if the simmetrical similarity ghas to be applied .. 
	 * TODO to be developed
	 */
	public void workSimilarityOutAllInstances(boolean symmetrical){
		//TODO which parameter has to be passed, the file where to write the results or the DataStructure containgthe results
	}


	/**
	 * @uml.property   name="ontologyLayer"
	 * @uml.associationEnd   aggregation="shared" inverse="similarityTool:similarity.OntologyLayer"
	 */
	public OntologyLayer ontologyLayer;
	/**
	 * @uml.property   name="dataLayer"
	 * @uml.associationEnd   aggregation="shared" inverse="similarityTool:similarity.DataLayer"
	 */
	public DataLayer dataLayer;
	/**
	 * @uml.property   name="contextLayer"
	 * @uml.associationEnd   aggregation="shared" inverse="similarityTool:similarity.ContextLayer"
	 */
	public ContextLayer contextLayer;
	/** 
	 * Getter of the property <tt>contextLayer</tt>
	 * @return  Returns the contextLayer.
	 * @uml.property  name="contextLayer"
	 */
	public ContextLayer getContextLayer() {
		return contextLayer;

	}
	/**
	 * It sets up the similarity tool starting from a Configuration instance
	 * @param c
	 * @throws Exception
	 */
	public SimilarityTool(Configuration c) throws Exception{
		om=new OntologyModelBindingFactory().createConnection(c);
		this.ontologyLayer= new OntologyLayer(this,om);
		this.contextLayer = new ContextLayer(c.getContextLocation(),c.isContextInJSON(),1d, 0d, om);
		this.dataLayer= new DataLayer();

	}

	/**
	 * It sets up the similarity tool starting from a Configuration instance
	 * @param c
	 * @throws Exception
	 */
	public SimilarityTool(Configuration c, OntologyModel om) throws Exception{
		this.om= om;//new OntologyModelBindingFactory().createConnection(c);
		this.ontologyLayer= new OntologyLayer(this,om);
		this.contextLayer = new ContextLayer(c.getContextLocation(),c.isContextInJSON(),1d, 0d, om);
		this.dataLayer= new DataLayer();

	}


	/** 
	 * Setter of the property <tt>contextLayer</tt>
	 * @param contextLayer  The contextLayer to set.
	 * @uml.property  name="contextLayer"
	 */
//	public void setContextLayer(ContextLayer contextLayer) {
//	this.contextLayer = contextLayer;
//	}


	/**
	 * It contains all the retrieved URI, and whenever it is necesserary it is passed to the context and ontology layer methods. 
	 * @uml.property  name="om"
	 */
	private OntologyModel om;

	/**
	 * It loads in the ontology model the  graph obtained deferencing the uri
	 * @param URIs
	 */
	public void loadUri(ArrayList <String> URIs){
		om.loadURIs(URIs);
	}

	public SimilarityResultWithPartials asymSimilarity(ArrayList <String> instances, ArrayList<String> instanceLabels){
		//SimilarityResults sr = new SimilarityResults(instances, instanceLabels);	
		
		SimilarityResultWithPartials sr= new SimilarityResultWithPartials(instances, instanceLabels, this.contextLayer);
		for (String i : instances ){
			for (String ii : instances ){
				try {
					// sr.put(i, ii, this.ontologyLayer.simAsym(i,ii));
					this.ontologyLayer.simAsym(i,ii, sr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return sr;


	}
}
