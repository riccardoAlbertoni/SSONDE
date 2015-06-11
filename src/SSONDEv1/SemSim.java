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

import org.json.JSONObject;


/**
 * @author riccardoAlbertoni
 * 
 * Application to call the semantic similarity  given a configuration file by command line
 * 
 * parameters -conf configuration file json, for a description of the expected JSON file structure see  {@link SSONDEv1.Configuration} class)  
 */
public class SemSim {

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		ArrayList <String> cf=new ArrayList <String>();  
		Configuration configuration;
		SimilarityTool similarityTool;

		int iii=0;

		if (args.length<1) System.out.println("No parameters specified: you must specify at least a  configuration file by using -conf filepath ");
		do 
			if (args[iii].equals("-conf")) {  cf.add(args[++iii]); iii++;}
			else System.out.println(" wrong line command: "+ args[iii++]);

		while(iii<args.length);
	
		try {
			for (String x : cf){
				// a configuration   is created according to a JSON file
				configuration=new Configuration(x); //"conf/test/VisionairSDB.json
			
				//Let's create the connection to data according to the configuration 
				SSONDEv1.OntologyModel om=new SSONDEv1.OntologyModelBindingFactory().createConnection(configuration);
				
				// let's instantiate the similarity tool according to the configuration on the ontology model om
				similarityTool = new SimilarityTool(configuration, om);

				// list of instances to be consider 
				ArrayList <String> listuri= configuration.getIstanceURIs(om);
				if (listuri.isEmpty()) throw new Exception("There are no Instances on which to assess the semantic similarity");
				
				//lets ask for each instance the rdf
				ArrayList <String> comm, 
				instanceLabels= new ArrayList <String> ();  
				
				for (String j: listuri){
					comm=om.getAttributeValue( j, "http://www.w3.org/2000/01/rdf-schema#label");
					instanceLabels.add(comm.size()!=0?comm.get(0):"");
			}
				// Intit the similarity results and  work out the similarity
				SimilarityResultWithPartials result= similarityTool.asymSimilarity(listuri, instanceLabels)	;

				// let provide the result according to the configuration
				result.writeResult(configuration);

				System.out.println(result.toString());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		long elapsedTime = System.nanoTime() - startTime;
		System.out.println(" the similarity assessment has taken: "+  elapsedTime +" nanoseconds equivalent to " +elapsedTime/1000000000 +"seconds" );
	
	}



}
