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

/** 
 * This class define a set of boolean variable whcih enable the debugging message.
 */
 public final  class Debug {
	public static final boolean printDebug= false;		
	public static final boolean printDebugSparqlOntologyModel= true;
	
	public static final boolean printDebugSparqlqueryInTheOntologyModel= false; // true when we want to see the sparql query that are made at the rdf store by the ontologyModel	
	
	public static final boolean printDownLoadedURI=true;// true when we want that the uri downloaded must be printed on the screen
	
	public static final boolean RecursiveSimilarityCaching=true;
	public static final boolean IntersectionByHashing=true;
	
	static final boolean SINDICE_EXPANSION_CACHE= false;
	static final boolean SINDICE_CACHE =false;
	
	static final boolean JENA_REASONING=true; //obsolete
	public static final boolean OFF_LINE=true; // if we are off line the instances are non loaded as uri but their RDF info are loade by a file RDF
	/*
	 * It is the set of graph downloaded by Semantic web
	 */
	// sc NamedGraphSet graph;
	public static final boolean printDebugRankingSindice=true;
	
	/*
	 * It is the set of graph downloaded by Semantic web
	 */
	// sc NamedGraphSet graph;
}

