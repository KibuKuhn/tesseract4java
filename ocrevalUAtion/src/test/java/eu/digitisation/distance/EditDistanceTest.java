/*
 * Copyright (C) 2014 Uni. de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.distance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import eu.digitisation.text.StringNormalizer;

public class EditDistanceTest {

    @Test
    public void testWeights() {
        EdOpWeight w = new OcrOpWeight();
        String s1 = "a b";
        String s2 = "acb";
        int expResult = 2;
        int result = EditDistance.charDistance(s1, s2, w, 50);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of wordDistance method, of class EditDistance.
     */
    @Test
    public void testWordDistance() {        
        String s1 = "p a t a t a";
        String s2 = "a p t a";
        int expResult = 3;
        int[] result = EditDistance.wordDistance(s1, s2, 10);
        assertThat(result[2]).isEqualTo(expResult);
    }

    @Test
    public void testWeightedDistance() {
        String s1 = "ÁÁÁÁ";
        String s2 = "ÁAáa";

        OcrOpWeight W = new OcrOpWeight(); // fully-sensitive
        String r1 = StringNormalizer.canonical(s1, false, false, false);
        String r2 = StringNormalizer.canonical(s2, false, false, false);
        assertThat(EditDistance.charDistance(r1, r2, W, 1000)).isEqualTo(3);

        W = new OcrOpWeight(true); //ignore everything
        r1 = StringNormalizer.canonical(s1, true, true, true);
        r2 = StringNormalizer.canonical(s2, true, true, true);
        assertThat(EditDistance.charDistance(r1, r2, W, 1000)).isEqualTo(0);

        W = new OcrOpWeight(true); //ignore diacritics
        r1 = StringNormalizer.canonical(s1, false, true, true);
        r2 = StringNormalizer.canonical(s2, false, true, true);
        assertThat(EditDistance.charDistance(r1, r2, W, 1000)).isEqualTo(2);

        W = new OcrOpWeight(true); //ignore case
        r1 = StringNormalizer.canonical(s1, true, false, true);
        r2 = StringNormalizer.canonical(s2, true, false, true);
        assertThat(EditDistance.charDistance(r1, r2, W, 1000)).isEqualTo(2);
    }
}
