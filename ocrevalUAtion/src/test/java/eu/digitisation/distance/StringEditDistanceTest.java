/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General  License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.distance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.digitisation.math.BiCounter;
import eu.digitisation.text.StringNormalizer;

class StringEditDistanceTest {

	/**
	 * Test of Inversion of Indel Distance method, of class StringEditDistance.
	 */
	@Test
	void testIndelDistance() {
		String first = "patata";
		String second = "apta";
		int expResult = 4;
		int result = StringEditDistance.indel(first, second);
		assertThat(result).isEqualTo(expResult);

	}

	/**
	 * Test of Levenshtein Distance method, of class StringEditDistance.
	 */
	@Test
	void testLevenshteinDistance() {
		System.out.println("levenshteinDistance");
		String first = "patata";
		String second = "apta";
		int expResult = 3;
		int result = StringEditDistance.levenshtein(first, second);
		assertThat(result).isEqualTo(expResult);
		// A second test
		first = "holanda";
		second = "wordland";
		result = StringEditDistance.levenshtein(first, second);
		assertThat(result).isEqualTo(4);
		// Test with normalization
		first = StringNormalizer.reduceWS("Mi enhorabuena");
		second = StringNormalizer.reduceWS("mi en  hora  buena");
		result = StringEditDistance.levenshtein(first, second);
		assertThat(result).isEqualTo(3);
	}

	/**
	 * Test of DL method, of class StringEditDistance.
	 */
	@Test
	void testDLDistance() {
		System.out.println("Damerau-Levenshtein Distance");
		String first = "abracadabra";
		String second = "arbadacarba";
		int expResult = 4;
		int result = StringEditDistance.DL(first, second);
		assertThat(result).isEqualTo(expResult);

	}

	@Test
	void testOperations() {
		System.out.println("operations");
		String first = "patata";
		String second = "apta";
		BiCounter<Character, EdOp> expResult = new BiCounter<Character, EdOp>();
		expResult.add('a', EdOp.KEEP, 2); // sure
		expResult.inc('t', EdOp.KEEP); // sure
		expResult.inc('p', EdOp.DELETE); // sure
		expResult.inc('t', EdOp.SUBSTITUTE); // not the ony pssibility
		expResult.inc('a', EdOp.DELETE); // could exchange with 'a' above

		BiCounter<Character, EdOp> result = StringEditDistance.operations(first, second);

		assertThat(result).isEqualTo(expResult);
	}
}
