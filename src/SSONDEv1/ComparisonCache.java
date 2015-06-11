/** 
 * SSONDE, a framework providing an instance similarity enabling in a detailed comparison, 
 * ranking of resources through the comparison of their RDF ontology driven metadata.
 * From the theoretical point of view SSONDE adapts the instance similarity presented in 
 * "Asymmetric and Context-Dependent Semantic Similarity among Ontology Instances. J. Data 
 * Semantics 10: 1-30 (2008)" into a linked data settings.
 *
 * Copyright (C) 2012  Riccardo Albertoni
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

import java.util.HashMap;
/** 
 * @author RiccardoAlbertoni
 * @date  November 5, 2012
 * 
 * It implements a very simple cache for intermediate results obtained during recursive similarity assessment 
 * 
 * @version 1.2
 * 
 */
public class ComparisonCache <T> {
	HashMap<String, HashMap<T, HashMap<T, Double>>> cache;

	public ComparisonCache(){
		cache=new HashMap <String, HashMap <T, HashMap <T, Double>>>();
	}

	/**
	 * @param pathAndProperty a string representing the path plus the properties we are considering
	 * @param operation which operation was specified in the context
	 * @param firstE the first parameter to compare 
	 * @param secondE the second parameter to compare  
	 * @returns the cached similarity value which ranges in [0,1], -1 if the value was not cached
	 */
	public double isInCache(String pathAndProperty,String operation, T firstE, T secondE){
		
		HashMap<T, HashMap<T, Double>> inPath;
		HashMap<T, Double> inFirstE;
		if (cache.containsKey(pathAndProperty+operation)) inPath= cache.get(pathAndProperty+operation);
		else return -1;
		
		if (inPath.containsKey(firstE)) inFirstE=  inPath.get(firstE);
		else{
			return -1;
		}
		
        if (inFirstE.containsKey(secondE)) return inFirstE.get(secondE);
       return	-1;

	}
	/**
	 * @param pathAndProperty a string representing the path plus the properties we are considering
	 * @param operation which operation was specified in the context
	 * @param firstE the first parameter to compare 
	 * @param secondE the second parameter to compare 
	 * @param value similarity value to remember
	 * @throws Exception if an element is inserted twice
	 */
	public void putInCache(String pathAndProperty ,String operation, T firstE, T secondE, double value) throws Exception{

		HashMap<T, HashMap<T, Double>> inPath;
		HashMap<T, Double> inFirstE;
		if (cache.containsKey(pathAndProperty+operation)) inPath= cache.get(pathAndProperty+operation);
		else {
			inPath = new HashMap <T, HashMap <T, Double>> ();
			cache.put(pathAndProperty+operation, inPath);
		}
			
		if (inPath.containsKey(firstE)) inFirstE=  inPath.get(firstE);
		else{
			inFirstE=new  HashMap <T, Double>();
			inPath.put(firstE,inFirstE);
		}
		
        if (inFirstE.containsKey(secondE)) throw new Exception("Cached twice ? pathAndProperty: "+pathAndProperty+"  operation: "+operation+" first element " +firstE+ " second element "+ secondE);
		
        inFirstE.put(secondE, value);
	}
}
