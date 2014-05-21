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

import java.io.Serializable;


import java.util.ArrayList;

/**
 * @author Tina Tian
 */
public class NonHeapRAMFile implements Serializable {

	private static final long serialVersionUID = 1l;

	protected ArrayList<byte[]> buffers = new ArrayList<byte[]>();
	long length;
	NonHeapRAMDirectory directory;
	protected long sizeInBytes;

	// This is publicly modifiable via Directory.touchFile(), so direct access not supported
	private long lastModified = System.currentTimeMillis();

	// File used as buffer, in no RAMDirectory
	public NonHeapRAMFile() {}

	NonHeapRAMFile(NonHeapRAMDirectory directory) {
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

	protected final byte[] addBuffer(int size) {
		byte[] buffer = newBuffer(size);

		synchronized(this) {
			buffers.add(buffer);
			sizeInBytes += size;
		}

		if (directory != null) {
			directory.sizeInBytes.getAndAdd(size);
		}

		return buffer;
	}

	protected final synchronized byte[] getBuffer(int index) {
		return buffers.get(index);
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