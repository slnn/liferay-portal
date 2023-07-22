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

package com.liferay.portal.kernel.service.context.factory.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.context.factory.ServiceContextFactory;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class ServiceContextFactoryUtil {

	public static ServiceContext getInstance(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return _serviceContextFactory.getInstance(httpServletRequest);
	}

	public static ServiceContext getInstance(PortletRequest portletRequest)
		throws PortalException {

		return _serviceContextFactory.getInstance(portletRequest);
	}

	public static ServiceContext getInstance(
			String className, HttpServletRequest httpServletRequest)
		throws PortalException {

		return _serviceContextFactory.getInstance(
			className, httpServletRequest);
	}

	public static ServiceContext getInstance(
			String className, PortletRequest portletRequest)
		throws PortalException {

		return _serviceContextFactory.getInstance(className, portletRequest);
	}

	private static volatile ServiceContextFactory _serviceContextFactory =
		ServiceProxyFactory.newServiceTrackedInstance(
			ServiceContextFactory.class, ServiceContextFactoryUtil.class,
			"_serviceContextFactory", false);

}