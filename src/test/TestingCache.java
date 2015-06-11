package test;

import SSONDEv1.ComparisonCache;

import SSONDEv1.ContextLayer;

public class TestingCache {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ComparisonCache c=new ComparisonCache();
	
	try {
	System.out.println("-1 : " +c.isInCache("http://purl.org/dc/terms/subject", "Count",  "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226"));
		c.putInCache("http://purl.org/dc/terms/subject", "Count",  "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", 3.0);
	System.out.println("3 : "+ c.isInCache("http://purl.org/dc/terms/subject", "Count",  "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226"));
	c.putInCache("http://purl.org/dc/terms/subject", "Count",  "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", "http://www.cnr.it/ontology/cnr/individuo/unitaDiPersonaleEsterno/ID226", 3.0);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
