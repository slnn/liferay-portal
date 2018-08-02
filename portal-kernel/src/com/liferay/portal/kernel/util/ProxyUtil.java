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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author Shuyang Zhou
 */
public class ProxyUtil {

	public static InvocationHandler getInvocationHandler(Object proxy) {
		return Proxy.getInvocationHandler(proxy);
	}

	public static Class<?> getProxyClass(
		ClassLoader classLoader, Class<?>... interfaceClasses) {

		return Proxy.getProxyClass(classLoader, interfaceClasses);
	}

	public static boolean isProxyClass(Class<?> clazz) {
		return Proxy.isProxyClass(clazz);
	}

	public static Object newProxyInstance(
		ClassLoader classLoader, Class<?>[] interfaces,
		InvocationHandler invocationHandler) {

		return Proxy.newProxyInstance(
			classLoader, interfaces, invocationHandler);
	}

}