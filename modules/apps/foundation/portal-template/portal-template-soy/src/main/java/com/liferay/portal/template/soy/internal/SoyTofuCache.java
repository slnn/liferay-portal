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

package com.liferay.portal.template.soy.internal;

import com.google.template.soy.tofu.SoyTofu;

import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Bruno Basto
 */
public class SoyTofuCache {

	public static SoyTofu add(
		List<TemplateResource> templateResources, SoyTofu soyTofu) {

		Set<TemplateResource> key = _getKey(templateResources);

		return _tofuCache.put(key, soyTofu);
	}

	public static SoyTofu get(List<TemplateResource> templateResources) {
		Set<TemplateResource> key = _getKey(templateResources);

		return _tofuCache.get(key);
	}

	public static void removeIfAny(List<TemplateResource> templateResources) {
		for (TemplateResource templateResource : templateResources) {
			Set<Set<TemplateResource>> keys = _tofuCache.keySet();

			for (Set<TemplateResource> key : keys) {
				if (key.contains(templateResource)) {
					_tofuCache.remove(key);
				}
			}
		}
	}

	private static Set<TemplateResource> _getKey(
		List<TemplateResource> templateResources) {

		return SetUtil.fromList(templateResources);
	}

	private static final ConcurrentHashMap<Set<TemplateResource>, SoyTofu>
		_tofuCache = new ConcurrentHashMap<>();

}