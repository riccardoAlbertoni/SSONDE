package DataFunction;

import SSONDEv1.DataSimilarity;

/*Example extracted as test for testing the pluggin mechanism. 
 * That measure is extracted from
 * $Id: StringDistances.java 126 2012-07-17 07:30:13Z euzenat $
 *
 * Copyright (C) INRIA, 2003-2007, 2009
 * Except for the Levenshtein class whose copyright is not claimed to
 * our knowledge.
 * Copyright (C) University of Montr�al, 2004-2005 for the tokenizer
 * This program was originaly part of the Alignment API implementation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */
public class JaroMeasure implements DataSimilarity <String>  {
	/*
     * jaroMeasure as a dissimilarity (identical have 0.)
     * return: 
     * Original algorithm by J�r�me Euzenat.
     * It traverses both strings at the same time
     * finding the first match on s, then looking for the first on t
     * (and deciding if there is transposition or not on the fly)
     * before comming back to "s", etc.
     * This is certainly minimal in lines of code if not optimal
     */
    public  double sim (String s, String t) {
	if (s == null || t == null) {
	    //throw new IllegalArgumentException("Strings must not be null");
	    return 1.;
	}
	int l1 = s.length(); // length of s
	int l2 = t.length(); // length of t
	int span = Math.min(l1, l2)/2; // vicinity of search
	int i = 0; // index on s
	int j = 0; // index on t
	int comps = 0; // nb of char in s, close in t
	int compt = 0; // nb of char in t, close in s
	int transp = 0; // nb of char NOT transposed
	             // i.e., nb of char in s appearing in the same order in t
	char lastchars = '\0'; // last matched char in s
	while( i < l1 || j < l2 ){
	    if ( ( j < l2 && comps > compt ) || i > l1 ){
		// find a new match in compt
		for ( int k = Math.max(0,j-span); k < Math.min(l1,j+span); k++){
		    if ( t.charAt(j) == s.charAt(k) ){
			compt++;
			if ( t.charAt(j) == lastchars ) transp++;
			k = l1;
		    }
		}
		j++;
	    } else if ( i == l1 ) { // end of s
		lastchars = '\0'; // avoid matching with it
		i = l1+1; // so we will go to the previous clause now
	    } else { // comps
		for ( int k = Math.max(0,i-span); k < Math.min(l2,i+span); k++){
		    if ( t.charAt(k) == s.charAt(i) ){
			comps++;
			lastchars = s.charAt(i);
			k = l2;
		    }
		}
		i++;
	    }
	}
	if ( comps == 0. ) return 1.;
	else return 1- (1.0 - ((double)comps/l1 + (double)compt/l2 + (double)transp/comps)/3);
    }

}
