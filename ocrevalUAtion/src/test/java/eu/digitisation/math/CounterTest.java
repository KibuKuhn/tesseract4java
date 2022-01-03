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

import java.util.List;

import org.junit.jupiter.api.Test;


 class CounterTest {

  
    @Test
     void test() {
        Counter<Integer> instance = new Counter<Integer>();
        instance.add(1, 3);
        instance.inc(1);
        instance.add(1, -1);
        assertThat(instance.get(1).intValue()).isEqualTo(3);
    }

    @Test
     void testKeyList() {       
        Counter<Integer> instance = new Counter<Integer>();
        instance.add(1, 6);
        instance.add(2, 3);
        instance.add(3, 1);
        instance.add(4, 5);
        Integer[] expResult = {3, 2, 4, 1};
        Integer[] result = new Integer[4];
        List<Integer> list = instance.keyList(Counter.Order.ASCENDING_VALUE);        
        list.toArray(result);
        assertThat(result).isEqualTo(expResult);
    }

}
