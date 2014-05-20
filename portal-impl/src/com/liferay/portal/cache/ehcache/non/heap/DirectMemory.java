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

import java.lang.reflect.Field;
import sun.misc.Unsafe;

/**
 * @author Tina Tian
 */
public class DirectMemory {
 
	public static boolean isAvailable() {
		return _AVAILABLE;
	}

	public static long alloc(long size) {
		if (!_AVAILABLE) {
			throw new IllegalStateException(
				"sun.misc.Unsafe is not accessible!");
		}

		return _unsafe.allocateMemory(size);
	}

	public static void free(long address) {
		_unsafe.freeMemory(address);
	}

	public static byte getByte(long address) {
		return _unsafe.getByte(address);
	}

	public static void putByte(long address, byte value) {
		_unsafe.putByte(address, value);
	}

	private static Unsafe _unsafe;
	private static boolean _AVAILABLE = false;

	static {
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");

			field.setAccessible(true);

			_unsafe = (Unsafe)field.get(null);

			_AVAILABLE = true;
		}
		catch(Exception e) {
		// NOOP: throw exception later when allocating memory
		}
	}
	
}