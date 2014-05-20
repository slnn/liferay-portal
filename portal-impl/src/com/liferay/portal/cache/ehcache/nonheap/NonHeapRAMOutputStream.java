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

import com.liferay.portal.cache.ehcache.non.heap.NonHeapByteArray;
import java.io.IOException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.IndexOutput;

/**
 * @author Tina Tian
 */
public class NonHeapRAMOutputStream extends IndexOutput {
	static final int BUFFER_SIZE = 1024;

	private NonHeapRAMFile file;

	private NonHeapByteArray currentBuffer;
	private int currentBufferIndex;

	private int bufferPosition;
	private long bufferStart;
	private int bufferLength;

	/** Construct an empty output buffer. */
	public NonHeapRAMOutputStream() {
		this(new NonHeapRAMFile());
	}

	public NonHeapRAMOutputStream(NonHeapRAMFile f) {
		file = f;

		// make sure that we switch to the
		// first needed buffer lazily
		currentBufferIndex = -1;
		currentBuffer = null;
	}

	/** Copy the current contents of this buffer to the named output. */
	public void writeTo(IndexOutput out) throws IOException {
		flush();

		final long end = file.length;
		long pos = 0;
		int buffer = 0;

		while (pos < end) {
			int length = BUFFER_SIZE;
			long nextPos = pos + length;

			if (nextPos > end) {
				length = (int)(end - pos);
			}

			NonHeapByteArray nonHeapBytes = file.getBuffer(buffer++);
			
			byte[] bytes = new byte[length];
			
			nonHeapBytes.copyTo(bytes, 0, length, length);
			
			out.writeBytes(bytes, length);

			pos = nextPos;
		}
	}

	/** Resets this to an empty file. */
	public void reset() {
		currentBuffer = null;
		currentBufferIndex = -1;
		bufferPosition = 0;
		bufferStart = 0;
		bufferLength = 0;
		file.setLength(0);
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	@Override
	public void seek(long pos) throws IOException {
		// set the file length in case we seek back
		// and flush() has not been called yet
		setFileLength();

		if (pos < bufferStart || pos >= bufferStart + bufferLength) {
			currentBufferIndex = (int) (pos / BUFFER_SIZE);

			switchCurrentBuffer();
		}

		bufferPosition = (int) (pos % BUFFER_SIZE);
	}

	@Override
	public long length() {
		return file.length;
	}

	@Override
	public void writeByte(byte b) throws IOException {
		if (bufferPosition == bufferLength) {
			currentBufferIndex++;
			switchCurrentBuffer();
		}
		
		currentBuffer.put(bufferPosition++, b);
	}

	@Override
	public void writeBytes(byte[] b, int offset, int len) throws IOException {
		while (len > 0) {
			if (bufferPosition ==  bufferLength) {
				currentBufferIndex++;

				switchCurrentBuffer();
			}

			int remainInBuffer = currentBuffer.getSize() - bufferPosition;

			int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;

			currentBuffer.copyFrom(b, offset, bytesToCopy, bufferPosition);

			offset += bytesToCopy;
			len -= bytesToCopy;
			bufferPosition += bytesToCopy;
		}
	}

	private final void switchCurrentBuffer() throws IOException {
		if (currentBufferIndex == file.numBuffers()) {
			currentBuffer = file.addBuffer(BUFFER_SIZE);
		}
		else {
			currentBuffer = file.getBuffer(currentBufferIndex);
		}

		bufferPosition = 0;
		bufferStart = (long)BUFFER_SIZE * (long)currentBufferIndex;
		bufferLength = currentBuffer.getSize();
	}

	private void setFileLength() {
		long pointer = bufferStart + bufferPosition;

		if (pointer > file.length) {
			file.setLength(pointer);
		}
	}

	@Override
	public void flush() throws IOException {
		file.setLastModified(System.currentTimeMillis());

		setFileLength();
	}

	@Override
	public long getFilePointer() {
		return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
	}

	/** Returns byte usage of all buffers. */
	public long sizeInBytes() {
		return file.numBuffers() * BUFFER_SIZE;
	}

	@Override
	public void copyBytes(DataInput input, long numBytes) throws IOException {
		assert numBytes >= 0: "numBytes=" + numBytes;

		while (numBytes > 0) {
			if (bufferPosition == bufferLength) {
				currentBufferIndex++;

				switchCurrentBuffer();
			}

			int toCopy = currentBuffer.getSize() - bufferPosition;

			if (numBytes < toCopy) {
				toCopy = (int) numBytes;
			}

			byte[] bytes = new byte[toCopy];
			
			input.readBytes(bytes, 0, toCopy, false);
			
			currentBuffer.copyFrom(bytes, 0, toCopy, bufferPosition);

			numBytes -= toCopy;
			bufferPosition += toCopy;
		}
	}

}
