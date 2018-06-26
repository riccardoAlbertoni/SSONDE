

# GETTING STARTED USING SSONDE
SSONDE is a framework to assess context dependent and asymmetric similarity on entities exposed on the linked data. It is developed in Java relying on JENA library. 
It can be exploited either as a stand alone Java application running  *SSONDEv1.SemSim* or as a library developing third parties application. Below the former is discussed and  data is  expected to be available in a RDF dump or in one of the supported JENA store. Further information on how load data in SSONDE are available  at [How to download the RDF statements](http://code.google.com/p/ssonde/wiki/RDF_statements_download).

  *  [Using SSONDE for the first time](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#Using_SSONDE_for_the_first_time)
     * [Software requirements](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#GettingStarted#Software_requirements)
     * [Downloading](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#Downloading)
     * [How to run SSONDE](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#How_to_run_SSONDE) 
  * [How to write a configuration file for SSONDE](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#How_to_write_a_configuration_file_for_SSONDE)
     * [JSON Configuration file](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#JSON_Configuration_file) 
     * [How to specify a context](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#How_to_specify_a_context)
     * [Output formats](https://github.com/riccardoAlbertoni/ssonde/blob/master/README.md#Output_formats)

## Using SSONDE for the first time

### Software requirements 
The semantic similarity is developed in Java. In order to run it, the Java Run Time 1.6.0 or the Java Development Kit (JDK) must be installed (download Java at http://www.java.com/en/download/index.jsp).

### Downloading 
The easiest way to get the SSONDE code is through SVN typing 

    svn checkout http://ssonde.googlecode.com/svn/SSONDEv1 SSONDEv1 

In that way, you get 4GB including some JENA datastores for testing the code.


### How to run SSONDE
SSONDE can be deployed by executing from the main directory *SSONDEv1*, the command line
 

    (UNIX like command line)
    java   -classpath ./lib/*:./bin SSONDEv1.SemSim –conf JSONconfigurationFile  
    
    (DOS/WINDOWS like command line)
    java   -classpath .\lib\*;.\bin SSONDEv1.SemSim –conf JSONconfigurationFile 

where JSONconfigurationFile is a configuration file defined according to . When more than a configuration file is specified including two or more –conf JSONConfigurationFile, the semantic similarity is run once for each of them. 

##### Example 2: SSONDE deployment
The following command runs the SSONDE semantic similarity to compare linked data about CNR-IMATI researchers according to the JSON configuration which corresponds to the comparison of researcher with respect to their research interests.


    (UNIX like command line)
    java -classpath ./lib/*:./bin/  SSONDEv1.SemSim -conf  conf/dataCNRIt/ComplexContextResearchInterest/CCRIIntPub.param.json
    
    (DOS\WINDOWS like command line)
    java -classpath .\lib\*;.\bin SSONDEv1.SemSim -conf conf/dataCNRIt/ComplexContextResearchInterest/CCRIIntPub.param.json


Results are stored as csv in conf/dataCNRIt/ComplexContextResearchInterest/CCRIIntPub.res.cvs

#### Example 2bis: SSONDE deployment
The following command runs the SSONDE semantic similarity to compare linked data about “habitat” according to the JSON configuration which corresponds to the comparison of habitat with respect to associated species as described in the paper ([http://code.google.com/p/ssonde/wiki/GettingStarted#Albertoni_&_De_Martino,_2011  Albertoni & De Martino, 2011]).


    (UNIX like command line)
    java -classpath ./lib/*:./bin/  SSONDEv1.SemSim -conf conf/EChallenges2011/IMATIEUNISHabitatWRTSpeciesEchalleges2011.json
    
    (DOS\WINDOWS like command line)
    java -classpath .\lib\*;.\bin SSONDEv1.SemSim -conf
    conf\EChallenges2011\IMATIEUNISHabitatWRTSpeciesEchalleges2011.json


Results are stored as csv in conf/EChallenges2011/skosHabitatWRTSpeciesContext.cvs 


## How to write a configuration file for SSONDE
All the options pertaining to the configuration of SSONDE must be specified into a [http://www.json.org/ JSON file]  configuration file.

### JSON Configuration file
The configuration file must be  made of the following JSON objects:
 * `StoreConfiguration` which specifies where to read the RDF data;
 * `ContextConfiguration` which specifies the context that similarity  must apply;
 * `InstanceConfiguration` which specifies the list of instances, namely the instances' URI, on which the similarity must be worked out, alternatively, it might specify a piece of Java code to generate the instances' URI list;
 * `OutputConfiguration` which specifies where and how the semantic similarity result must be stored.

Each configuration file has the following four JSON Objects and looks in the shape:


    {
       "StoreConfiguration":{
          …………
       },
       "ContextConfiguration":{
          …………
       },
       "InstanceConfiguration":{
          …………
       },
       "OutputConfiguration":{
    	…………
       }
    }


Let’s see how to complete each of the above JSON Object:

  * `StoreConfiguration` is a JSON Object, which includes the following keys:
    * `KindOfStore`, which specifies the kinds of store: 
      * `JENAMem` similarity works on a memory JENA model, It must specify:
        * `RDFDocumentURIs` array of URIs as string, list of vocabularies schema or similar that have to be included (it can also be empty). Consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced. Instance URIs have to be listed here if they have to be dereferenced;
      * `JENAMemReasoner` similarity works on a memory JENA model with JENA reasoned. It must specify the same info of JENAMem plus 
        * `JENARulesURI` a filePath as string, optional, file containing the JENA rule to be used (it can be also empty);
      * `JENATDB` similarity works on a TDB JENA store.  It must specify:
        * `RDFDocumentURIs` array of URIs as string, list of vocabularies schema or similar that have to be included (it can also be empty. Consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced. Instance URIs have to be listed here if they have to be dereferenced;
        * `TDBDirectory` a directory path as string, mandatory, the directory where the TDB is stored;
      * `JENATDBReasoner` similarity works on a TDB JENA store with JENA reasoner, beside the options defined in JENATDB. It must specify:
        * `JENARulesURI` a string, optional, file containing the JENA rule to be used (it can be also empty);
      * `JENASDB` similarity works on a SDB JENA model. It must specify:
        * `RDFDocumentURIs` array of URIs as string, list of vocabularies schema or similar that have to be included (it can also be empty). Consider that URIs of vocabularies indicated as prefixes in contexts are automatically dereferenced. Instance URIs have to be listed here if they have to be dereferenced;
        * `JENASDBjdbcURL` a string indicating the JDBC url;
        * `JENASDBuser` a string indicating the user to the DB;
        * `JENASDBpsw` a string indicating the password to the DB; 
        * `JENASDBType` a string indicating the kind of Db e.g. PostgreSQL;
        * `ListOfModels` a JSON array containing the URIs of the models to be considered in the store;
      * `JENASDBReasoner` similarity works on a SDB JENA model with JENA reasoning, beside the options indicated for JENASDB, It must specify:
        * `JENARulesURI` a filePath as string, optional, file containing the JENA rule we want to use, it can be also empty.

  * `ContextConfiguration` is a JSON Object specifying the !context on which the semantic similarity is assessed. It must specify:
    * `ContextFilePath`: the file path as string pointing to the text file as defined in the next Section [GettingStarted#How_to_specify_a_context "How to specify a context"]

  * `InstanceConfiguration` is a JSON Object specifying one of the following:
    * `InstanceURIs`, JSON array which contains the list of instances URI that must be assessed by similarity;
    * `InstanceURIsClass`, specify the class, which implements the !ListOfInputInstances interface and the abstract method !ArrayList getListOfInstanceURIs(), to generate from the repository the list of instances on which to assess the similarity. 

  * `OutputConfiguration` is a JSON Object specifying the string (!KindOfOutput) to store the result. 
    * `KindOfOutput` is one of the string among the following options: 
      * `CVSFile`, it writes the results as a similarity matrix in a CVS file; it must be specified the 
        * `FilePath`: string, the CVS file path.
      * `JSONOrderedResult`, it writes the most similar results for each input instance as a JSON file; it must be specified:
        * `NumberOfOrderedResult`, integer, "n" is the number of most similar instances to be written in the JSON file for each instance, and it must be minor of the number of the compared instances “m”;
        * `FilePath`, string, the JSON file path.

#### Example1 of configuration file
An example of configuration file, where the semantic similarity is:
  * reading from a TDB store (data/EUNISIMATI/TDB-0.8.9/); 
  * loading some RDF schemas, "http://www.w3.org/2004/02/skos/core#"; 
  * assessing the similarity according to the context defined in conf/EChallenges2011/skosHabitatWRTSpeciesContext.ctx;
  * The output results are written in the CVS file conf/EChallenges2011/skosHabitatWRTSpeciesContext.cvs.
     

	     { 
	          "StoreConfiguration":{
	          "KindOfStore":"JENATDB",
	          "RDFDocumentURIs":[
	             "http://www.w3.org/2004/02/skos/core#",
	             "http://www.w3.org/2000/01/rdf-schema#",
	             "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	             "http://www.w3.org/2002/07/owl#"
	          ],
	          "TDBDirectory":"data/EUNISIMATI/TDB-0.8.9/"
	          },
	            "ContextConfiguration":{
	                "ContextFilePath":"conf/EChallenges2011/skosHabitatWRTSpeciesContext.ctx"
	             },
	           "InstanceConfiguration":{
	              "InstanceURIsClass":"application.EChallenges2011.InstancesForEUNISSimilarityEchallenges"
	            },
	          "OutputConfiguration":{
	             "KindOfOutput":"CVSFile",
	             "FilePath":"conf/EChallenges2011/skosHabitatWRTSpeciesContext.cvs"
	           }
	        }


### How to specify a context
The context specification text pointed by the !ContextFilePath  in the Jason Object !ContextConfiguration is specified according to the format introduced in ([http://code.google.com/p/ssonde/wiki/GettingStarted#Albertoni_&_De_Martino,_2008 Albertoni & De Martino, 2008]), and adapted for considering multiples RDF/OWL vocabularies in [http://code.google.com/p/ssonde/wiki/GettingStarted#Albertoni_&_De_Martino,_2010 Albertoni & De Martino, 2010]. 
A context in a text file is shaped as in the following:

    PREFIX namespaceA: <urlA>
    PREFIX namespaceB: <urlB>
    PREFIX namespaceC: <urlC>
    [owl:Thing]->{ {(namespace:attribute1,operationForAttribute1), …,(namespace:attributeN, operationForAttributeN)}, {(namespace:relation1,operationForRelation1),…,(namespace:relationM, operationForRelationM)}}

If a relation is recursive, namely Simil, it is selected as operation for the instance comparison, (e.g., _( xxx:yyy, Simil)_), the context specification must provide information about the entities to be considered during the similarity assessment among the instances reachable by xxx:yyy. That is done by adding the recursive path _owl:Thing→xxx:yyy_  and listing its criteria as shown in the following excerpt:


    [owl:Thing, xxx:yyy]->{ {(namespace:attribute1, operationForAttribute1), namespace:attributeN, operationForAttributeN)}… , {(namespace:relation1, operationForRelation1),…,( namespace:relationM, operationForRelationM)}}

#### Example 3 of context specification

     PREFIX res: <http://example.org/>
     PREFIX owl: <http://www.w3.org/2002/07/owl#>
     [owl:Thing]->{{},{(res:publication,Inter),(res:workAtProject,Inter),(res:researchInterest,Simil)}}
     [owl:Thing, res:researchInterest]->{{(res:topicName,Inter)},{( res:relatedTopic,Inter)}}

Context without namespaces can be defined (even if the definition of owl namespace is recommended) in that case the previous example becomes:

#### Example 4 of context specification

    PREFIX res: <http://example.org/>
    PREFIX owl: <http://www.w3.org/2002/07/owl#>
    [owl:Thing]->{{},{( http://example.org/publication,Inter),
    (http://example.org/workAtProject,Inter),( http://example.org/researchInterest,Simil)}}
    [owl:Thing, http://example.org/researchInterest]->{{( http://example.org/topicName,Inter)},{( http://example.org/relatedTopic,Inter)}}

### Output formats

The output formats supported are CVS file and JSON. 
 * When *CVS File* is selected in the configuration file a similarity matrix is produced as CVS, the first two columns represent the instance URIs (namespace and identifier respectively) and the similarity values follow in the other columns.
 * When *JSONOrderedResult* is selected the resulting JSON is in the following form

    [
       {
          "URI":"uriA1"      //first parameter in the similarity assessment  Sim(uriA1,
          uri??)
      "Result":[
             {
                "Val":"1",
                //double between 0 and 1 representing the similarity Sim(uriA1,
                uriB1) result 
    	     "URI":"uriB1"            //uriB1 is the most similar instance to  
    	                     uriA1
             },
             ...         {
                "Val":0.0945,
                //double between 0 and 1 representing the       
    	                     similarity Sim(uriA1,
                uriBn) result
    	     "URI":"uriBn"            //uriBn is the n most similar instance to
    	                      uriA
             }
          ],
    
       },
       ....   {
          "URI":"uriAm” //first parameter in the similarity 
                      assessment Sim(uriAm,uri??)
      "      Result":[
             {
                "Val":"1",
                //double between 0 and 1 representing the
                       similarity Sim(uriAm,
                uriC1) result 
         "URI":"uriC1"            //uriC1 is the most similar instance to  
                         uriaAm
             },
             ...         {
                "Val":0.0945,
                //double between 0 and 1 representing the       
                         similarity Sim(uriAm,
                uriCn) result
         "URI":"uribBn"            //uriBn is the n most similar instance to
                          uriAm
             }
          ],
    
       },
    
    ]

After preparing the configuration file, it is always advisable to validate its syntactical correctness by using one of the services available on internet (an example of JSON validator/formatter is available at http://jsonformatter.curiousconcept.com/.

## References 

### Albertoni & De Martino, 2008 
Albertoni, R., & De Martino, M. (2008). _Asymmetric and context-dependent semantic similarity among ontology instances_, Journal on Data Semantics X. (S. Spaccapietra, Ed.) J. Data Semantics (Vol. 4900, pp. 1-30). Berlin, Heidelberg: Springer Berlin Heidelberg. doi:10.1007/978-3-540-77688-8 [preprint](http://www.ima.ge.cnr.it/ima/personal/albertoni/PersonalPage/src/JodsX.pdf)

### Albertoni & De Martino, 2010 
Albertoni, R., & De Martino, M. (2010). _Semantic Similarity and Selection of Resources Published According to Linked Data Best Practice_, On the Move to Meaningful Internet Systems: OTM 2010 Workshops. (R. Meersman, T. Dillon, & P. Herrero, Eds.)OTM Workshops (Vol. 6428, pp. 378-383). Berlin, Heidelberg: Springer Berlin Heidelberg. doi:10.1007/978-3-642-16961-8  [preprint](http://saturno.ge.imati.cnr.it/ima/personal/albertoni/PersonalPage/src/OntoContent2010.pdf)

### Albertoni & De Martino, 2011
Albertoni, R., & De Martino, M. (2011). _Semantic Technology to Exploit Digital Content Exposed as Linked Data_. In P. Cunningham & M. Cunningham (Eds.), eChallenges e-2011 (pp. 1-8). IIMC International Information Management Corporation, 2011 ISBN: 978-1-905824-27-4.








