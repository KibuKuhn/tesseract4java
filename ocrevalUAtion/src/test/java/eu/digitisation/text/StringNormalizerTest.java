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
package eu.digitisation.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringNormalizerTest {

	@Test
	void testReduceWS() {
		String s = "one  \rtwo\nthree";
		String expResult = "one two three";
		String result = StringNormalizer.reduceWS(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testComposed() {
		String s = "n\u0303";
		String expResult = "ñ";
		String result = StringNormalizer.composed(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testCompatible() {
		String s = "\ufb00"; // ff ligature
		String expResult = "ff";
		String result = StringNormalizer.compatible(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testRemoveDiacritics() {
		String s = "cañón";
		String expResult = "canon";
		String result = StringNormalizer.removeDiacritics(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testRemovePunctuation() {
		String s = "!\"#}-"; // + is not in punctuation block
		String expResult = "";
		String result = StringNormalizer.removePunctuation(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testTrim() {
		String s = "! \"#lin?ks+!\"#}-"; // + is not in punctuation block
		String expResult = "lin?ks+";
		String result = StringNormalizer.trim(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testStrip() {
		String s = "Stra\u00dfe+ links+!\"#}-"; // ª is a letter!
		String expResult = "Stra\u00dfe links";
		String result = StringNormalizer.strip(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testEncode() {
		String s = "<\">";
		String expResult = "&lt;&quot;&gt;";
		String result = StringNormalizer.encode(s);
		assertThat(result).isEqualTo(expResult);
	}
}
