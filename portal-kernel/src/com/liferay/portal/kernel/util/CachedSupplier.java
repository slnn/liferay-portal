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

package com.liferay.portal.kernel.util;

import java.util.function.Supplier;

/**
 * @author Preston Crary
 */
public abstract class CachedSupplier<T> implements Supplier<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T get() {
		if (_result == null) {
			_result = doGet();

			if (_result == null) {
				_result = _nullResult;
			}
		}

		if (_result == _nullResult) {
			return null;
		}

		return (T)_result;
	}

	protected abstract T doGet();

	private static final Object _nullResult = new Object();

	private Object _result;

}