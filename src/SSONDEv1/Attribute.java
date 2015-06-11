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


public class Attribute {

	
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
	 * @uml.property  name="datatype"
	 */
	private String datatype = "";

	/**
	 * Getter of the property <tt>datatype</tt>
	 * @return  Returns the datatype.
	 * @uml.property  name="datatype"
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * Setter of the property <tt>datatype</tt>
	 * @param datatype  The datatype to set.
	 * @uml.property  name="datatype"
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

		
		/**
		 */
		public Attribute(String domain, String datatype){
			this.domain=domain;
			this.datatype=datatype;
		
		}

		/**
		 * @uml.property  name="attibuteName"
		 */
		private String attibuteName = "";

		/**
		 * Getter of the property <tt>attibuteName</tt>
		 * @return  Returns the attibuteName.
		 * @uml.property  name="attibuteName"
		 */
		public String getAttibuteName() {
			return attibuteName;
		}

		/**
		 * Setter of the property <tt>attibuteName</tt>
		 * @param attibuteName  The attibuteName to set.
		 * @uml.property  name="attibuteName"
		 */
		public void setAttibuteName(String attibuteName) {
			this.attibuteName = attibuteName;
		}

}
