/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.context.function;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.context.factory.util.ServiceContextFactoryUtil;

import java.util.function.Function;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author André de Oliveira
 */
public class ServiceContextFunction
	implements Function<String, ServiceContext> {

	public ServiceContextFunction(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;

		_portletRequest = null;
	}

	public ServiceContextFunction(PortletRequest portletRequest) {
		_portletRequest = portletRequest;

		_httpServletRequest = null;
	}

	@Override
	public ServiceContext apply(String className) {
		try {
			if (_portletRequest != null) {
				return ServiceContextFactoryUtil.getInstance(
					className, _portletRequest);
			}

			return ServiceContextFactoryUtil.getInstance(
				className, _httpServletRequest);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletRequest _portletRequest;

}