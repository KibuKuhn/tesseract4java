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
package eu.digitisation.document;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.digitisation.math.MinimalPerfectHash;

class TokenArrayTest {

	@Test
	void testEncode_String() {
		String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
				+ "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
		String expOutput = "hola&amigo 2 3.14 mi casa todos los días"
				+ " mesa-camilla java 4 you i.b.m i+d Dª María 3 100%";
		MinimalPerfectHash f = new MinimalPerfectHash(true);
		TokenArray array = new TokenArray(f, input);
		String output = array.toString();
		assertThat(output).isEqualTo(expOutput);

		int size = array.length();
		assertThat(size).isEqualTo(18);
	}
}
