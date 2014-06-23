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

import com.liferay.portal.kernel.cache.IndexedFieldExtractor;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheWrapper;

import java.io.IOException;
import java.io.Serializable;

import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

/**
 * @author Tina Tian
 */
public class SearchablePortalCache <K extends Serializable, V>
	extends PortalCacheWrapper<K, V> {

	public static final String FIELD_UID = "key";

	public SearchablePortalCache(
		PortalCache<K, V> portalCache,
		IndexedFieldExtractor<K, V> indexedFieldExtractor) {

		super(portalCache);

		_indexedFiledExtractor = indexedFieldExtractor;
	}

	@Override
	public void put(K key, V value) {
		doPut(key, value, false, -1);
	}

	@Override
	public void put(K key, V value, int timeToLive) {
		if (timeToLive < 0) {
			throw new IllegalArgumentException("Time to live is negative");
		}

		doPut(key, value, false, timeToLive);
	}

	@Override
	public void putQuiet(K key, V value) {
		doPut(key, value, true, -1);
	}

	@Override
	public void putQuiet(K key, V value, int timeToLive) {
		if (timeToLive < 0) {
			throw new IllegalArgumentException("Time to live is negative");
		}

		doPut(key, value, true, timeToLive);
	}

	@Override
	public void remove(K key) {
		portalCache.remove(key);

		try {
			CacheSearchManager.removeDocument(getName(), key);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeAll() {
		portalCache.removeAll();

		try {
			CacheSearchManager.clear(getName());
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	protected void doPut(K key, V value, boolean quiet, int timeToLive) {
		if (key == null) {
			throw new IllegalArgumentException("Key is null");
		}

		if (value == null) {
			throw new IllegalArgumentException("Value is null");
		}

		if (quiet) {
			if (timeToLive >= 0) {
				portalCache.putQuiet(key, value, timeToLive);
			}
			else {
				portalCache.putQuiet(key, value);
			}
		}
		else {
			if (timeToLive >= 0) {
				portalCache.put(key, value, timeToLive);
			}
			else {
				portalCache.put(key, value);
			}
		}

		Map<String, String> indexedFields =
			_indexedFiledExtractor.getIndexedFields(key, value);

		Document document = new Document();

		for (Map.Entry<String, String> entry : indexedFields.entrySet()) {
			Field field = new Field(
				entry.getKey(), entry.getValue(), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);

			document.add(field);
		}

		Field uidField = null;

		if (key instanceof String) {
			uidField = new Field(
				FIELD_UID, String.valueOf(key), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);
		}
		else {
			uidField = new Field(
				FIELD_UID, String.valueOf(key), Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS);
		}

		document.add(uidField);

		Term term = new Term(FIELD_UID, String.valueOf(key));

		try {
			CacheSearchManager.updateDocument(getName(), term, document);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private IndexedFieldExtractor<K, V> _indexedFiledExtractor;

}