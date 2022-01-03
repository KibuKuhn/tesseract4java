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
package eu.digitisation.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BatchTest {

	/**
	 * Test of prefix method
	 */
	@Test
	void testLcp() {
		String s1 = "compare";
		String s2 = "competence";
		String expResult = "comp";
		String result = Batch.prefix(s1, s2);
		assertThat(result).isEqualTo(expResult);
	}

	/**
	 * Test of suffix method
	 */
	@Test
	void testLcs() {
		String s1 = "switzerland";
		String s2 = "disneyland";
		String expResult = "land";
		String result = Batch.suffix(s1, s2);
		assertThat(result).isEqualTo(expResult);
		s2 = "sweden";
		expResult = "";
		result = Batch.suffix(s1, s2);
		assertThat(result).isEqualTo(expResult);
	}

}
