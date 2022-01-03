/*
 * Copyright (C) 2014 Universidad de Alicante
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

import eu.digitisation.text.StringNormalizer;

class OcrOpWeightTest {

	/**
	 * Test of sub method, of class OcrWeights.
	 */
	@Test
	void testSub() {		
		char[] c1 = { 'Á', 'Á', 'Á', 'Á', 'Á' };
		char[] c2 = { 'Á', 'A', 'á', 'a', ' ' };
		int[] w1 = { 0, 1, 1, 1, 2 };
		OcrOpWeight W1 = new OcrOpWeight(); // fully-sensitive
		for (int n = 0; n < w1.length; ++n) {
			String s1 = StringNormalizer.canonical(String.valueOf(c1[n]), false, false, false);
			String s2 = StringNormalizer.canonical(String.valueOf(c2[n]), false, false, false);
			int d = EditDistance.charDistance(s1, s2, W1, 10);
			assertThat(d).isEqualTo(w1[n]);
		}
		OcrOpWeight W2 = new OcrOpWeight(true); // ignore everything
		int[] w2 = { 0, 0, 0, 0, 2 };
		for (int n = 0; n < w2.length; ++n) {
			String s1 = StringNormalizer.canonical(String.valueOf(c1[n]), true, true, true);
			String s2 = StringNormalizer.canonical(String.valueOf(c2[n]), true, true, true);
			int d = EditDistance.charDistance(s1, s2, W2, 10);
			assertThat(d).isEqualTo(w2[n]);
		}
		OcrOpWeight W3 = new OcrOpWeight(false); // ignore diacritics
		int[] w3 = { 0, 0, 1, 1, 2 };
		for (int n = 0; n < w3.length; ++n) {
			String s1 = StringNormalizer.canonical(String.valueOf(c1[n]), false, true, true);
			String s2 = StringNormalizer.canonical(String.valueOf(c2[n]), false, true, true);
			int d = EditDistance.charDistance(s1, s2, W3, 10);
			assertThat(d).isEqualTo(d);
		}

		OcrOpWeight W4 = new OcrOpWeight(true); // ignore case
		int[] w4 = { 0, 1, 0, 1, 2 };
		for (int n = 0; n < w4.length; ++n) {
			String s1 = StringNormalizer.canonical(String.valueOf(c1[n]), true, false, true);
			String s2 = StringNormalizer.canonical(String.valueOf(c2[n]), true, false, true);
			int d = EditDistance.charDistance(s1, s2, W4, 10);
			assertThat(d).isEqualTo(w4[n]);
		}
	}

	/**
	 * Test of ins method, of class OcrWeights.
	 */
	@Test
	void testIns() {
		OcrOpWeight W = new OcrOpWeight(true); // ignore punct
		assertThat(W.ins('a')).isEqualTo(1);
		assertThat(W.ins('@')).isEqualTo(0);
		assertThat(W.ins('+')).isEqualTo(0);
		W = new OcrOpWeight();
		assertThat(W.ins('a')).isEqualTo(1);
		assertThat(W.ins('@')).isEqualTo(1);
		assertThat(W.ins('+')).isEqualTo(1);

	}
}
