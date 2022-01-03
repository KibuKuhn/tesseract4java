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
package eu.digitisation.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.digitisation.distance.EditDistance;

class TermFrequencyVectorTest {

	private static final String S1 = // "UN AN : PARIS, 8 Francs. — PROVINCE, 10 Francs. — ETRANGER, suivant le Tarif
										// postal. "
			"A LA LIBRAIRIE, 10, RUE DE LA BOURSE. CHRONIQUE GOURMANDE UNE des gracieuses";
	private static final String S2 = // "V AN : PA's»s*c8fFrancs. — Pr«vjnv-e, 11 > Fr.it:-*.— K: kvnobi. ', Tarif
										// ;\".s:a!. 1- ni 7
			"A LA LIBRAIRIE, 10. RUE DE LA BOURSE. TOUS PREMIER. I I VRAIS'< , CHRONIQUE GOURMANDE * ,' -~J.,' 1 Ii nk .!•'« gracieuses";

	
	@Test
	void testDistance() {
		TermFrequencyVector tf1 = new TermFrequencyVector(S1);
		TermFrequencyVector tf2 = new TermFrequencyVector(S2);
		int expResult = EditDistance.wordDistance(S1, S2, 1000)[2];
		int result = tf1.distance(tf2);
		assertThat(result).isEqualTo(expResult);
	}

	
	@Test
	void testTotal() {
		TermFrequencyVector tf1 = new TermFrequencyVector(S1);
		TermFrequencyVector tf2 = new TermFrequencyVector(S2);
		assertThat(tf1.total()).isEqualTo(13);
		assertThat(tf2.total()).isEqualTo(20);
	}

}
