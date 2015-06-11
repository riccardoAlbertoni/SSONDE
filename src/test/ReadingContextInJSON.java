package test;

import SSONDEv1.OntologyModel;

import SSONDEv1.ContextLayer;

public class ReadingContextInJSON {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	 
	// context with 100 for one of the attribute	
	//String pathJSON ="/Users/riccardoalbertoni/Documents/workspace/SSONDE/bin/test/JSONContextTest.json";	
   
	// context with 50 as weight and not specified   the other attribute	
	//String pathJSON ="/Users/riccardoalbertoni/Documents/workspace/SSONDE/src/test/JSONContextTest1.json";	
	
	// context with 50 as weight and 3   other attribute not specified 	
	String pathJSON ="/Users/riccardoalbertoni/Documents/workspace/SSONDE/src/test/JSONContextTest2.json";	
	
	ContextLayer c= new ContextLayer(pathJSON, true, 1, 0, null);
	System.out.println(c.toString());
	
	String pathCTX="/Users/riccardoalbertoni/Documents/workspace/SSONDE/src/test/JSONContextTest2.ctx";
	ContextLayer c1 = new ContextLayer(pathCTX, false, 1, 0, null);
	System.out.println(c1.toString());
	}

}
