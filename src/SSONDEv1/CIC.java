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
 * The acronimous means Context Images Couple  It represent a couple const of a relation or attribute name and the criteria associated.
 */
public class CIC {

	/**
	 * @uml.property  name="slot"
	 */
	private String slot = "";

	/**
	 * Getter of the property <tt>slot</tt>
	 * @return  Returns the slot.
	 * @uml.property  name="slot"
	 */
	public String getSlot() {
		return slot;
	}

	/**
	 * Setter of the property <tt>slot</tt>
	 * @param slot  The slot to set.
	 * @uml.property  name="slot"
	 */
	public void setSlot(String slot) {
		this.slot = slot;
	}

	/**
	 * @uml.property  name="criterion"
	 */
	private String criterion;

	/** 
	 * Getter of the property <tt>Criteria</tt>
	 * @return  Returns the criteria.
	 * @uml.property  name="criterion"
	 */
	public String getCriterion() {
		return criterion;
	}

	/** 
	 * Setter of the property <tt>Criteria</tt>
	 * @param Criteria  The criteria to set.
	 * @uml.property  name="criterion"
	 */
	public void setCriterion(String criterion) {
		this.criterion = criterion;
	}

		
		
		public CIC(String slot, String criterion, float weight){
			this.slot=slot;
			this.criterion=criterion;
			this.weight=weight;
			}

		/**
		 * The importance which have each attribute or relation.
		 * @uml.property  name="weight"
		 */
		private float weight = 0;

		/**
		 * Getter of the property <tt>weight</tt>
		 * @return  Returns the weight.
		 * @uml.property  name="weight"
		 */
		public float getWeight() {
			return weight;
		}

		/**
		 * Setter of the property <tt>weight</tt>
		 * @param weight  The weight to set.
		 * @uml.property  name="weight"
		 */
		public void setWeight(float weight) {
			this.weight = weight;
		}

}
