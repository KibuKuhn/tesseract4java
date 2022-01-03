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
package eu.digitisation.math;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BiCounterTest {

	@Test
	void testValue() {
		BiCounter<Integer, Integer> bc = new BiCounter<Integer, Integer>();
		bc.inc(1, 2);
		bc.inc(1, 3);
		bc.add(1, 3, 4);
		assertThat(bc.value(1, 2)).isEqualTo(1);
		assertThat(bc.value(1, 3)).isEqualTo(5);
		assertThat(bc.value(1, null)).isEqualTo(6);
		assertThat(bc.total()).isEqualTo(6);
	}

}
