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
import com.liferay.portal.kernel.util.HashUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Bruno Basto
 */
public class SoyTofuCache {

	public static SoyTofu add(
		List<TemplateResource> templateResources, SoyTofu soyTofu) {

		Set<SoyTemplateResource> key = _getKey(templateResources);

		return _tofuCache.put(key, soyTofu);
	}

	public static SoyTofu get(List<TemplateResource> templateResources) {
		Set<SoyTemplateResource> key = _getKey(templateResources);

		return _tofuCache.get(key);
	}

	public static void removeIfAny(List<TemplateResource> templateResources) {
		for (TemplateResource templateResource : templateResources) {
			SoyTemplateResource soyTemplateResource = new SoyTemplateResource(
				templateResource);

			Set<Set<SoyTemplateResource>> keys = _tofuCache.keySet();

			for (Set<SoyTemplateResource> key : keys) {
				if (key.contains(soyTemplateResource)) {
					_tofuCache.remove(key);
				}
			}
		}
	}

	protected static class SoyTemplateResource {

		public SoyTemplateResource(TemplateResource templateResource) {
			_templateResource = templateResource;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof SoyTemplateResource)) {
				return false;
			}

			SoyTemplateResource soyTemplateResource = (SoyTemplateResource)obj;

			if (_templateResource.equals(
					soyTemplateResource._templateResource) &&
				(_templateResource.getLastModified() ==
					soyTemplateResource._templateResource.getLastModified())) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, _templateResource.hashCode());

			return HashUtil.hash(hash, _templateResource.getLastModified());
		}

		private final TemplateResource _templateResource;

	}

	private static Set<SoyTemplateResource> _getKey(
		List<TemplateResource> templateResources) {

		return templateResources.stream().map(SoyTemplateResource::new).collect(
			Collectors.toSet());
	}

	private static final ConcurrentHashMap<Set<SoyTemplateResource>, SoyTofu>
		_tofuCache = new ConcurrentHashMap<>();

}