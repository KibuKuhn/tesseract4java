package de.vorb.tesseract.tools.training;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmptyInputBufferTest {
	private InputBuffer empty;

	@BeforeEach
	void setUp() throws Exception {
		empty = InputBuffer.allocate(new ByteArrayInputStream(new byte[0]), 4096);
	}

	@Test
	void testReadByte() throws IOException {
		assertThat(empty.readByte()).isFalse();
	}

	@Test
	void testReadShort() throws IOException {
		assertThat(empty.readShort()).isFalse();
	}

	@Test
	void testReadInt() throws IOException {
		assertThat(empty.readInt()).isFalse();
	}

	@Test
	void testReadLong() throws IOException {
		assertThat(empty.readLong()).isFalse();
	}
}
