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
import org.apache.lucene.store.IndexInput;

class ByteBufferInputStream extends IndexInput implements Cloneable {
  static final int BUFFER_SIZE = 1024;

  private ByteBufferFile file;
  private long length;

  private ByteBuffer currentBuffer;
  private int currentBufferIndex;
  
  private int bufferPosition;
  private long bufferStart;
  private int bufferLength;

  ByteBufferInputStream(ByteBufferFile f) throws IOException {
    file = f;
    length = file.length;
    if (length/BUFFER_SIZE >= Integer.MAX_VALUE) {
      throw new IOException("Too large RAMFile! "+length); 
    }

    // make sure that we switch to the
    // first needed buffer lazily
    currentBufferIndex = -1;
    currentBuffer = null;
  }

  public void close() {
    // nothing to do here
  }

  public long length() {
    return length;
  }

  public byte readByte() throws IOException {
    if (bufferPosition >= bufferLength) {
      currentBufferIndex++;
      switchCurrentBuffer(true);
    }
    currentBuffer.position(bufferPosition++);
    byte b = currentBuffer.get();
    //return currentBuffer[bufferPosition++];
    return b;
  }

  public void readBytes(byte[] b, int offset, int len) throws IOException {
    while (len > 0) {
      if (bufferPosition >= bufferLength) {
        currentBufferIndex++;
        switchCurrentBuffer(true);
      }

      int remainInBuffer = bufferLength - bufferPosition;
      int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
      //System.arraycopy(currentBuffer, bufferPosition, b, offset, bytesToCopy);
      //arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
      currentBuffer.position(bufferPosition);
      currentBuffer.get(b, offset, bytesToCopy);
      offset += bytesToCopy;
      len -= bytesToCopy;
      bufferPosition += bytesToCopy;
      currentBuffer.position(bufferPosition);
    }
  }

  private final void switchCurrentBuffer(boolean enforceEOF) throws IOException {
    if (currentBufferIndex >= file.numBuffers()) {
      // end of file reached, no more buffers left
      if (enforceEOF)
        throw new IOException("Read past EOF");
      else {
        // Force EOF if a read takes place at this position
        currentBufferIndex--;
        bufferPosition = BUFFER_SIZE;
        currentBuffer.position(bufferPosition);
      }
    } else {
      currentBuffer = file.getBuffer(currentBufferIndex);
      bufferPosition = 0;
      currentBuffer.position(bufferPosition);
      bufferStart = (long) BUFFER_SIZE * (long) currentBufferIndex;
      long buflen = length - bufferStart;
      bufferLength = buflen > BUFFER_SIZE ? BUFFER_SIZE : (int) buflen;
    }
  }

  public long getFilePointer() {
    return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
  }

  public void seek(long pos) throws IOException {
    if (currentBuffer==null || pos < bufferStart || pos >= bufferStart + BUFFER_SIZE) {
      currentBufferIndex = (int) (pos / BUFFER_SIZE);
      switchCurrentBuffer(false);
    }
    bufferPosition = (int) (pos % BUFFER_SIZE);
    currentBuffer.position(bufferPosition);
  }
}