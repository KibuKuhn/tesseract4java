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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;

class CharFilterTest {

	
	@Test
	void testTranslate_String() throws URISyntaxException {
		URL resourceUrl = getClass().getResource("/UnicodeCharEquivalences.txt");
		File file = new File(resourceUrl.toURI());
		CharFilter filter = new CharFilter(file);
		String s = "a\u0133"; // ij
		String expResult = "aij";
		String result = filter.translate(s);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testCompatibilityMode() {
		CharFilter filter = new CharFilter();
		String s = "\u0133";
		String r = "ij";
		assert (!r.equals(filter.translate(s)));
		filter.setCompatibility(true);
		assertThat(filter.translate(s)).isEqualTo(r);

	}
}
