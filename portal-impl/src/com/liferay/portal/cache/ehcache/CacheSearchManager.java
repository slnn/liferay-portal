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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

/**
 * @author Tina Tian
 */
public class CacheSearchManager {

	public static void clear(String cacheName) throws IOException {
		_instance._clear(cacheName);
	}

	public static void close() throws IOException {
		_instance._close();
	}

	public static void close(String cacheName) throws IOException {
		_instance._close(cacheName);
	}

	public static void removeDocument(String cacheName, Object key)
		throws IOException {

		_instance._removeDocument(cacheName, key);
	}

	public static Set<String> search(String cacheName, Query query)
		throws IOException {

		return _instance._search(cacheName, query);
	}

	public static void updateDocument(
			String cacheName, Term term, Document document)
		throws IOException {

		_instance._updateDocument(cacheName, term, document);
	}

	private void _clear(String cacheName) throws IOException {
		CacheIndexAccessor cacheIndexAccessor = _indexAccessors.get(cacheName);

		if (cacheIndexAccessor != null) {
			cacheIndexAccessor.clear();
		}
	}

	private void _close() throws IOException {
		for (CacheIndexAccessor cacheIndexAccessor : _indexAccessors.values()) {
			cacheIndexAccessor.close();
		}
	}

	private void _close(String cacheName) throws IOException {
		CacheIndexAccessor cacheIndexAccessor = _indexAccessors.get(cacheName);

		if (cacheIndexAccessor != null) {
			cacheIndexAccessor.close();
		}
	}

	private void _removeDocument(String cacheName, Object key)
		throws IOException {

		CacheIndexAccessor cacheIndexAccessor = _indexAccessors.get(cacheName);

		if (cacheIndexAccessor != null) {
			cacheIndexAccessor.removeDocument(key);
		}
	}

	private Set<String> _search(String cacheName, Query query)
		throws IOException {

		CacheIndexAccessor cacheIndexAccessor = _indexAccessors.get(cacheName);

		if (cacheIndexAccessor != null) {
			return cacheIndexAccessor.search(query);
		}

		return Collections.emptySet();
	}

	private void _updateDocument(String cacheName, Term term, Document document)
		throws IOException {

		CacheIndexAccessor cacheIndexAccessor = _indexAccessors.get(cacheName);

		if (cacheIndexAccessor == null) {
			synchronized(this) {
				cacheIndexAccessor = _indexAccessors.get(cacheName);

				if (cacheIndexAccessor == null) {
					cacheIndexAccessor = new CacheIndexAccessor();

					_indexAccessors.put(cacheName, cacheIndexAccessor);
				}
			}
		}

		cacheIndexAccessor.updateDocument(term, document);
	}

	private static CacheSearchManager _instance = new CacheSearchManager();

	private Map<String, CacheIndexAccessor> _indexAccessors =
		new ConcurrentHashMap<String, CacheIndexAccessor>();

}