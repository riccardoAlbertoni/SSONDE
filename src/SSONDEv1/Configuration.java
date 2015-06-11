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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
/**
 * Configuration is the class that interprets a semantic similarity configuration file.
 * <p>
 * The configuration file is defined by a  JSON file  made of the following parts:
 * <ul> 
 * <li> StoreConfiguration, which specifies where to  read the RDF data; </li>
 * <li> ContextConfiguration, which specifies the context that similarity must apply; </li>
 * <li> InstanceConfiguration, which specifies the list of instances, namely the instances' URI, on which the similarity must be worked out, alternatively, it might specify a piece of  JAVA code  to generate the instances' URI list; </li> 
 * <li> OutputConfiguration, which specifies where and how the semantic similarity result must be stored; </li>
 * </ul>
 * <p>
 * 
 * <ul>
 * <li> <code>StoreConfiguration</code>, JSONObject, includes the following keys:
 *<ul>
 *<li> <code>KindOfStore:</code> a string,  for each of the possibilities specified in the class enum  KindOfStore ( e.g.,  "JENAMem", "JENAMemReasoner","JENATDB", "JENATDBReasoner",  "JENASDB", "JENASDBReasoner"), there is class extending an OntologyModel;
 *</li>
 *</li>
 * <li> <code>StoreConfiguration</code> must be completed by further keys   according to the value specified in <code>KindOfSTore</code>: </li>
 *		<ul>
 * 			<li>if JENAMem, similarity works on a memory JENA model, you should specify
 * 				<ul>
 * 					<li> <code>RDFDocumentURIs</code>: array of URIs as string, List of vocabularies schema or similar that
 * 						 have to be included, it can also be  empty, please consider that URIs of vocabularies indicated as 
 * 						 prefixes in contexts are automatically dereferenced. Instance URIs  have to be listed here if they have to be  dereferenced; 
 * 					</li> 
 * 				</ul>
 * 			</li>
 * 			<li>if JENAMemReasoner, similarity works on a memory JENA model with JENA reasoner, you should specify
 * 				<ul>
 * 					<li><code>RDFDocumentURIs</code>: array of URIs as string, List of vocabularies schema or similar that have to be included, it can also be  empty, please consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced.  Instance URIs  have to be listed here if they have to be  dereferenced; 
 * 					</li>
 * 					<li><code>JENARulesURI</code>: a filePath as string, optional, file containing the JENA rule we want to use, it can be also empty;
 * 					</li>
 * 				</ul>
 * 			</li>
 * 			<li>if JENATDB, similarity works on a TDB JENA store, you should specify
 * 				<ul>
 * 					<li><code>RDFDocumentURIs</code>: array of URIs as string, List of vocabularies schema or similar that have to be included, it can also be  empty, please consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced.  Instance URIs  have to be listed here if they have to be  dereferenced; 
 * 					</li>
 * 					<li><code>TDBDirectory</code>:  a directory Path as String, mandatory, the directory where the TDB is stored;
 * 					</li>
 * 					<li><code>JENARulesURI</code>: a filePath as string, optional, file containing the JENA rule we want to use, it can be also empty;
 *					</li>
 *				</ul>
 *			</li>
 * 			<li>if JENASDB, similarity works on a SDB JENA model, you should specify 
 * 				<ul>
 * 					<li><code>RDFDocumentURIs</code>: array of URIs as string, List of vocabularies schema or similar that have to be included, it can also be  empty, please consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced.  Instance URIs  have to be listed here if they have to be  dereferenced; 
 * 					</li>
 * 					<li><code>JENASDBjdbcURL</code>: a string indicating the JDBC url;
 * 					</li> 					
 * 					<li><code>JENASDBuser</code>: a string indicating the user to the DB;
 * 					</li> 					
 * 					<li><code>JENASDBpsw</code>: a string indicating the password to the DB;
 * 					</li> 					
 * 					<li><code>JENASDBType</code>: a string indicating the kind of Db e.g. PostgreSQL;
 * 					</li> 					
 * 					<li><code>ListOfModels</code>: a JSON array containing the uris of the models we have to consider in the store;
 * 					</li> 					
 *				</ul>
 *			</li>
 *  
 * 			<li>if JENASDBReasoner,  similarity works on a SDB JENA model with JENA reasoning, you should specify  
 * 				<ul>
 * 					<li><code>RDFDocumentURIs</code>: array of URIs as string, List of vocabularies schema or similar that have to be included, it can also be  empty, please consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced.  Instance URIs  have to be listed here if they have to be  dereferenced; 
 * 					</li>
 * 					<li><code>JENASDBjdbcURL</code>: a string indicating the JDBC url;
 * 					</li>
 * 					<li><code>JENASDBuser</code>: a string indicating the user to the DB;
 * 					</li>
 * 					<li><code>JENASDBpsw</code>: a string indicating the password to the DB;
* 					</li> 
 * 					<li><code>JENASDBType</code>: a string indicating the kind of Db e.g. PostgreSQL;
* 					</li>
 * 					<li><code>ListOfModels</code>: a JSON array containing the uris of the models we have to consider in the store;
 * 					</li>
 * 					<li><code>JENARulesURI</code>: a filePath as string, optional, file containing the JENA rule we want to use, it can be also empty;
 * 					</li>
 *				</ul>
 *			</li>
 *		</ul> 
 *		<li> <code>ContextConfiguration</code>, a JSONObject specifying the context
 *			<ul>
 * 				<li><code>ContextFilePath</code>:  the file path as string pointing to the  text file where  context is specified;
 *				</li>
 *              <li><code>ContextEncoding</code>  the Format in which the context is specified, It can be "JSON" or "in-house", if this parameter is not specified "in-house" is assumed as default  
 *			</li>
 *			</ul>
 * 		</li>
 * 
 * 
 * 		<li><code>InstanceConfiguration</code>, you should specify one of the following
 * 			<ul>
 *				<li><code>InstanceURIs</code>: json list of uri that must be assessed by similarity;
 *				</li>
 *				<li><code>InstanceURIsClass</code>: specify the class to extract the list of instances on which we are going to work out the similarity,e.g. "test.InstancesForVisionair", the class is expected to implement the ListOfInputInstances interface, implementing the abstract method ArrayList<String> getListOfInstanceURIs(); 
 *				</li>
 *			</ul>
 *		</li>
 *
 *		<li><code>OutputConfiguration</code>
 *		<ul>
 * 			<li><code>KindOfOutput</code>: one among the strings  specified in the enum class KindOfOutput (e.g.,"CVSFile", "JSONOrderedResult");
 * 		    </li>
 * 			<li>if CVSFile, it writes the result in a CVS file
 * 				<ul>
 * 					<li><code>FilePath</code>: cvs filePath;
 * 					</li>
 * 				</ul>
 * 			</li>
 *			<li> if JSONOrderedResult, it writes the  n most similar result for each input instance result in a JSON file
 * 				<ul>
 * 				<li><code>NumberOfOrderedResult</code>: "n" // n is a integer minor of the number of instances m;
 * 				</li>
 * 				<li><code>FilePath</code>: json file path;
 * 				</li>
 * 				</ul>
 *  			the resulting JSON is in the form 
				 *<pre>
*				[
				   {
				      "Result":[
				         {
				            "Val": "1", // double between 0 and 1 representing the similarity Sim(uriA1,uriB1) result
				            "URI":"uriB1" //Second parameter, an instance
				         },
				         ...
				         {
				            "Val":0.09454253487339823, // double between 0 and 1r epresenting the similarity Sim(uriA1,uriBn) result
				             "URI":"uribBn" Second parameter, an instance uriBn
				         }
				      ],
				      "URI":"first parameter, an instance uriA1"
				   },
				   ....
				   "Result":[
				         {
				            "Val": "1", // double between 0 and 1 representing the similarity Sim(uriAm,uriB1) result
				            "URI":"uriB1" //Second parameter, an instance
				         },
				         ...
				         {
				            "Val":0.09454253487339823, // double between 0 and 1 representing the similarity Sim(uriAm,uriBn) result
				             "URI":"uribBn" Second parameter, an instance uriBn
				         }
				      ],
				      "URI":"first parameter, an instance uriAm"
				   }
				]</pre>
		</ul>
		</li><li> if JSONOrderedResultPartials, it writes the  n most similar result for each input instance result in a JSON file plus the drilldown per attributes 
 * 				<ul>
 * 				<li><code>NumberOfOrderedResult</code>: "n" // n is a integer minor of the number of instances m;
 * 				</li>
 * 				<li><code>FilePath</code>: json file path;
 * 				</li>
 * 				</ul>
 *  			the resulting JSON is in the form 
				 *<pre>
 *				{ 
 * 					"URI": "uriInstance", 
 *  				"rdfs:label":  "xyz",
 * 					"Result":[
 * 								{ "URI": "uri1",
 * 				  				  "rdfs:label":  "xyz", 
 * 				 				  "Val": 1,
 * 								  "DrillDown": [
 * 										{
 * 										path: " ",
 * 						 				property: " ",
 * 						 				operation:" ",
 *  					 				val: " "},
 *  									{ 
 *  									path: " ",
 * 						 				property: " ",
 * 						 				operation:" ",
 *  					 				val: " "}
 *  								]  			 
 *                              },
 * 								{ URI: "uri2",
 *  			  				 "Val": 0.987  },
 * 					...
 * 								{ URI: "urin",
 * 								"Val": 0.987  }, 
 * 
 *   
 * 							 ]
 * 				}
		</pre>
		</ul>
		</li>
</ul>
 *
 **/
//FUTUREWORKS support more than one context 
//TODOV1.2 Write in the wiki how to specify a JSON context
public class Configuration {

	public JSONObject storeConfiguration;
	private JSONObject contextConfiguration;
//	private JSONObject instanceConfiguration;
	private ListOfInputInstances ist=null;
	public  JSONObject outputConfiguration;

	private ArrayList<String> instanceURIs= new ArrayList<String>();
//	private InstaceURIGeneration ins=null;

	public Configuration(String configurationFile) {
		JSONTokener inputFile;
		JSONArray luri;
		try {
			inputFile=	new JSONTokener(new FileInputStream(configurationFile));
			JSONObject conf=new JSONObject(inputFile);
			//store part
			storeConfiguration=conf.getJSONObject("StoreConfiguration");
			
//			 Context part
			this.contextConfiguration =conf.getJSONObject("ContextConfiguration");
			//try {
			
			//instances Part
			JSONObject ic=conf.getJSONObject("InstanceConfiguration");
			 try{
				 //if the list of instances uri to be considered is defined in a class implementing Instances
				 
				 String className = ic.getString("InstanceURIsClass");
				 try {
					 ist= ( ListOfInputInstances) Class.forName(className).newInstance();
					//this.instanceURIs =ist.getListOfInstanceURIs();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 

			 } 
			 catch (JSONException e) {
		
				// if the instancesURI have been defined in the context as arraylist 
				
				luri= ic.getJSONArray("InstanceURIs");
			 
				for(int i=0;i< luri.length(); i++) this.instanceURIs.add(luri.getString(i));
			 	 
				 
			 }

				//				}catch (JSONException e){
				//				// if instancesURI is not defined try to get the procedure to execute in order to determining the list of instances 
				//					String procedureName=conf.getString("instanceURIGeneration");
				//					
				//				    try {
				//						ins = (InstaceURIGeneration) Class.forName(procedureName).newInstance();
				//					} catch (InstantiationException e1) {
				//						// TODO Auto-generated catch block
				//						e1.printStackTrace();
				//					} catch (IllegalAccessException e1) {
				//						// TODO Auto-generated catch block
				//						e1.printStackTrace();
				//					} catch (ClassNotFoundException e1) {
				//						// TODO Auto-generated catch block
				//						e1.printStackTrace();
				//					}
				//					
				//					
				//				}
			 
			 
//			Output Part
			 outputConfiguration=conf.getJSONObject("OutputConfiguration");
			
			  
			  
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
/**
 * 
 * @return the path of file specifying the context
 */
		public String getContextLocation(){
			String res = null;
			try {
				res= this.contextConfiguration.getString("ContextFilePath");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;


		}
		
		/**
		 * 
		 * @return true if the context is in "JSON" false if it is in the "in-house" format, if no encoding is specified then it assumed the context is encode in the in-house format
		 */
				public boolean isContextInJSON(){
					String res = null;
					try {
						res= this.contextConfiguration.getString("ContextEncoding");
					} catch (JSONException e) {
						// if no encoding is specified then it assumed the context is encode in the in-house format
						return false;
					}
					return res.equals("JSON");


				}	
		
/**
 * 
 * @param om ontology model on which to extract the list of istances if a "InstanceURIsClass" is set in the configuration file; null if the list of instances is explicitly stated by "InstanceURIs" parameter in the configuration file
 * @return list of istances URI
 */
		public ArrayList <String> getIstanceURIs(OntologyModel om){
			if (ist==null) return this.instanceURIs;
			else {
				return ist.getListOfInstanceURIs(om);
			}
			
			
		}
        
//		public ArrayList <String> getIstanceUri(OntologyModel o){ 
//			return this.ins.getInstances(o);
//
//		}
	}
