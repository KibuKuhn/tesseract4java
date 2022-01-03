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
package eu.digitisation.ngram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class NgramModelTest {

	@Test
	void testSize() {
		NgramModel ngrams = new NgramModel(2);
		ngrams.addWord("0000");
		ngrams.addWord("0100");

		int expResult = 9; // 5 bi-grams plus 3 uni-grams plus 1 0-gram
		int result = ngrams.size();

		assertThat(result).isEqualTo(expResult);

		ngrams = new NgramModel(3);
		ngrams.addWord("0000");
		ngrams.addWord("0100");

		expResult = 15; // 6 tri-grams + 5 bi-grams +3 uni-grams + 1 0-gram
		result = ngrams.size();

		assertThat(result).isEqualTo(expResult);

	}

	@Test
	void testGetGoodTuringPars() {
		NgramModel ngrams = new NgramModel(3);
		ngrams.addWord("0000");
		ngrams.addWord("0100");
		double[] expResult = { 0.1, 0.2, 0.5 };
		double[] result = ngrams.getGoodTuringPars();
		assertThat(result.length).isEqualTo(expResult.length);
		for (int n = 0; n < result.length; ++n) {
			assertThat(result[n]).isCloseTo(expResult[n], offset(0.001));
		}

	}

	@Test
	void testProb() {
		NgramModel ngrams = new NgramModel(3);
		ngrams.addWord("0000");
		ngrams.addWord("0100");

		assertThat(ngrams.prob("00")).isCloseTo(4 / (double) 7, offset(0.001));
		assertThat(ngrams.prob("0")).isCloseTo(0.7, offset(0.001));
	}

	@Test
	void testSmoothProb() {
		NgramModel ngrams = new NgramModel(3);
		ngrams.addWord("0000");
		ngrams.addWord("0100");

		double expResult = 0.8 * (4 / (double) 7) + 0.2 * 0.7;
		double result = ngrams.smoothProb("00");
		assertThat(result).isCloseTo(expResult, offset(0.001));

		expResult = 0.8 * (2 / (double) 7) + 0.2 * 0.2;
		result = ngrams.smoothProb("0" + NgramModel.EOS);
		assertThat(result).isCloseTo(expResult, offset(0.001));
	}

	@Test
	void testWordLogProb() {
		NgramModel instance = new NgramModel(1);
		instance.addWord("lava");
		double expResult = (3 * Math.log(0.2) + 2 * Math.log(0.4));
		double result = instance.logWordProb("lava");
		assertThat(result).isCloseTo(expResult, offset(0.01));
	}

	@Test
	void testLogProb() {
		NgramModel instance = new NgramModel(1);
		instance.addWord("lava");
		double expResult = -Math.log(5);
		double result = instance.logProb("baba", 'v');
		assertThat(result).isCloseTo(expResult, offset(0.01));

		instance = new NgramModel(2);
		instance.addWord("lava");
		expResult = Math.log(0.2);
		result = instance.logProb("ca", 'v');
		assertThat(result).isCloseTo(expResult, offset(0.01));
	}

	@Test
	void testAddSubstrings() {		
		NgramModel ngrams = new NgramModel(3);
		NgramModel ref = new NgramModel(3);

		ngrams.addSubstrings("b", "cde");

		// 3-grams
//        ref.addEntry("abc");
		ref.addEntry("bcd");
		ref.addEntry("cde");

		// 2-grams
		ref.addEntry("bc");
		ref.addEntry("cd");
		ref.addEntry("de");

		// 1-grams
		ref.addEntry("c");
		ref.addEntry("d");
		ref.addEntry("e");

		// 0-grams
		ref.addEntries("", 3);
		ref.showDiff(ngrams);

		assertThat(ngrams).isEqualTo(ref);

	}

	@Test
	void testAddText() {
		String BOS = String.valueOf(NgramModel.BOS);
		String EOS = String.valueOf(NgramModel.EOS);
		NgramModel ngrams = new NgramModel(3);
		NgramModel ref = new NgramModel(3);
		String input = "ab\nc";
		InputStream is = new ByteArrayInputStream(input.getBytes());

		// result
		ngrams.addText(is);

		// expected result
		// 3-grams
		ref.addEntry(BOS + "ab");
		ref.addEntry("ab ");
		ref.addEntry("b c");
		ref.addEntry(" c" + EOS);

		// 2-grams
		ref.addEntry(BOS + 'a');
		ref.addEntry("ab");
		ref.addEntry("b ");
		ref.addEntry(" c");
		ref.addEntry("c" + EOS);
		// 1-grams
		ref.addEntry("a");
		ref.addEntry("b");
		ref.addEntry(" ");
		ref.addEntry("c");
		ref.addEntry(EOS);
		// 0-grams
		ref.addEntries("", 5);

		ref.showDiff(ngrams);

		assertThat(ngrams).isEqualTo(ref);

		String text = "ab";

		is = new ByteArrayInputStream(text.getBytes());
		double expectedResult = Math.log(0.2);
		double result = ngrams.logLikelihood(is, 0);
		assertThat(result).isCloseTo(expectedResult, offset(0.0001));
	}
}
