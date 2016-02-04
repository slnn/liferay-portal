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

package com.liferay.portal.template.freemarker;

import com.liferay.portal.kernel.concurrent.ConcurrentLFUCache;

import freemarker.ext.util.ModelCache;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;

import java.lang.ref.SoftReference;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A ModelCache wrapper which adds custom implementation of TemplateModel cache
 * on top of ModelCache and delegates TemplateModel creation to its wrapped
 * ModelCache.
 *
 * @author Dante Wang
 */
public class LiferayModelCacheWrapper extends ModelCache {

	public LiferayModelCacheWrapper(
		ModelCache modelCache, Map<String, Object> helperUtilities,
		int maxSize) throws Exception {

		this(
			modelCache, helperUtilities, maxSize,
			Runtime.getRuntime().availableProcessors() * 2);
	}

	public LiferayModelCacheWrapper(
			ModelCache modelCache, Map<String, Object> helperUtilities,
			int maxSize, int concurrencyLevel)
		throws Exception {

		_modelCache = modelCache;

		modelCache.setUseCache(false);

		Map<Object, TemplateModel> helperUtilityCache = new IdentityHashMap<>();

		for (Object object : helperUtilities.values()) {
			try {
				helperUtilityCache.put(object, _modelCache.getInstance(object));
			}
			catch (NullPointerException npe) {
			}
		}

		_helperUtilityCache = Collections.unmodifiableMap(helperUtilityCache);

		_caches = new ConcurrentLFUCache[concurrencyLevel];

		int maxCacheSize = (int)Math.ceil(
			((double)maxSize) / ((double)concurrencyLevel));

		for (int i = 0; i < concurrencyLevel; i++) {
			_caches[i] = new ConcurrentLFUCache<>(maxCacheSize);
		}
	}

	public void clearCache() {
		synchronized (this) {
			for (int i = 0; i < _caches.length; i++) {
				_caches[i].clear();
			}
		}
	}

	@Override
	public TemplateModel getInstance(Object object) {
		if (object instanceof TemplateModel) {
			return (TemplateModel)object;
		}

		if (object instanceof TemplateModelAdapter) {
			return ((TemplateModelAdapter)object).getTemplateModel();
		}

		// Level 1: cache for helper utilities

		TemplateModel templateModel = _helperUtilityCache.get(object);

		if (templateModel == null) {
			if (isCacheable(object)) {

				// Level 2: least frequently used cache for all cacheable
				// objects

				ObjectKey objectKey = new ObjectKey(object);

				SoftReference<TemplateModel> modelReference = _get(objectKey);

				if (modelReference == null) {
					templateModel = _modelCache.getInstance(object);

					_put(objectKey, new SoftReference<>(templateModel));
				}
				else {
					templateModel = modelReference.get();
				}
			}
			else {
				templateModel = _modelCache.getInstance(object);
			}
		}

		return templateModel;
	}

	@Override
	public boolean getUseCache() {
		return true;
	}

	@Override
	public void setUseCache(boolean useCache) {

		// Avoiding creating the cache map in super class

	}

	public class ObjectKey {

		@Override
		public int hashCode() {
			return _hashCode;
		}

		private ObjectKey(Object object) {
			_hashCode = System.identityHashCode(object);
		}

		private final int _hashCode;

	}

	@Override
	protected TemplateModel create(Object object) {

		// TemplateModel creation is delegated to wrapped ModelCache

		return null;
	}

	@Override
	protected boolean isCacheable(Object object) {
		return Boolean.class != object.getClass();
	}

	private SoftReference<TemplateModel> _get(ObjectKey key) {
		return _mapToCache(key).get(key);
	}

	private ConcurrentLFUCache<ObjectKey, SoftReference<TemplateModel>>
		_mapToCache(ObjectKey objectKey) {

		int hash = objectKey.hashCode() * 31;

		hash = Math.abs(hash % _caches.length);

		return _caches[hash];
	}

	private void _put(ObjectKey key, SoftReference<TemplateModel> value) {
		_mapToCache(key).put(key, value);
	}

	private final ConcurrentLFUCache<ObjectKey, SoftReference<TemplateModel>>[]
		_caches;
	private final Map<Object, TemplateModel> _helperUtilityCache;
	private final ModelCache _modelCache;

}