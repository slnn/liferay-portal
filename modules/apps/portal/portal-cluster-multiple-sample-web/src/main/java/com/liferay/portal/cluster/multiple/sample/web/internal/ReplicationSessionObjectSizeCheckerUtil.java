/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.sample.web.internal;

import com.liferay.portal.kernel.util.PortalUtil;
import jakarta.portlet.PortletSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class ReplicationSessionObjectSizeCheckerUtil{

	public static long getSerializedSizes(PortletSession portletSession) {
		Map<String, List<Long>> sizeMap = new HashMap<>();
		long totalSize = 0;

		totalSize += _processScope(portletSession, PortletSession.APPLICATION_SCOPE, "APPLICATION_SCOPE", sizeMap);

		_logSessionDetails(sizeMap, totalSize);

		return totalSize;
	}

	public static long getSerializedSizes(HttpServletRequest httpServletRequest) {
		Map<String, List<Long>> sizeMap = new HashMap<>();
		long totalSize = 0;

		HttpServletRequest originalHttpServletRequest = PortalUtil.getOriginalServletRequest(httpServletRequest);
		HttpSession httpSession = originalHttpServletRequest.getSession();

		totalSize += _process(httpSession, sizeMap);

		_logSessionDetailsHttpSession(sizeMap, totalSize);

		return totalSize;
	}

	private static long _process(
			HttpSession httpSession, Map<String, List<Long>> sizeMap) {

		long scopeTotalSize = 0;
		Enumeration<String> names = httpSession.getAttributeNames();

		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object object = httpSession.getAttribute(name);

			try {
				Serializable serializableObject = (Serializable) object;

				long size = getSerializedSize(serializableObject);

				String key = name;

				sizeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(size);

				scopeTotalSize += size;

			} catch (ClassCastException e) {
				sizeMap.put(name, new ArrayList<>());
			}
		}
		return scopeTotalSize;
	}

	private static void _logSessionDetailsHttpSession(Map<String, List<Long>> sizeMap, long totalSize) {
		Map<String, Long> totalSizeMap = new HashMap<>();
		Map<String, Integer> countMap = new HashMap<>();

		sizeMap.forEach((key, values) -> {
			totalSizeMap.put(key, values.stream().mapToLong(Long::longValue).sum());

			countMap.put(key, values.size());
		});

		System.out.println("--- HTTP SESSION REPLICATION SIZE DIAGNOSTICS (Total: " + totalSize + " bytes) ---");

		totalSizeMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Sort by largest size
				.forEach(entry -> {
					System.out.println("Attribute: " + entry.getKey() + " | Total Objects Count: " + countMap.get(entry.getKey()) + " | Objects Size Sum: " + entry.getValue());
				});
		System.out.println("-------------------------------------------------------------------------");
	}

	private static long _processScope(
			PortletSession portletSession, int scope, String scopeName, Map<String, List<Long>> sizeMap) {

		long scopeTotalSize = 0;
		Enumeration<String> names = portletSession.getAttributeNames(scope);

		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object object = portletSession.getAttribute(name, scope);

			try {
				Serializable serializableObject = (Serializable) object;

				long size = getSerializedSize(serializableObject);

				String key = scopeName + "::" + name;

				sizeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(size);

				scopeTotalSize += size;

			} catch (ClassCastException e) {
				sizeMap.put(scopeName + "::" + name, new ArrayList<>());
			}
		}
		return scopeTotalSize;
	}

	private static void _logSessionDetails(Map<String, List<Long>> sizeMap, long totalSize) {
		Map<String, Long> totalSizeMap = new HashMap<>();
		Map<String, Integer> countMap = new HashMap<>();

		sizeMap.forEach((key, values) -> {
			totalSizeMap.put(key, values.stream().mapToLong(Long::longValue).sum());

			countMap.put(key, values.size());
		});

		System.out.println("--- PORTLET SESSION REPLICATION SIZE DIAGNOSTICS (Total: " + totalSize + " bytes) ---");

		totalSizeMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // Sort by largest size
				.forEach(entry -> {
					System.out.println("Attribute: " + entry.getKey() + " | Total Objects Count: " + countMap.get(entry.getKey()) + " | Objects Size Sum: " + entry.getValue());
				});
		System.out.println("-------------------------------------------------------------------------");
	}

	public static long getSerializedSize(Serializable object) {
		try (
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
		) {
			objectOutputStream.writeObject(object);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray().length;
		} catch (IOException ioException) {
			System.err.println("ERROR: Failed to serialize object of type " + object.getClass().getName());
			return -1;
		}
	}
}
