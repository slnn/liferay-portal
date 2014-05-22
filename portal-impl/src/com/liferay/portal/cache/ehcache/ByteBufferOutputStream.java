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

package com.liferay.portal.cache.ehcache;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.lucene.store.IndexOutput;

/**
 * A memory-resident {@link IndexOutput} implementation.
 * 
 * <p>For Lucene internal use</p>
 * @version $Id: RAMOutputStream.java 941125 2010-05-05 00:44:15Z mikemccand $
 */

public class ByteBufferOutputStream extends IndexOutput {
  static final int BUFFER_SIZE = 1024;

  private ByteBufferFile file;

  private ByteBuffer currentBuffer;
  private int currentBufferIndex;
  
  private int bufferPosition;
  private long bufferStart;
  private int bufferLength;

  /** Construct an empty output buffer. */
  public ByteBufferOutputStream() {
    this(new ByteBufferFile());
  }

  public ByteBufferOutputStream(ByteBufferFile f) {
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
      if (nextPos > end) {                        // at the last buffer
        length = (int)(end - pos);
      }
      //out.writeBytes((byte[])file.getBuffer(buffer++), length);
      byte[] b = new byte[length];
      ByteBuffer bbuffer = file.getBuffer(buffer++);
      bbuffer.get(b, 0, length);
      out.writeBytes(b, length);
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

  public void close() throws IOException {
    flush();
  }

  public void seek(long pos) throws IOException {
    // set the file length in case we seek back
    // and flush() has not been called yet
    setFileLength();
    if (pos < bufferStart || pos >= bufferStart + bufferLength) {
      currentBufferIndex = (int) (pos / BUFFER_SIZE);
      switchCurrentBuffer();
    }

    bufferPosition = (int) (pos % BUFFER_SIZE);
    currentBuffer.position(bufferPosition);
  }

  public long length() {
    return file.length;
  }

  public void writeByte(byte b) throws IOException {
    if (bufferPosition == bufferLength) {
      currentBufferIndex++;
      switchCurrentBuffer();
    }
    //currentBuffer[bufferPosition++] = b;
    currentBuffer.position(bufferPosition++);
    currentBuffer.put(b);
  }

  public void writeBytes(byte[] b, int offset, int len) throws IOException {
    assert b != null;
    while (len > 0) {
      if (bufferPosition ==  bufferLength) {
        currentBufferIndex++;
        switchCurrentBuffer();
      }

      int remainInBuffer = currentBuffer.capacity() - bufferPosition;
      int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
      
      //System.arraycopy(b, offset, currentBuffer, bufferPosition, bytesToCopy);
      currentBuffer.position(bufferPosition);
      currentBuffer.put(b, offset, bytesToCopy);
      
      offset += bytesToCopy;
      len -= bytesToCopy;
      bufferPosition += bytesToCopy;
      currentBuffer.position(bufferPosition);
    }
  }

  private final void switchCurrentBuffer() throws IOException {
    if (currentBufferIndex == file.numBuffers()) {
      currentBuffer = file.addBuffer(BUFFER_SIZE);
    } else {
      currentBuffer = file.getBuffer(currentBufferIndex);
    }
    bufferPosition = 0;
    currentBuffer.position(bufferPosition);
    bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
    bufferLength = currentBuffer.capacity();
  }

  private void setFileLength() {
    long pointer = bufferStart + bufferPosition;
    if (pointer > file.length) {
      file.setLength(pointer);
    }
  }

  public void flush() throws IOException {
    file.setLastModified(System.currentTimeMillis());
    setFileLength();
  }

  public long getFilePointer() {
    return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
  }

  /** Returns byte usage of all buffers. */
  public long sizeInBytes() {
    return file.numBuffers() * BUFFER_SIZE;
  }
}