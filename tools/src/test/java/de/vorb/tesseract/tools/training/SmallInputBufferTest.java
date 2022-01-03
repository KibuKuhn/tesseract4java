package de.vorb.tesseract.tools.training;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmallInputBufferTest {
	private static final byte[] BYTES = new byte[256];

	private InputBuffer buf;

	@BeforeEach
	void setUp() {
		buf = InputBuffer.allocate(new ByteArrayInputStream(BYTES), 4096);
	}

	@Test
	void testReadByte() throws IOException {
		int i = 0;
		while (buf.readByte()) {
			i++;
		}

		assertThat(i).isEqualTo(BYTES.length);
	}

	@Test
	void testReadShort() throws IOException {
		int i = 0;
		while (buf.readShort()) {
			i++;
		}

		assertThat(i).isEqualTo(BYTES.length / 2);
	}

	@Test
	void testReadInt() throws IOException {
		int i = 0;
		while (buf.readInt()) {
			i++;
		}

		assertThat(i).isEqualTo(BYTES.length / 4);
	}

	@Test
	void testReadLong() throws IOException {
		int i = 0;
		while (buf.readLong()) {
			i++;
		}

		assertThat(i).isEqualTo(BYTES.length / 8);
	}
}
