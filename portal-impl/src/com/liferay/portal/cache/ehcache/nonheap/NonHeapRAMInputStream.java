/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.cache.ehcache.nonheap;

import java.io.EOFException;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

/**
 * @author Tina Tian
 */
public class NonHeapRAMInputStream extends IndexInput implements Cloneable {

	static final int BUFFER_SIZE = NonHeapRAMOutputStream.BUFFER_SIZE;

	private NonHeapRAMFile file;
	private long length;

	private byte[] currentBuffer;
	private int currentBufferIndex;

	private int bufferPosition;
	private long bufferStart;
	private int bufferLength;

	/** Please pass String name */
	@Deprecated
	public NonHeapRAMInputStream(NonHeapRAMFile f) throws IOException {
		this("anonymous", f);
	}

	public NonHeapRAMInputStream(String name, NonHeapRAMFile f)
		throws IOException {

		super("NonHeapRAMInputStream(name=" + name + ")");

		file = f;
		length = file.length;

		if (length/BUFFER_SIZE >= Integer.MAX_VALUE) {
			throw new IOException(
				"NonHeapRAMInputStream too large length=" + length + ": " +
					name); 
		}

		// make sure that we switch to the
		// first needed buffer lazily
		currentBufferIndex = -1;
		currentBuffer = null;
	}

	@Override
	public void close() {
		// nothing to do here
	}

	@Override
	public long length() {
		return length;
	}

	@Override
	public byte readByte() throws IOException {
		if (bufferPosition >= bufferLength) {
			currentBufferIndex++;

			switchCurrentBuffer(true);
		}

		return currentBuffer[bufferPosition++];
	}

	@Override
	public void readBytes(byte[] b, int offset, int len) throws IOException {
		while (len > 0) {
			if (bufferPosition >= bufferLength) {
				currentBufferIndex++;

				switchCurrentBuffer(true);
			}

			int remainInBuffer = bufferLength - bufferPosition;
			int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;

			System.arraycopy(
				currentBuffer, bufferPosition, b, offset, bytesToCopy);

			offset += bytesToCopy;
			len -= bytesToCopy;
			bufferPosition += bytesToCopy;
		}
	}

	private final void switchCurrentBuffer(boolean enforceEOF)
		throws IOException {

		bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;

		if (currentBufferIndex >= file.numBuffers()) {
			// end of file reached, no more buffers left
			if (enforceEOF) {
				throw new EOFException(
					"Read past EOF (resource: " + this + ")");
			}
			else {
				// Force EOF if a read takes place at this position

				currentBufferIndex--;
				bufferPosition = BUFFER_SIZE;
			}
		}
		else {
			currentBuffer = file.getBuffer(currentBufferIndex);

			bufferPosition = 0;

			long buflen = length - bufferStart;

			bufferLength = buflen > BUFFER_SIZE ? BUFFER_SIZE : (int) buflen;
		}
	}

	@Override
	public void copyBytes(IndexOutput out, long numBytes) throws IOException {
		long left = numBytes;

		while (left > 0) {
			if (bufferPosition == bufferLength) {
				++currentBufferIndex;

				switchCurrentBuffer(true);
			}

			final int bytesInBuffer = bufferLength - bufferPosition;
			final int toCopy =
				(int)(bytesInBuffer < left ? bytesInBuffer : left);

			out.writeBytes(currentBuffer, bufferPosition, toCopy);

			bufferPosition += toCopy;
			left -= toCopy;
		}
	}

	@Override
	public long getFilePointer() {
		return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
	}

	@Override
	public void seek(long pos) throws IOException {
		if (currentBuffer==null || pos < bufferStart ||
			pos >= bufferStart + BUFFER_SIZE) {

			currentBufferIndex = (int) (pos / BUFFER_SIZE);

			switchCurrentBuffer(false);
		}

		bufferPosition = (int) (pos % BUFFER_SIZE);
	}
	
}
