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



public class Relation {

	/**
	 * @uml.property  name="domain"
	 */
	private String domain = "";

	/**
	 * Getter of the property <tt>domain</tt>
	 * @return  Returns the domain.
	 * @uml.property  name="domain"
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Setter of the property <tt>domain</tt>
	 * @param domain  The domain to set.
	 * @uml.property  name="domain"
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/** 
	 * Getter of the property <tt>Range</tt>
	 * @return  Returns the range.
	 * @uml.property  name="range"
	 */
	public String getRange() {
		return range;
	}

	/** 
	 * Setter of the property <tt>Range</tt>
	 * @param Range  The range to set.
	 * @uml.property  name="range"
	 */
	public void setRange(String range) {
		this.range = range;
	}

	/**
	 * @uml.property  name="range"
	 */
	private String range="";
	/**
	 * @uml.property  name="relationName"
	 */
	private String relationName = "";

	/**
	 * Getter of the property <tt>relationName</tt>
	 * @return  Returns the relationName.
	 * @uml.property  name="relationName"
	 */
	public String getRelationName() {
		return relationName;
	}

	/**
	 * Setter of the property <tt>relationName</tt>
	 * @param relationName  The relationName to set.
	 * @uml.property  name="relationName"
	 */
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

		
			
			
				
				/** 
				 * @param r relation range
				 * @param n relation name 
				 * @param d relation domain
				 */
				public Relation(String d, String n, String r){
					domain = d;
					relationName = n;
					range = r;
				}

}
