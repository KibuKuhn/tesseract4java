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
package eu.digitisation.math;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.junit.jupiter.api.Test;

class ArraysTest {

	@Test
	void testSum_intArr() {
		int[] array = { 1, 2, 3, 0, -1 };
		int expResult = 5;
		int result = Arrays.sum(array);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testSum_doubleArr() {
		double[] array = { 1, 2, 3, 0, -1 };
		double expResult = 5;
		double result = Arrays.sum(array);
		assertThat(result).isCloseTo(expResult, offset(0.01));
	}

	@Test
	void testAverage_intArr() {
		int[] array = { 1, 2, 3, -2 };
		double expResult = 1.0;
		double result = Arrays.average(array);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

	@Test
	void testAverage_doubleArr() {
		double[] array = { 1, 2, 3, -2 };
		double expResult = 1.0;
		double result = Arrays.average(array);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

	@Test
	void testLogaverage_intArr() {
		int[] array = { 10, 100, 1000 };
		double expResult = 100.0;
		double result = Arrays.logaverage(array);
		assertThat(result).isCloseTo(expResult, offset(0.001));
	}

	@Test
	void testLogaverage_doubleArr() {
		double[] array = { 10, 100, 1000 };
		double expResult = 100.0;
		double result = Arrays.logaverage(array);
		assertThat(result).isCloseTo(expResult, offset(0.001));
	}

	@Test
	void testScalar() {
		double[] x = { 1, 2, 3 };
		double[] y = { 1, 2, 3 };
		double expResult = 14.0;
		double result = Arrays.scalar(x, y);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

	@Test
	void testMax_intArr() {
		int[] array = { -5, 2, 3 };
		int expResult = 3;
		int result = Arrays.max(array);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testMax_doubleArr() {
		double[] array = { -5, 2, 3 };
		double expResult = 3;
		double result = Arrays.max(array);
		assertThat(result).isCloseTo(expResult, offset(0.0));
	}

	@Test
	void testMin_intArr() {
		int[] array = { 2, -1 };
		int expResult = -1;
		int result = Arrays.min(array);
		assertThat(result).isEqualTo(expResult);
	}

	@Test
	void testMin_doubleArr() {
		double[] array = { 2, 0, -1 };
		double expResult = -1.0;
		double result = Arrays.min(array);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

	@Test
	void testCov_intArr_intArr() {
		int[] X = { 1, 2, 3 };
		int[] Y = { 1, 2, 3 };
		double expResult = 2.0 / 3;
		double result = Arrays.cov(X, Y);
		assertThat(result).isCloseTo(expResult, offset(0.001));
	}

	@Test
	void testCov_doubleArr_doubleArr() {
		double[] X = { 1, 2, 3 };
		double[] Y = { 1, 2, 3 };
		double expResult = 2.0 / 3;
		double result = Arrays.cov(X, Y);
		assertThat(result).isCloseTo(expResult, offset(0.001));
	}

	@Test
	void testStd_intArr() {
		int[] X = { 1, 2, 2, 3 };
		double expResult = Math.sqrt(0.5);
		double result = Arrays.std(X);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

	@Test
	void testStd_doubleArr() {
		double[] X = { 1, 2, 2, 3 };
		double expResult = Math.sqrt(0.5);
		double result = Arrays.std(X);
		assertThat(result).isCloseTo(expResult, offset(0.0001));
	}

}
