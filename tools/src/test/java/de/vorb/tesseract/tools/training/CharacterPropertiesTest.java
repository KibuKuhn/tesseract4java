package de.vorb.tesseract.tools.training;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CharacterPropertiesTest {
    final CharacterProperties semicolon = new CharacterProperties(false, false,
            false, false, true);
    final CharacterProperties b = new CharacterProperties(true, false, false,
            true, false);
    final CharacterProperties W = new CharacterProperties(true, false, true,
            false, false);
    final CharacterProperties digit7 = new CharacterProperties(false, true,
            false, false, false);
    final CharacterProperties equalSign = new CharacterProperties(false, false,
            false, false, false);
    final CharacterProperties sharpS = new CharacterProperties(true, false,
            false, true, false);
    final CharacterProperties umlautA = new CharacterProperties(true, false,
            true, false, false);

    @Test
    void testForByteCode() {
        assertThat(semicolon).isEqualTo(CharacterProperties.forByteCode((byte) 16));
        assertThat(b).isEqualTo(CharacterProperties.forByteCode((byte) 3));
        assertThat(W).isEqualTo(CharacterProperties.forByteCode((byte) 5));
        assertThat(digit7).isEqualTo(CharacterProperties.forByteCode((byte) 8));
        assertThat(equalSign).isEqualTo(CharacterProperties.forByteCode((byte) 0));
    }

    @Test
    void testFromHexString() {
        assertThat(semicolon).isEqualTo(CharacterProperties.forHexString("10"));
        assertThat(b).isEqualTo(CharacterProperties.forHexString("3"));
        assertThat(W).isEqualTo(CharacterProperties.forHexString("5"));
        assertThat(digit7).isEqualTo(CharacterProperties.forHexString("8"));
        assertThat(equalSign).isEqualTo(CharacterProperties.forHexString("0"));
    }

    @Test
    void testForCharacter() {
        assertThat(semicolon).isEqualTo(CharacterProperties.forCharacter(';'));
        assertThat(b).isEqualTo(CharacterProperties.forCharacter('b'));
        assertThat(W).isEqualTo(CharacterProperties.forCharacter('W'));
        assertThat(digit7).isEqualTo(CharacterProperties.forCharacter('7'));
        assertThat(equalSign).isEqualTo(CharacterProperties.forCharacter('='));

        // unicode characters
        assertThat(sharpS).isEqualTo(CharacterProperties.forCharacter('ß'));
        assertThat(umlautA).isEqualTo(CharacterProperties.forCharacter('Ä'));
    }

    @Test
    void testToByteCode() {
        assertThat(semicolon.toByteCode()).isEqualTo((byte) 16);
        assertThat(b.toByteCode()).isEqualTo((byte) 3);
        assertThat(W.toByteCode()).isEqualTo((byte) 5);
        assertThat(digit7.toByteCode()).isEqualTo((byte) 8);
        assertThat(equalSign.toByteCode()).isEqualTo((byte) 0);
    }

    @Test
    void testToHexString() {
        assertThat(semicolon.toHexString()).isEqualTo("10");
        assertThat(b.toHexString()).isEqualTo("3");
        assertThat(W.toHexString()).isEqualTo("5");
        assertThat(digit7.toHexString()).isEqualTo("8");
        assertThat(equalSign.toHexString()).isEqualTo("0");
    }

    @Test
    void testEquals() {
        assertThat(b.equals(b)).isTrue();
        assertThat(b.equals(W)).isFalse();
    }

    @Test
    void testHashCode() {
        assertThat(b.hashCode()).isEqualTo(b.hashCode());
        assertThat(b.hashCode()).isNotEqualTo(W.hashCode());
    }
}
