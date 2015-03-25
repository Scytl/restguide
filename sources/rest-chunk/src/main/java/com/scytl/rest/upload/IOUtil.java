package com.scytl.rest.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class IOUtil {

	private IOUtil() {
		super();
	}
	
	public static final long copyToFile(final Path destination, final InputStream source,
			final long maxSize) throws IOException {

		InputStream fis = new BoundedInputStream(source);

		try (ReadableByteChannel channel = Channels.newChannel(fis);
				FileChannel out = FileChannel.open(destination,
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);) {

			ByteBuffer bytebuf = ByteBuffer.allocateDirect(2048);
			long writtenBytes = 0;
			while ((channel.read(bytebuf)) > 0) {
				// Read data from file into ByteBuffer
				// flip the buffer which set the limit to current position, and
				// position to 0.
				bytebuf.flip();
				writtenBytes += out.write(bytebuf); // Write data from ByteBuffer to file
				bytebuf.clear(); // For the next read
			}
			return writtenBytes;
		}
	}
}
