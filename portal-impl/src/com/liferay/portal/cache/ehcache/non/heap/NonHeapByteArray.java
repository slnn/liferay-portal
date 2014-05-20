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
package com.liferay.portal.cache.ehcache.non.heap;

/**
 * @author Tina Tian
 */
public class NonHeapByteArray {

	public NonHeapByteArray(int size) {
		_size = size;

		_address = DirectMemory.alloc(size);
	}
	
	public int getSize() {
		return _size;
	}
	
	@Override
    public void finalize() {
        DirectMemory.free(_address);
    }
	
	public byte get(int index) {
		return DirectMemory.getByte(_address + index);
	}
	
	public void copyTo(byte[] bytes, int offset, int length, int startIndex) {
		long startAddress = _address + startIndex;

		for (int i = offset; i < offset + length; i++) {
			bytes[i] = DirectMemory.getByte(startAddress++);
		}
	}
	
	public void put(int index, byte b)  {
		DirectMemory.putByte(_address + index, b);
	}
	
	public void copyFrom(byte[] bytes, int offset, int length, int startIndex) {
		long startAddress = _address + startIndex;
		
		for (int i = offset; i < offset + length; i++) {
			DirectMemory.putByte(startAddress++, bytes[i]);
		}
	}

	private long _address;
	private int _size;

}
