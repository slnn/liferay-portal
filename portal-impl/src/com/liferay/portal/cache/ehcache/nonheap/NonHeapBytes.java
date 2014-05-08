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

import java.nio.ByteBuffer;

/**
 * @author Tina Tian
 */
public class NonHeapBytes {
	
	public NonHeapBytes(int size) {
		_byteBuffer = ByteBuffer.allocateDirect(size);
		
		_size = size;
	}
	
	public int getSize() {
		return _size;
	}
	
	public byte get(int index) {
		_byteBuffer.limit(index + 1);

		return _byteBuffer.get(index);
	}
	
	public void get(byte[] b, int offset, int length, int startIndex) {
		_byteBuffer.limit(startIndex + length + 1);
		_byteBuffer.position(startIndex);

		_byteBuffer.get(b, offset, length);
	}
	
	public void set(byte b, int index)  {
		_byteBuffer.limit(index + 1);
		
		_byteBuffer.put(index, b);
	}
	
	public void set(byte[] b, int offset, int length, int startIndex) {
		_byteBuffer.limit(startIndex + length + 1);
		_byteBuffer.position(startIndex);
		
		_byteBuffer.put(b, offset, length);
	}
	
	private ByteBuffer _byteBuffer;
	private int _size;

}
