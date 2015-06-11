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


public class Couple <T> {
		
		/**
		 */
		public Couple(T first,T second){
			this.first=first;
			this.second=second;
		}

		/**
		 * @uml.property  name="first"
		 */
		private T first;

		/**
		 * Getter of the property <tt>first</tt>
		 * @return  Returns the first.
		 * @uml.property  name="first"
		 */
		public  T getFirst() {
			return first;
		}

		/**
		 * Setter of the property <tt>first</tt>
		 * @param first  The first to set.
		 * @uml.property  name="first"
		 */
		public void setFirst(T first) {
			this.first = first;
		}

		/**
		 * @uml.property  name="second"
		 */
		private T second;

		/**
		 * Getter of the property <tt>second</tt>
		 * @return  Returns the second.
		 * @uml.property  name="second"
		 */
		public T getSecond() {
			return second;
		}

		/**
		 * Setter of the property <tt>second</tt>
		 * @param second  The second to set.
		 * @uml.property  name="second"
		 */
		public void setSecond(T second) {
			this.second = second;
		}
	

}

