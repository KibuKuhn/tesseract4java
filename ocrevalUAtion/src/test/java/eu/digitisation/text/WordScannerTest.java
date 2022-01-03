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
package eu.digitisation.text;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class WordScannerTest {

	@Test
	void testnextWord() throws IOException {
		String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
				+ "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
		WordScanner scanner = new WordScanner(input, null);
		String word;
		int num = 0;
		while ((word = scanner.nextWord()) != null) {
			++num;
		}
		Assertions.assertThat(num).isEqualTo(18);

	}
}
