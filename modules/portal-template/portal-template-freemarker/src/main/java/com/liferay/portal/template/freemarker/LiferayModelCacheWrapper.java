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

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
			int maxSize)
		throws Exception {

		_modelCache = modelCache;

		modelCache.setUseCache(false);

		Map<Object, TemplateModel> helperUtilityCache = new IdentityHashMap<>();

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

		_templateModelCache = new IdentityConcurrentLFUCache<>(maxSize);
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

		// Level 1: cache for helper utilities

		TemplateModel templateModel = _helperUtilityCache.get(object);

		if (templateModel == null) {
			if (isCacheable(object)) {

				// Level 2: least frequently used cache for all cacheable
				// objects

				SoftReference<TemplateModel> modelReference =
					_templateModelCache.get(object);

				if (modelReference == null) {
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
		return Boolean.class != object.getClass();
	}

	private final Map<Object, TemplateModel> _helperUtilityCache;
	private final ModelCache _modelCache;
	private final
		IdentityConcurrentLFUCache<Object, SoftReference<TemplateModel>>
			_templateModelCache;

}