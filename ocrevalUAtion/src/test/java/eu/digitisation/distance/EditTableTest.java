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
package eu.digitisation.distance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EditTableTest {

	/*
	 * @Test void testSet() { System.out.println("set"); byte b = 0; byte result =
	 * EditTable.setBit(b, 0, true); System.out.println(result);
	 * Assertions.assertThat(1, result); Assertions.assertThat(true,
	 * EditTable.getBit(result,0)); }
	 */
	/**
	 * Test of get method, of class EditTable.
	 */
	@Test
	void testGet() {
		EditTable instance = new EditTable(2, 2);
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				if (i == j) {
					instance.set(i, j, EdOp.KEEP);
				} else {
					instance.set(i, j, EdOp.SUBSTITUTE);
				}
			}
		}
		for (int i = 0; i < 2; ++i) {
			for (int j = 0; j < 2; ++j) {
				if (i == j) {
					assertThat(instance.get(i, j)).isEqualTo(EdOp.KEEP);
				} else {
					assertThat(instance.get(i, j)).isEqualTo(EdOp.SUBSTITUTE);
				}
			}
		}
	}
}
