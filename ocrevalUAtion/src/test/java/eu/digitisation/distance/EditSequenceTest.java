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
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import eu.digitisation.math.BiCounter;
import eu.digitisation.output.CharStatTable;

class EditSequenceTest {

	/**
	 * Test of cost method, of class EditSequence.
	 */
	@Test
	void testCost() {
		EditSequence instance = new EditSequence("acb", "a b", new OcrOpWeight());
		int expResult = 2;
		int result = instance.length();
		assertThat(result).isEqualTo(expResult);
	}

	/**
	 * Test of shift1 method, of class EditSequence.
	 */
	@Test
	void testShift1() {
		EditSequence instance = new EditSequence("acb", "a b", new OcrOpWeight());
		int expResult = 3;
		int result = instance.shift1();
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testStats() {
		System.out.println("stats");
		String s1 = "acb";
		String s2 = "abs";
		EditSequence instance = new EditSequence(s1, s2, new OcrOpWeight());
		BiCounter<Character, EdOp> result = instance.stats(s1, s2);
		BiCounter<Character, EdOp> expResult = new BiCounter<Character, EdOp>();
		expResult.add('a', EdOp.KEEP, 1);
		expResult.add('b', EdOp.KEEP, 1);
		expResult.add('c', EdOp.DELETE, 1);
		expResult.add('s', EdOp.INSERT, 1);
		assertThat(result).isEqualTo(expResult);

		EdOpWeight w = new OcrOpWeight();
		result = instance.stats(s1, s2, w);
		assertThat(result).isEqualTo(expResult);
	}

	@Disabled("to be clarified")
	@Test
	void testPunct() {
		OcrOpWeight w = new OcrOpWeight(true); /// ignore punctuation
		String s1 = "yes ! , he said";
		String s2 = "yes he said";
		EditSequence edit = new EditSequence(s1, s2, w);
		CharStatTable stats = new CharStatTable();
		stats.add(edit.stats(s1, s2));
		double cer = stats.cer();

		assertEquals(0, cer, 0.00001);

		assertThat(cer).isCloseTo(0d, Offset.offset(0.00001));
	}
}
