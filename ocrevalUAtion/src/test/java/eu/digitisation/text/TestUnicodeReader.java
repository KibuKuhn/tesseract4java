package eu.digitisation.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

 class TestUnicodeReader {

	@Test
     void testUnicodeReader() {
        String input = "día, mes y año";
        String ref = "[100, 237, 97, 44, 32, 109, 101, 115, 32, 121, 32, 97, 241, 111]";

        String output =
                java.util.Arrays.toString(UnicodeReader.toCodepoints(input));
        assertThat(output).isEqualTo(ref);
    }
}
