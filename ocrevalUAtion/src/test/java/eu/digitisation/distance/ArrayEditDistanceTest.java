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

class ArrayEditDistanceTest {

	/**
	 * Test of Inversion Indel Distance method
	 */
	@Test
	void testIndelDistance() {
		System.out.println("indelDistance");
		Object[] first = { 'p', 'a', 't', 'a', 't', 'a' };
		Object[] second = { 'a', 'p', 't', 'a' };
		int expResult = 4;
		int result = ArrayEditDistance.indel(first, second);
		assertThat(result).isEqualTo(expResult);
	}

	/**
	 * Test of Levenshtein Distance method
	 */
	@Test
	void testLevenshteinDistance() {
		Object[] first = { 'p', 'a', 't', 'a', 't', 'a' };
		Object[] second = { 'a', 'p', 't', 'a' };
		int expResult = 3;
		int result = ArrayEditDistance.levenshtein(first, second);
		assertThat(result).isEqualTo(expResult);
	}

}
