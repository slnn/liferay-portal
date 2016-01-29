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
import com.liferay.portal.kernel.util.ReflectionUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Dante Wang
 */
public class LiferayBeansModelCache extends ModelCache {

	public LiferayBeansModelCache(
			BeansWrapper beansWrapper, Map<String, Object> helperUtilities)
		throws Exception {

		_beansWrapper = beansWrapper;

		_method = ReflectionUtil.getDeclaredMethod(
			BeansWrapper.class, "getModelFactory", Class.class);

		Map<Object, TemplateModel> helperUtilityCache = new HashMap<>();

		for (Object object : helperUtilities.values()) {
			if (object == null) {
				continue;
			}

			try {
				helperUtilityCache.put(object, getInstance(object));
			}
			catch (NullPointerException e) {

				// Sliently bypass object that can't be added into the hashset

			}
		}

		_helperUtilityCache = Collections.unmodifiableMap(helperUtilityCache);

		setUseCache(true);
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

		if ((templateModel == null) && isCacheable(object)) {

			// Level 2: least frequently used cache for all cacheable objects

			templateModel = _modelCache.get(object);

			if (templateModel == null) {
				templateModel = create(object);

				_modelCache.put(object, templateModel);
			}
		}
		else {
			return create(object);
		}

		return templateModel;
	}

	@Override
	protected TemplateModel create(Object object) {
		Class clazz = object.getClass();

		try {
			ModelFactory modelFactory = (ModelFactory)_method.invoke(
				_beansWrapper, clazz);

			return modelFactory.create(object, _beansWrapper);
		}
		catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected boolean isCacheable(Object object) {
		Class clazz = object.getClass();

		if (Map.class.isAssignableFrom(clazz) ||
				Collection.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) || Boolean.class == clazz ||
				ResourceBundle.class.isAssignableFrom(clazz) ||
				Iterator.class.isAssignableFrom(clazz) ||
				Enumeration.class.isAssignableFrom(clazz) || clazz.isArray()) {

			return false;
		}

		return true;
	}

	private final BeansWrapper _beansWrapper;
	private final Map<Object, TemplateModel> _helperUtilityCache;
	private final Method _method;
	private final ConcurrentLFUCache<Object, TemplateModel> _modelCache =
		new ConcurrentLFUCache<>(1000);

}