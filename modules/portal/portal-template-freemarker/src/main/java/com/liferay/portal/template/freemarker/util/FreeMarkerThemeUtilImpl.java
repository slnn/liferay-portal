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

package com.liferay.portal.template.freemarker.util;

import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.taglib.portletext.RuntimeTag;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

/**
 * @author ChemaBalsas
 */
public class FreeMarkerThemeUtilImpl implements FreeMarkerThemeUtil {

	public FreeMarkerThemeUtilImpl(
		ServletContext servletContext, HttpServletRequest request,
		HttpServletResponse response, Map<String, Object> contextObjects) {

		_servletContext = servletContext;
		_request = request;
		_response = response;
		_contextObjects = contextObjects;

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		_pageContext = jspFactory.getPageContext(
			new JSPSupportServlet(_servletContext), _request, _response, null,
			false, 0, false);
	}

	@Override
	public void include(ServletContext servletContext, String page)
		throws Exception {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(page);

		requestDispatcher.include(_request, _response);
	}

	@Override
	public void include(String page) throws Exception {
		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				_servletContext, page);

		requestDispatcher.include(_request, _response);
	}

	@Override
	public void runtime(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction)
		throws Exception {

		RuntimeTag.doTag(
			portletProviderClassName, portletProviderAction, StringPool.BLANK,
			null, null, _pageContext, _request, _response);
	}

	@Override
	public void runtime(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction, String instanceId,
			String defaultPreferences)
		throws Exception {

		RuntimeTag.doTag(
			portletProviderClassName, portletProviderAction, instanceId, null,
			defaultPreferences, _pageContext, _request, _response);
	}

	private final Map<String, Object> _contextObjects;
	private final PageContext _pageContext;
	private final HttpServletRequest _request;
	private final HttpServletResponse _response;
	private final ServletContext _servletContext;

}