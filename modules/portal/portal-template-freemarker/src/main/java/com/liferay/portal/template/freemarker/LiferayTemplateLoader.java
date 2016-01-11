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

import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.template.TemplateResourceThreadLocal;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;

import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dante Wang
 */
public class LiferayTemplateLoader implements TemplateLoader {

	public LiferayTemplateLoader(
		FreeMarkerEngineConfiguration freemarkerEngineConfiguration,
		TemplateResourceLoader templateResourceLoader) {

		_freemarkerEngineConfiguration = freemarkerEngineConfiguration;
		_templateResourceLoader = templateResourceLoader;
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		Reader reader = _templateResourceReaderCache.get(
			(TemplateResource)templateSource);

		reader.close();
	}

	@Override
	public Object findTemplateSource(String templateId) throws IOException {
		for (String macroTemplateId :
				_freemarkerEngineConfiguration.macroLibrary()) {

			int pos = macroTemplateId.indexOf(" as ");

			if (pos != -1) {
				macroTemplateId = macroTemplateId.substring(0, pos);
			}

			if (templateId.equals(macroTemplateId)) {

				// This template is provided by the portal, so invoke it from an
				// access controller

				try {
					return AccessController.doPrivileged(
						new TemplatePrivilegedExceptionAction(macroTemplateId));
				}
				catch (PrivilegedActionException pae) {
					throw (IOException)pae.getException();
				}
			}
		}

		return _getTemplateResource(templateId);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return ((TemplateResource)templateSource).getLastModified();
	}

	@Override
	public Reader getReader(Object templateSource, String encoding)
		throws IOException {

		Reader reader = ((TemplateResource)templateSource).getReader();

		_templateResourceReaderCache.put(
			((TemplateResource)templateSource), reader);

		return reader;
	}

	private TemplateResource _getTemplateResource(String templateId)
		throws IOException {

		if (templateId == null) {
			throw new IllegalArgumentException("Argument \"name\" is null");
		}

		TemplateResource templateResource = null;

		if (templateId.startsWith(
				TemplateConstants.TEMPLATE_RESOURCE_UUID_PREFIX)) {

			templateResource = TemplateResourceThreadLocal.getTemplateResource(
				TemplateConstants.LANG_TYPE_FTL);
		}
		else {
			try {
				templateResource = _templateResourceLoader.getTemplateResource(
					templateId);
			}
			catch (Exception e) {
				templateResource = null;
			}
		}

		if (templateResource == null) {
			throw new IOException(
				"Unable to find FreeMarker template with ID " + templateId);
		}

		return templateResource;
	}

	private final FreeMarkerEngineConfiguration _freemarkerEngineConfiguration;
	private final TemplateResourceLoader _templateResourceLoader;
	private final Map<TemplateResource, Reader> _templateResourceReaderCache =
		new ConcurrentHashMap<>();

	private class TemplatePrivilegedExceptionAction
		implements PrivilegedExceptionAction<TemplateResource> {

		public TemplatePrivilegedExceptionAction(String templateId) {
			_templateId = templateId;
		}

		@Override
		public TemplateResource run() throws Exception {
			return _getTemplateResource(_templateId);
		}

		private final String _templateId;

	}

}