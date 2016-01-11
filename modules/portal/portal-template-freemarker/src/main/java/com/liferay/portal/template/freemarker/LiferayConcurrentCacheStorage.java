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

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPoolUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.StringPool;

import freemarker.cache.ConcurrentCacheStorage;

import java.io.Serializable;

/**
 * @author Dante Wang
 */
public class LiferayConcurrentCacheStorage implements ConcurrentCacheStorage {

	public LiferayConcurrentCacheStorage() {
		String portalCacheName = TemplateResource.class.getName();

		portalCacheName = portalCacheName.concat(StringPool.POUND).concat(
			TemplateConstants.LANG_TYPE_FTL);

		_portalCache = SingleVMPoolUtil.getPortalCache(portalCacheName);
	}

	@Override
	public void clear() {
		_portalCache.removeAll();
	}

	@Override
	public Object get(Object key) {
		return _portalCache.get((Serializable)key);
	}

	@Override
	public boolean isConcurrent() {
		return true;
	}

	@Override
	public void put(Object key, Object value) {
		_portalCache.put((Serializable)key, value);
	}

	@Override
	public void remove(Object key) {
		_portalCache.remove((Serializable)key);
	}

	private final PortalCache<Serializable, Object> _portalCache;

}