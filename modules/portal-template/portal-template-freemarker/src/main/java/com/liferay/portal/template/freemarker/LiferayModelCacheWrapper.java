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

import freemarker.ext.util.ModelCache;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;

import java.lang.ref.SoftReference;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ModelCache wrapper which adds custom implementation of TemplateModel cache
 * on top of ModelCache and delegates TemplateModel creation to its wrapped
 * ModelCache.
 *
 * @author Dante Wang
 */
public class LiferayModelCacheWrapper extends ModelCache {

	public LiferayModelCacheWrapper(
			ModelCache modelCache, Map<String, Object> helperUtilities)
		throws Exception {

		_modelCache = modelCache;

		modelCache.setUseCache(false);

		Map<Object, TemplateModel> helperUtilityCache = new HashMap<>();

		for (Object object : helperUtilities.values()) {
			if (object == null) {
				continue;
			}

			try {
				helperUtilityCache.put(object, _modelCache.getInstance(object));
			}
			catch (NullPointerException e) {

				// Sliently bypass object that can't be added into the hashset

			}
		}

		_helperUtilityCache = Collections.unmodifiableMap(helperUtilityCache);
	}

	public void clearCache() {
		_templateModelCache.clear();
	}

	@Override
	public TemplateModel getInstance(Object object) {
		if (object instanceof TemplateModel) {
			return (TemplateModel)object;
		}

		if (object instanceof TemplateModelAdapter) {
			return ((TemplateModelAdapter)object).getTemplateModel();
		}

		_cacheInvocationCount.getAndIncrement();

		// Level 1: cache for helper utilities

		TemplateModel templateModel = _helperUtilityCache.get(object);

		if (templateModel == null) {
			_cacheLevel1MissCount.getAndIncrement();

			if (isCacheable(object)) {

				// Level 2: least frequently used cache for all cacheable
				// objects

				SoftReference<TemplateModel> modelReference =
					_templateModelCache.get(object);

				if (modelReference == null) {
					_cacheLevel2MissCount.getAndIncrement();

					templateModel = _modelCache.getInstance(object);

					_templateModelCache.put(
						object, new SoftReference<>(templateModel));
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

	@Override
	protected TemplateModel create(Object object) {

		// TemplateModel creation is delegated to wrapped ModelCache

		return null;
	}

	@Override
	protected boolean isCacheable(Object object) {
		Class clazz = object.getClass();

		if (Map.class.isAssignableFrom(clazz) ||
			Collection.class.isAssignableFrom(clazz) ||
			Number.class.isAssignableFrom(clazz) ||
			Date.class.isAssignableFrom(clazz) || (Boolean.class == clazz) ||
			ResourceBundle.class.isAssignableFrom(clazz) ||
			Iterator.class.isAssignableFrom(clazz) ||
			Enumeration.class.isAssignableFrom(clazz) || clazz.isArray()) {

			return false;
		}

		return true;
	}

	private final AtomicInteger _cacheInvocationCount = new AtomicInteger(0);
	private final AtomicInteger _cacheLevel1MissCount = new AtomicInteger(0);
	private final AtomicInteger _cacheLevel2MissCount = new AtomicInteger(0);
	private final Map<Object, TemplateModel> _helperUtilityCache;
	private final ModelCache _modelCache;
	private final IdentityConcurrentLFUCache<
		Object, SoftReference<TemplateModel>> _templateModelCache =
			new IdentityConcurrentLFUCache<>(2000);

}