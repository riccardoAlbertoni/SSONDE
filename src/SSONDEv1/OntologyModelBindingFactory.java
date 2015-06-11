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

import org.json.JSONArray;
import org.json.JSONException;

public class OntologyModelBindingFactory implements OntologyModelBindingFactoryIF {


	private String JENASDBType;

	public OntologyModel createConnection(Configuration  c) {
		OntologyModel m=null;
		
		 
		JSONArray a, b;
		ArrayList <String> RDFDocumentURIs = new  ArrayList<String>();
		String JENArulesPath=null;
		String TDBDirectory=null;
		String JENASDBjdbcURL=null;
		String JENASDBuser= null;
		String JENASDBpsw = null;
		ArrayList <String> listOfModels= new ArrayList <String>();
		
		try {
			b= c.storeConfiguration.getJSONArray("RDFDocumentURIs");
			for(int i=0; i<b.length();i++) RDFDocumentURIs.add(b.getString(i));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		try {	
			switch (KindOfStore.valueOf(c.storeConfiguration.getString("KindOfStore"))){

			case JENAMem:
				m= new JENAMemReasoner( RDFDocumentURIs ,JENArulesPath, false);
				break;
			case JENAMemReasoner:
				try {
					JENArulesPath=c.storeConfiguration.getString("JENARulesURI");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				m= new JENAMemReasoner( RDFDocumentURIs , JENArulesPath, true);
				break;
				
			case JENATDB:
				TDBDirectory= c.storeConfiguration.getString("TDBDirectory");
				m= new JENATBD(TDBDirectory, RDFDocumentURIs , null, false);

				break;
			case JENATDBReasoner:
				try {
					JENArulesPath=c.storeConfiguration.getString("JENARulesURI");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				TDBDirectory= c.storeConfiguration.getString("TDBDirectory");
				m= new JENATBD(TDBDirectory, RDFDocumentURIs , JENArulesPath, true);

				break;
			case JENASDB:
			
					 JENASDBjdbcURL= c.storeConfiguration.getString("JENASDBjdbcURL");
					 JENASDBuser= c.storeConfiguration.getString("JENASDBuser");
					 JENASDBpsw = c.storeConfiguration.getString("JENASDBpsw");
					 JENASDBType= c.storeConfiguration.getString("JENASDBType");
					 JSONArray jListModels= c.storeConfiguration.getJSONArray("ListOfModels");
					 for (int i=0; i<jListModels.length(); i++){
						 listOfModels.add(jListModels.getString(i));
					 }
					m= new JENASDB(JENASDBjdbcURL,JENASDBuser,JENASDBpsw, JENASDBType,listOfModels, RDFDocumentURIs , JENArulesPath, false); 
				break;
			case JENASDBReasoner:
				try {
					JENArulesPath=c.storeConfiguration.getString("JENARulesURI");
					
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return m;
	}



}
