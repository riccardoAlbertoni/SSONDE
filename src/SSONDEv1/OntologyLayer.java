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
 * 
 * 
 **/
package SSONDEv1;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.net.URI;

//import Attempts.OntologyModelSparql;

/** 
 * @author RiccardoAlbertoni
 * @date  November 5, 2012
 * 
 * 
 * @version 1.2
 * - a Caching Mechanism for recursive instances comparison has been deployed 
 * - part of code related to External similarity is deprecated because it is not very relevant in Linked data context
 * - part related to the symmetrical similarity commented in the code 
 */
public class OntologyLayer {

	public OntologyLayer(SimilarityTool simTool, OntologyModel om) throws Exception{
		ontologyModel = om;
		similarityTool  =simTool;
		cache= new ComparisonCache();

	}

	/** 
	 * The methods to load an ontology from a URI.
	 * It sets up the ontology as the private attribute ontologyModel
	 */
	public void loadURIs(ArrayList <String> URIs) throws Exception{
		ontologyModel.loadURIs(URIs);

	}

	/**
	 * @param symmetrical TODO
	 * @return TODO
	 * @throws Exception 
	 */
	@Deprecated
	public double externalSimilarity(String firstInstance, String secondInstance, boolean symmetrical) throws Exception{

		//to find out the lub for the instances 
		Collection <String> classesForFirst= ontologyModel.getClassesForInstance(firstInstance);
		Collection <String> classesForSecond= ontologyModel.getClassesForInstance(secondInstance);
		//ONTOMODEL to handle the multiple class type for instances - if there are more than one element in the intersection then the instances have multiple classes they belong to 
		String first = (classesForFirst.iterator()).next();
		String second = (classesForSecond.iterator()).next();
		if (first.equals(second)) return 1;

		if ((classesForFirst.size()>1)||(classesForSecond.size()>1)) throw new Exception("Multiple class type for instances");
		// By working out the lub, it is possible to realize which is the class having attribute and relation common to both the instances
		double resultSM= (this.SM(first,second, symmetrical));
		double resultCM =(this.CM(first,second, symmetrical));					

		return (resultSM+resultCM)/2;
	}




	@Deprecated
	private double SM(String first, String second, boolean symmetrical) throws Exception {

		String lub=ontologyModel.lub(first,second);
		double alfa,simAtt,simRel,
		dfl = ontologyModel.classDistance(first,lub),
		dsl = ontologyModel.classDistance(second,lub);
		alfa=(ontologyModel.classDistance(first,lub)/ontologyModel.classDistance(first,second));
		if (dfl>dsl) alfa= 1-alfa;


		//TODO to add the adjustement with respect to the relation and attribute inheritance 
		//then we perform the slot matching 
		ArrayList <String> firstAtt= ontologyModel.getAllAttributesForClass(first);
		ArrayList <String> secondAtt= ontologyModel.getAllAttributesForClass(second);
		ArrayList <String> intersectAtt = new ArrayList <String>(),
		onlyFirst = new ArrayList <String>(),
		onlySecond = new ArrayList <String>();

		for ( Iterator <String> i=firstAtt.iterator(); i.hasNext(); ){
			String e = i.next();
			if (secondAtt.contains(e)) intersectAtt.add(e);	
			else onlyFirst.add(e); 
		}

		for ( Iterator <String> i=secondAtt.iterator(); i.hasNext(); ){
			String e = i.next();
			if (!firstAtt.contains(e))  onlySecond.add(e); 
		}

		simAtt=((double) intersectAtt.size())/((intersectAtt.size())+(alfa*(onlyFirst.size()))+((1-alfa)*(onlySecond.size())));

		//TODO What in the case of simetry 
		//TODO to add the adjustement with respect to the relation and attribute inheritance 
		//then we perform the slot matching 

		ArrayList <String> firstRel= ontologyModel.getAllRelationsForClass(first);
		ArrayList <String> secondRel= ontologyModel.getAllRelationsForClass(second);
		ArrayList <String> intersectRel = new ArrayList <String>();
		onlyFirst = new ArrayList <String>();
		onlySecond = new ArrayList <String>();

		for ( Iterator <String> i=firstRel.iterator(); i.hasNext(); ){
			String e = i.next();
			if (secondRel.contains(e)) intersectRel.add(e);	
			else onlyFirst.add(e); 
		}

		for ( Iterator <String> i=secondRel.iterator(); i.hasNext(); ){
			String e = i.next();
			if (!firstRel.contains(e))  onlySecond.add(e); 
		}

		simRel=((double) intersectRel.size())/((intersectRel.size())+(alfa*(onlyFirst.size()))+((1-alfa)*(onlySecond.size())));
		return (simRel+simAtt)/2;
	}




	@Deprecated
	private double CM(String first, String second, boolean symmetrical) {
		// TODO Auto-generated method stub
		//NOLUBVERSION the method has to be changed
		/*	
					ArrayList<String> ucfirst = this.ontologyModel.getUpwardCotocopy(first);
					ArrayList<String> ucsecond = this.ontologyModel.getUpwardCotocopy(second);
					ArrayList <String> intersection= new ArrayList<String>(); 

					for (String x:ucfirst){
						if (ucsecond.contains(x))intersection.add(x);
						}
				return ((double)intersection.size())/ ucfirst.size();
		 */
		return 1d;
	}





	/**
	 * @param symmetrical TODO
	 * @param path TODO
	 * @param sr 
	 * @throws Exception 
	 */
	public double extensionalSimilarity(String firstInstance, String secondInstance, /* boolean symmetrical, */ String path, SimilarityResultWithPartials sr) throws Exception{

		double acc=0;
		// Are they the same object? 
		if (firstInstance==secondInstance) return 1; // their are completely equal
		else{

			ArrayList <ArrayList <String>> slot;

			slot =this.similarityTool.contextLayer.getRelevantSlotForClass(path);

			/* according to the criteria that are defined in the context 
						 for each attribute and relation we call the appropriate Instance values comparison
			 */
			Iterator <String> attIter = slot.get(0).iterator();
			Iterator <String> relIter = slot.get(1).iterator();

			// let us compare the attribute values for the instances
			while (attIter.hasNext()){
				// the name of the attribute we are considering
				String att=(String) attIter.next();
				//let us know the type of criteria associated to the attribute in the context
				String criteria=similarityTool.contextLayer.getAttributeCriterion(att,path);
				// let us know the weight of the attribute
				float weight =similarityTool.contextLayer.getAttWeight(att,path);
				// let us know the attribute values for the instances we are comparing
				ArrayList <String> fvalue= ontologyModel.getAttributeValue(firstInstance,att);
				ArrayList <String> svalue= ontologyModel.getAttributeValue(secondInstance,att);
				//let us know the attribute dataType
				String dataType=(this.ontologyModel.getAttributeType(att));
				double part=0;
				/*if (!(symmetrical)) { */
				if (criteria.equals("Count")||((svalue.size()==0)||(fvalue.size()==0))){ 
					double fcount=fvalue.size();
					double scount=svalue.size();

					if ((criteria.equals("Count")&&((svalue.size()!=0)||(fvalue.size()!=0)))){
						part=(scount/Math.max(fcount,scount));
						acc+=part*(weight/100);
					}else {
						// in the case they have no value for this attribute their similarity has to be considered 0
						acc+=0;
					}
				}
				//the case where one of the two relations has no values
				else if ((svalue.get(0)==null)||(fvalue.get(0)==null)){
					// nothing has to be done!!!
				}

				else if (criteria.equals("Inter")){
					// let us find out which is the intersection.. 
					Collection <String> intersection= new ArrayList<String>(); 

					for(String e : fvalue) { 
						// this is  to work out the intersection..
						for (String ee : svalue ){
							String  el=(ee); // shallow copy 
							if (el.equals(e)){  
								intersection.add(el); 
								break; // if e is contained twice in ee  has to be inserted one time only    
							} 
						}
					}

					part=((double) intersection.size()/ (double)fvalue.size());
					acc+=part*(weight/100);
				} 
				else if (criteria.equals("Simil")) {

					if (Debug.printDebug)System.out.println("there is a criteria equal to Simil ");
					// in order to figure out the similarity among two sets we should figure out which is the best elements accomplishment.
					// let do it as straightforwardly as possible 

					double accMax=0, accCom=0;

					ArrayList <ArrayList<Couple<String>>> aa= DataLayer.generateSetAccomplishement(fvalue,svalue, new ArrayList<Integer>(),new ArrayList<Integer>()); 
					for(Iterator <ArrayList<Couple<String>>> i=aa.iterator(); i.hasNext(); ){
						ArrayList <Couple<String>> a=i.next();
						for(Iterator <Couple<String>> ii=a.iterator(); ii.hasNext();  ){
							Couple<String> c=ii.next();
							if (dataType.equals(new String("string"))||dataType.equals(new String("http://www.w3.org/2001/XMLSchema#string")) /*||dataType.equals(new String("boolean"))*/) {
								accCom+= this.similarityTool.dataLayer.simString(((c.getFirst())),(c.getSecond()));
							}
							else if (dataType.equals(new String("float"))||dataType.equals(new String("int"))) {
								accCom+= this.similarityTool.dataLayer.simNumber((Float.parseFloat(c.getFirst())),Float.parseFloat((c.getSecond())));
							}
							else if (dataType.equals(new String("http://www.w3.org/2001/XMLSchema#int"))||dataType.equals(new String("http://www.w3.org/2001/XMLSchema#double"))) {
								//System.out.println("primo "+c.getFirst() +"secondo" + c.getSecond()) ;
								accCom+= this.similarityTool.dataLayer.simNumber(Float.parseFloat(c.getFirst().substring(0, c.getFirst().indexOf("^^"))),Float.parseFloat(c.getSecond().substring(0, c.getSecond().indexOf("^^"))));
							}
							else if (dataType.equals(new String("boolean"))|| dataType.equals(new String("http://www.w3.org/2001/XMLSchema#boolean"))) {
								accCom+= this.similarityTool.dataLayer.simString(((c.getFirst())),(c.getSecond()));
							} else throw new Exception("the attribute "+ att+ " results to have  an uncosidered datatype " + dataType);												
						}					
						//for each way the similarity has to be worked out to be remembered 
						if (accMax< accCom) accMax= accCom;
						accCom=0;
					}
					//									GC
					aa=null;
					// once the maximum similarity has been worked out it has to be added to the accumulator
					//float zero=new Float(0);
					part=accMax *(1- Math.max(new Float(0),(float) (fvalue.size() - svalue.size()))/fvalue.size());
					acc+=part*(weight/100);
				}else throw new Exception("the criteria " +  criteria + " indicated for the attribute "+ att+ " in the context is not well defined");

				// PrintF to have the partial values 
				if (Debug.printDebug)System.out.println("Attr "+ att +" val " +part +" criteria " + criteria +" weight "+ weight);	
				sr.put(firstInstance, secondInstance, path, att,criteria, part);
			}

			/*	else{ // if the similarity to be worked out is symmetric 

					//TODO Complete the extensional part  in the case of simmetric similarity   
					if (criteria.equals("Count")){ 

					}
					else if (criteria.equals("Inter")) {

					} 
					else if (criteria.equals("Simil")) {


					} 
				}
			}*/

			// Let us to compare the related instances 
			// to retrieve the values the instances have for such a attribute ..
			// let us compare the attribute values for the instances
			while (relIter.hasNext()){
				String rel=(String) relIter.next();
				//let us know the type of criteria associated to the relation in the context
				String criteria=similarityTool.contextLayer.getRelationCriterion(rel,path);
				// let us know the weight of relation
				float weight=  similarityTool.contextLayer.getRelWeight(rel,path);

//				// v1.2 before optimization set comparison
//								ArrayList <String> fvalue= ontologyModel.getRelationValues(firstInstance,rel);
//								ArrayList <String> svalue= ontologyModel.getRelationValues(secondInstance,rel);
//			
				// v1.2 after optimization set comparison
				HashSet <String> fvalue= new HashSet <String> ( ontologyModel.getRelationValues(firstInstance,rel));
				HashSet <String> svalue= new HashSet <String>(ontologyModel.getRelationValues(secondInstance,rel));

				double partR =0;

				/*if (!(symmetrical)) { */

				if (criteria.equals("Count")||((svalue.size()==0)||(fvalue.size()==0))){ 
					//double fcount=fvalue.size();
					//double scount=svalue.size();
					double fcount=fvalue.size();
					double scount=svalue.size();

					if ((criteria.equals("Count"))&&((fcount!=0)||(scount!=0))){
						partR= Math.abs((double) scount/ (double)Math.max(fcount,scount));
						acc+=partR*(weight/100);
					}else{
						acc+=0; 
					}


				}
				else if (criteria.equals("Inter")){
					// let us find out which is the intersection.. 

					ArrayList <String> intersect= new ArrayList<String>();

//					// v1.2 before optimization set comparison
//					// this is  to work out the intersection..
//										for(Iterator <String>si = svalue.iterator(); si.hasNext();){
//											String so =si.next();
//											for(Iterator<String> fi = fvalue.iterator(); fi.hasNext();){
//												String fo =fi.next();
//												if (fo.equals(so)) {
//													intersect.add(fo); //FIXIT to verify which kind of equality is  adopted here?
//													//break;
//												}
//											}
//										}

					// v1.2 after optimization set comparison
					for(Iterator <String> fi = fvalue.iterator(); fi.hasNext();){
						String fo =fi.next();
						if (svalue.contains(fo)) {
							intersect.add(fo); //FIXIT to verify which kind of equality is  adopted here?
						}
					}					


					partR=((double)intersect.size()/(double)fvalue.size());
					acc+=partR*(weight/100);
				} 
				else if (criteria.equals("Simil")) {

					// the similarity function is recursive 
					// let determine if there are more instances related 
					if (!(((ontologyModel.getRelationValues(firstInstance,rel)).size()<2)&&(ontologyModel.getRelationValues(secondInstance,rel)).size()<2)){
						// if so the possible coupling among the two set of instances
						double accMax=0, accCom=0;
						
//						// v1.2 before optimization set comparison
//						ArrayList <ArrayList<Couple<String>>> aa= DataLayer.generateSetAccomplishement(fvalue,svalue, new ArrayList<Integer>(),new ArrayList<Integer>()); 
//						
						// v1.2 after optimization set comparison
						ArrayList <String> afvalue=new ArrayList <String>(fvalue);
						ArrayList <String> asvalue=new ArrayList <String>(svalue);
						ArrayList <ArrayList<Couple<String>>> aa= DataLayer.generateSetAccomplishement(afvalue,asvalue, new ArrayList<Integer>(),new ArrayList<Integer>()); 

						Double val;
						int count =0;
						for(Iterator <ArrayList<Couple<String>>> i=aa.iterator(); i.hasNext(); ){
							ArrayList <Couple<String>> a=i.next();
							count++;
							//System.out.println();
							for(Iterator <Couple<String>> ii=a.iterator(); ii.hasNext();  ){
								Couple<String>c=ii.next();

								//									//caching for V1.2
								if (Debug.RecursiveSimilarityCaching){
									double csim=cache.isInCache(path+"."+rel, "Simil", c.getFirst(), c.getSecond());
									if (csim<0){
										// FIXIT when we are in recursion sr have the label from the kind of object we came from so the partial value cannot be stored 
										csim=this.extensionalSimilarity((c.getFirst()).toString(),(c.getSecond()).toString(), /*symmetrical, */path+"."+rel, sr);
										cache.putInCache(path+"."+rel, "Simil", c.getFirst(), c.getSecond(), csim);
									}
									val=csim/Math.min(fvalue.size(),svalue.size());
								}else
								{
									// before caching in V1.2
									val=(this.extensionalSimilarity((c.getFirst()).toString(),(c.getSecond()).toString()/*,symmetrical*/,path+"."+rel, sr))/Math.min(fvalue.size(),svalue.size());
								}
								accCom+=val;		
								if (Debug.printDebug)System.out.println( "First:"+ (c.getFirst()) + " Second " +(c.getSecond())+" = " +val);
							}					
							//for each way the similarity has to be worked out to be remembered 
							if (accMax< accCom) accMax= accCom;
							accCom=0;
						}

						// once the maximum similarity has been worked out it has to be added to the accumulator

						if (Debug.printDebug)System.out.println("accMax:  " + accMax);
						partR=accMax *(1- Math.max(new Float(0),(float) (fvalue.size() - svalue.size()))/fvalue.size());
						acc+=partR*(weight/100);

						//										GC
						aa=null;
					}
				}else throw new Exception("the criteria " +  criteria + " indicated for the attribute "+ rel+ " in the context is not well defined");



				// FIXIT at the end of if-else chain should be added this else throw new Exception("the criteria " +  criteria + " indicated for the attribute "+ rel+ " in the context is not well defined");

				if (Debug.printDebug)System.out.println("Rel "+ rel +" val " +partR +" criteria " + criteria + " weight " + weight);	
				sr.put(firstInstance, secondInstance, path, rel, criteria, partR);
			}
			/*else{ // if the similarity to be worked out is symmetric 

					//TODO Complete the extensional part  in the case of symmetric similarity   
					if (criteria.equals("Count")){ 

					}
					else if (criteria.equals("Inter")) {

					} 
					else if (criteria.equals("Simil")) {

					} 
				}	
			}*/

			// then we accumulate the result obtained dividing them for the number of attributes and relation we have considered in the context.	

			//int com=((slot.get(0)).size()+(slot.get(1)).size());
			if ((slot.get(0)).size()+(slot.get(1)).size()==0)  throw new Exception("the context for the path "+ path+ " is not well-defined ");

			//return (double) acc/(double)((slot.get(0)).size()+(slot.get(1)).size());
			return (double) acc;
		}
	}






	/** 
	 * @uml.property name="similarityTool"
	 * @uml.associationEnd inverse="ontologyLayer:similarity.SimilarityTool"
	 */
	public SimilarityTool similarityTool;
	/** 
	 * @uml.property name="ontologyModel"
	 * @uml.associationEnd aggregation="composite" inverse="ontologyLayer:similarity.OntologyModel"
	 */
	protected OntologyModel ontologyModel;

	/**
	 * Cache to limit the comparison when using recursion
	 */
	private ComparisonCache <String> cache;



	/** 
	 * Getter of the property <tt>ontologyModel</tt>
	 * @return  Returns the ontologyModel.
	 * @uml.property  name="ontologyModel"
	 */
	public OntologyModel getOntologyModel() {
		return ontologyModel;
	}





	//							/** 
	//							 * Setter of the property <tt>ontologyModel</tt>
	//							 * @param ontologyModel  The ontologyModel to set.
	//							 * @uml.property  name="ontologyModel"
	//							 */
	//							public void setOntologyModel(OntologyModelSparql ontologyModel) {
	//								this.ontologyModel = ontologyModel;
	//							}






	/**
	 * @param sr 
	 * @throws Exception 
	 */
	public void simAsym(String firstInstance, String secondInstance, SimilarityResultWithPartials sr) throws Exception{
		double result;

		//FIXIT to make it faster we should divide the part pertaing the symmetrical similarity from the  rest
		//to find out the lub for the instances 
		//NOLUBVERSION
		/*
									ArrayList <String> classesForFirst= ontologyModel.getClassesForInstance(firstInstance);
									ArrayList <String> classesForSecond= ontologyModel.getClassesForInstance(secondInstance);
		 */
		//ONTOMODEL to handle the multiple class type for instances - if there are more than one element in the intersection then the instances have multiple classes they belong to 

		//NOLUBVERSION	
		String simTemplate="http://www.w3.org/2002/07/owl#Thing";
		//NOLUBVERSION	
		/*
									String	first = (classesForFirst.iterator()).next(),
											second = (classesForSecond.iterator()).next();

									if ((classesForFirst.size()>1)||(classesForSecond.size()>1)) throw new Exception("Multiple class type for instances");

									// By working out the lub, it is possible to realize which is the class having attribute and relation common to both the instances
									simTemplate = ontologyModel.lub(first, second);
		 */
		ContextLayer c =this.similarityTool.getContextLayer();

		double extern=0;
		// if the external similarity is weigthed as 0  id does not make sense to work out it 
		//if (c.wExternalSim>0) extern=externalSimilarity(firstInstance,secondInstance,false);

		double extens=0;
		//									 if the extensional similarity is weigthed as 0  id does not make sense to work out it 
		if (c.wExtensionalSim>0) extens=	extensionalSimilarity(firstInstance,secondInstance,/*false,*/simTemplate, sr);


		if (Debug.printDebug) System.out.println(" External Similiarity between ("+ firstInstance+","+secondInstance+") "+ extern);
		if (Debug.printDebug) System.out.println(" Extensional Similiarity between ("+ firstInstance+","+secondInstance+") "+ extens);


		result= ((double)((c.wExternalSim)*(extern)+((c.wExtensionalSim*extens)))/((double)(c.wExtensionalSim+c.wExternalSim)));
		//result=(externalSimilarity(firstInstance,secondInstance,false)+this.extensionalSimilarity(firstInstance,secondInstance,false,simTemplate))/2;
		//CG
		//NOLUBVERSION
		/*
									classesForFirst=null;
									classesForSecond=null;
		 */
		//return result;
		sr.put(firstInstance, secondInstance, result);
	}








}
