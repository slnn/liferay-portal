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


import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferFile {
    private static final long serialVersionUID = 1l;

      protected ArrayList buffers = new ArrayList();

      long length;
      ByteBufferDirectory directory;
      protected long sizeInBytes;

      // This is publicly modifiable via Directory.touchFile(), so direct access not supported
      private long lastModified = System.currentTimeMillis();

      // File used as buffer, in no RAMDirectory
      protected ByteBufferFile() {
          System.out.println("Using byeBuffer");
          
      }
      
      ByteBufferFile(ByteBufferDirectory directory) {
        this.directory = directory;        
      }

      // For non-stream access from thread that might be concurrent with writing
      public synchronized long getLength() {
        return length;
      }

      protected synchronized void setLength(long length) {
        this.length = length;
      }

      // For non-stream access from thread that might be concurrent with writing
      public synchronized long getLastModified() {
        return lastModified;
      }

      protected synchronized void setLastModified(long lastModified) {
        this.lastModified = lastModified;
      }

      protected final ByteBuffer addBuffer(int size) {
        //byte[] buffer = newBuffer(size);
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        synchronized(this) {
          buffers.add(buffer);
          sizeInBytes += size;
        }

        if (directory != null) {
          synchronized(directory) {
            directory.sizeInBytes += size;
          }
        }
        return buffer;
      }

      protected final synchronized ByteBuffer getBuffer(int index) {
        return (ByteBuffer) buffers.get(index);
      }

      protected final synchronized int numBuffers() {
        return buffers.size();
      }

      /**
       * Expert: allocate a new buffer. 
       * Subclasses can allocate differently. 
       * @param size size of allocated buffer.
       * @return allocated buffer.
       */
      protected byte[] newBuffer(int size) {
        return new byte[size];
      }

      public synchronized long getSizeInBytes() {
        return sizeInBytes;
      }
    

}