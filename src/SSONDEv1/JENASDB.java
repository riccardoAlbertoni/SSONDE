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

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.query.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import java.io.FileOutputStream;			
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * 
 * @author riccardoalbertoni
 * @version 1.0
 * @since 2011-11-15
 * 
 * This class implements the similarity.OntologyModel interface for defining disk stored JENA model, model might support forward chaining reasoning, 
 *  
 *  
 */

//FUTUREWORKS we should keep methods which access to the extensional part of model separate from methods pertaining to the schema/vocabulary level, for example we might have ExtensionalOntologyModel and ExternalOntologyModel super interface on the current OntologyModel 
//FUTUREWORKS we should have different sparql query in the case of quadstore, 

public class JENASDB implements OntologyModel {
	Model	model;	
	InfModel infModel;
	boolean reasonerActive=false;

	static String JENASDBjdbcURL;
	static String JENASDBuser;
	static String JENASDBpsw;
	static String JENASDBType;

	private GenericRuleReasoner setUpReasoner(String uri){


		List <Rule> rules = Rule.rulesFromURL(uri);		    
		GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		reasoner.setOWLTranslation(true);               // not needed in RDFS case
		reasoner.setTransitiveClosureCaching(true);
		return reasoner;
	}

	/**
	 * @param directory the local path in which is stored the JENA model
	 * @param RDFDocumentURIs list of URIs representing background RDF documents, such as vocabularies schema that must be considered in the model 
	 * @param ruleUri An URI representing a file which contains JENA rules
	 * @param reasonerActive true when reasoning must be active
	 * 
	 * JENA rules look as in the following
	 * 	 @prefix  wiki: <http://semanticweb.org/id/>. 
	 *	[r1b:  (?x owl:sameAs ?y) (?z owl:sameAs ?y)-> (?x owl:sameAs ?z)]
	 *	[r2: (?x wiki:Property-3AHas_PC_member ?y) -> (?y wiki:INVProperty-3AHas_PC_member ?x)]
	 *
	 *
	 */
	public JENASDB( String JENASDBjdbcURL, String JENASDBuser, String JENASDBpsw, String JENASDBType,ArrayList <String> listOfModels,	ArrayList <String> RDFDocumentURIs, String ruleUri, Boolean reasonerActive) {

//		new way to connect to a sdb jena store 	
//		StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash,
//		DatabaseType.PostgreSQL) ;
//		JDBC.loadDriverPGSQL(); 
//		SDBConnection conn = new SDBConnection(JENASDBjdbcURL, JENASDBuser, JENASDBpsw) ; 
//		Store store = SDBFactory.connectStore(conn, storeDesc) ;
//		model=SDBFactory.connectDefaultModel(store);
//		//		 model = TDBFactory.createModel(directory) ;
		//		//model = ModelFactory.createDefaultModel();

		JENASDB.JENASDBjdbcURL=JENASDBjdbcURL;
		JENASDB.JENASDBuser=JENASDBuser;
		JENASDB.JENASDBpsw=JENASDBpsw;
		JENASDB.JENASDBType=JENASDBType;

//		for (String m : listOfModels ){
//		model.add(getRDBModel(m));
//		}

		model=getRDBModel("file:/home/visionair/ontologies/shapeOntology_v2.3.1.owl");

		this.reasonerActive=reasonerActive;
		for ( String uri :RDFDocumentURIs){	
			model.read(uri);
			if (Debug.printDownLoadedURI)
				System.out.println("The " + uri + " has been retrieved by JENATBD.Constructor!");
		}

		GenericRuleReasoner reasoner;
		if (reasonerActive){	
			reasoner =this.setUpReasoner(ruleUri);
			infModel = ModelFactory.createInfModel(reasoner, model);
		}
	}
	private static IDBConnection getIDBConnection() {

		IDBConnection jdbcConnection = null;
		try {
			jdbcConnection = new DBConnection(JENASDBjdbcURL, JENASDBuser, JENASDBpsw, JENASDBType);
		} catch (Exception ex) {
			System.out.println("Exception making connection: " + ex.getMessage());
			System.exit(9);
		}
		return jdbcConnection;
	}

	private static void closeIDBConnection(IDBConnection con) {

		try {
			con.close();
		} catch (Exception ex) {
			System.out.println("Exception closing connection: " + ex.getMessage());
			System.exit(9);
		}
	}

	private static ModelRDB getRDBModel(String modelName) {

		ModelRDB dbModel = null ;

		try {

			if (modelName == null)
				dbModel = ModelRDB.open(getIDBConnection());
			else
				try {
					dbModel = ModelRDB.open(getIDBConnection(), modelName);
				} catch (com.hp.hpl.jena.shared.DoesNotExistException ex) {
					System.out.println("No model '" + modelName + "' in that database");
					System.exit(9);
				}
		} catch (com.hp.hpl.jena.db.RDFRDBException dbEx) {
			Throwable t = dbEx.getCause();
			if (t == null)
				t = dbEx;
			System.out.println("Failed to connect to the database: " + t.getMessage());
			System.exit(9);
		}

		return dbModel;
	}

	/**
	 * It loads all the graphs that are available in the uris contained in the
	 * array
	 * @param URIs list of URIs to be uploaded
	 */
	public void loadURIs(ArrayList<String> URIs) {

		for (String uri : URIs) {
			if (this.reasonerActive) infModel.read(uri);
			else model.read(uri);
			if (Debug.printDownLoadedURI)
				System.out.println("The " + uri + " has been retrieved by JENATBD.loadUri!");
		}
	}		


	public String getAttributeType(String attribute) throws Exception {
		String q = this.Prefix + "SELECT ?x WHERE { <" + attribute
		+ "> rdfs:range ?x }";
		// the we can have a cicle that retrieve alla the supre class of x

		Query sparql = QueryFactory.create(q);
		QueryExecution qe;
		if (this.reasonerActive){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else   qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		String r = "";

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
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
		String q = this.Prefix + "SELECT ?x WHERE { <" + relation
		+ "> rdfs:range ?x }";
		// the we can have a cycle that retrieve all the super classes of x

		Query sparql = QueryFactory.create(q);
		QueryExecution qe;
		if (this.reasonerActive){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}

		String r = "";
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
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
		QueryExecution qe;
		if (this.reasonerActive){
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
			RDFNode subject = result.get("x");
			r.add(subject.toString());
		}
		if (Debug.printDebugSparqlOntologyModel) {
			System.out.println("getAttributeValue returns");
			for (String e : r) {
				System.out.println(e + " ");
			}
			System.out.println();

		}
		qe.close();
		return r;
	}



	public ArrayList<String> getRelationValues(String instance, String relation) {
		String q = this.Prefix + " select DISTINCT ?x WHERE { " + "{<"
		+ instance + ">  <" + relation + "> ?x .}" +
		// query to find the inverse relation
		//" UNION {?inv  <http://www.w3.org/2002/07/owl#inverseOf> <"
		//+ relation + ">. " + "?x ?inv <" + instance + ">.} " + 
		"}";
		Query sparql = QueryFactory.create(q);
		QueryExecution qe;
		if (this.reasonerActive){
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
		qe.close();

		/// let clean r from duplicated instance with respect to owl:SameAs
		// r= cleanDuplicates(r);
		return r;
	}
	/**
	 * @uml.property name="rdfs"
	 */
	private String Prefix = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>	\n";

	public String toString() {
		String r=new String();

		r.concat("\n Is reasoner active? : "+this.reasonerActive);
		r.concat("\n model : " + (reasonerActive ? this.infModel : this.model ) );
		r.concat("\n prefix loaded" + this.Prefix.toString());
		return r;
	}

	public void SerializeTheModel(String fileName) throws IOException {
		if (fileName==null)
			fileName="serializedModel"+ java.lang.System.currentTimeMillis();

		//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
		model.write(new OutputStreamWriter(new FileOutputStream(fileName+".n3"),"UTF-8"), "N-TRIPLE");
		model.write(new OutputStreamWriter(new FileOutputStream(fileName+".rdf"),"UTF-8" ), "RDF/XML");
	}


	@SuppressWarnings("unused")
	private ArrayList<String> cleanDuplicates(ArrayList<String> r) {
		int i=0;
		boolean goOn=true;
		while(goOn && (r.size()>0)) {

			String x=	r.get(i);

			String q = this.Prefix + " select DISTINCT ?x WHERE { " + "{<"
			+ x + ">  <http://www.w3.org/2002/07/owl#sameAs> ?x .}" +
			// query to find the inverse relation
			//" UNION {?inv  <http://www.w3.org/2002/07/owl#inverseOf> <"
			//+ relation + ">. " + "?x ?inv <" + instance + ">.} " + 
			"}";

			//Iterator <String> i=	r.iterator();
			Query sparql = QueryFactory.create(q);
			QueryExecution qe;
			if (this.reasonerActive){
				qe = QueryExecutionFactory.create(sparql, infModel);
			}  else  qe = QueryExecutionFactory.create(sparql, model);

			ResultSet results = qe.execSelect();
			// let prepare the results
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(sparql);
			}

			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
				RDFNode subject = result.get("x");
				if(!x.equals(subject.toString())){
					r.remove(subject.toString());
					if (Debug.printDebugSparqlqueryInTheOntologyModel) {
						System.out.println(" I am removing sparql"+ subject.toString());
					}
				}
			}
			if (i< (r.size()-1)) i++;
			else goOn=false;
		}

		return r;
	}

	/**
	 * Given two classes it works out their distance with respect to the is-a
	 * Hierarchy.
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
		//this.reasonerActive
		QueryExecution qe;
		if ( this.reasonerActive){
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
					if (this.reasonerActive){
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
					QueryExecution qe;
					if (this.reasonerActive){
						qe = QueryExecutionFactory.create(sparql, infModel);
					}  else  qe = QueryExecutionFactory.create(sparql, model);
					ResultSet results = qe.execSelect();
				if (Debug.printDebugSparqlqueryInTheOntologyModel) {
						System.out.println(sparql);
//						ResultSetFormatter.out(System.out, results);
					}

					while (results.hasNext()) {
						QuerySolution result = results.nextSolution();
						RDFNode x = result.get("x");
						if (Debug.printDebugSparqlqueryInTheOntologyModel) {
							System.out.println(x.toString());
						}
						r.add(x.toString());
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
		QueryExecution qe;
		if (this.reasonerActive){
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
		//qe.close();
		return r;
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
		QueryExecution qe;

		if (this.reasonerActive){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else qe = QueryExecutionFactory.create(sparql, model);
		ResultSet results = qe.execSelect();
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
		}
		HashSet<String> r = new HashSet<String>();
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
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

	public ArrayList<String> getAllInstancesForClass(String className) {

		String q =  "SELECT distinct ?x WHERE {  ?x a <"+className+"> }";
		// the we can have a cicle that retrieve alla the supre class of x
		ArrayList<String> r= new ArrayList<String>();
		Query sparql = QueryFactory.create(q);
		QueryExecution qe;
		if (this.reasonerActive){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();

// if we call the result formatter then the results are empty		
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
//			ResultSetFormatter.out(System.out, results);
		}

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode x = result.get("x");
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(x.toString());
			}

			r.add(x.toString());
		}
		
		qe.close();

		return r;
	}
	
	public ArrayList<String> getListOfInstances(String sparqlQuery) {
		ArrayList<String> r= new ArrayList<String>();
		Query sparql = QueryFactory.create(sparqlQuery);
		QueryExecution qe;
		if (this.reasonerActive){
			qe = QueryExecutionFactory.create(sparql, infModel);
		}  else  qe = QueryExecutionFactory.create(sparql, model);

		ResultSet results = qe.execSelect();

// if we call the result formatter then the results are empty		
		if (Debug.printDebugSparqlqueryInTheOntologyModel) {
			System.out.println(sparql);
//			ResultSetFormatter.out(System.out, results);
		}

		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			RDFNode x = result.get("x");
			if (Debug.printDebugSparqlqueryInTheOntologyModel) {
				System.out.println(x.toString());
			}

			r.add(x.toString());
		}
		
		qe.close();

		return r;
	}
}
