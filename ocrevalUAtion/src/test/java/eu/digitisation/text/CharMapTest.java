/*
 * Copyright (C) 2014 U. de Alicante
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import eu.digitisation.text.CharMap.Option;

class CharMapTest {

	@Test
	void testNormalForm_String() throws URISyntaxException {
		String s = "Mañana! Antígona2";
		Option[] ops = {};
		CharMap map = new CharMap(ops);
		String expResult = "manana antigona2";
		String result = map.normalForm(s);
		assertThat(result).isEqualTo(expResult);
		// Test comaptiblity file
		URL resourceUrl = getClass().getResource("/UnicodeCharEquivalences.txt");
		File file = new File(resourceUrl.toURI());
		CharMap filter = new CharMap();
		filter.addFilter(file);
		s = "a\uf50d"; // triple ligature not in Unicode compatibilty
		expResult = "aq\u0301\uA76B"; // q + acute + et
		result = filter.normalForm(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testNormalForm_char() {
		char longs = '\u017F'; // a long s
		char ff = '\ufb00';
		Option[] ops = {};
		CharMap map = new CharMap(ops);
		String expResult;
		String result;
		result = map.normalForm(longs);
		expResult = "s";
		assertThat(result).isEqualTo(expResult);
		result = map.normalForm(ff);
		expResult = "ff";
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testEquiv() {
		char c1 = '?';
		char c2 = ' ';
		Option[] ops = {};
		CharMap instance = new CharMap(ops);
		boolean expResult = true;
		boolean result = instance.equiv(c1, c2);
		assertThat(result).isEqualTo(expResult);
	}

}
