package com.buhzzi.wuej_0.kit;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class stream_helper {
	public static byte[] read_input_stream(InputStream is) throws IOException {
		final int block_size = 8192;
		final ArrayList<byte[]> blocks = new ArrayList<>();
		byte[] block = new byte[block_size];
		int off = 0;
		while (true) {
			final int b = is.read();
			if (b == -1) {
				break;
			}
			block[off++] = (byte) b;
			if (off == block_size) {{
				blocks.add(block);
				block = new byte[block_size];
				off = 0;
			}}
		}
		final long length = (long) blocks.size() * block_size + off;
		if (length > Integer.MAX_VALUE) {
			throw new IOException("Stream longer than 4 GiB");
		}
		final byte[] result = new byte[(int) length];
		int dst_off = 0;
		for (final byte[] block_ref : blocks) {
			System.arraycopy(block_ref, 0, result, dst_off, block_size);
			dst_off += block_size;
		}
		System.arraycopy(block, 0, result, (int) length - off, off);
		return result;
	}

	public static byte[] concat_data_slices(final byte[]... data_slices) throws java.lang.Exception {
		final byte[] data;
		{
			long len = 0;
			for (final byte[] data_slice : data_slices) {
				len += data_slice.length;
			}
			if (len > java.lang.Integer.MAX_VALUE) {
				throw new java.lang.Exception("len > java.lang.Integer.MAX_VALUE");
			}
			data = new byte[(int) len];
		}
		int off = 0;
		for (final byte[] data_slice : data_slices) {
			java.lang.System.arraycopy(data_slice, 0, data, off, data_slice.length);
			off += data_slice.length;
		}
		return data;
	}
}
