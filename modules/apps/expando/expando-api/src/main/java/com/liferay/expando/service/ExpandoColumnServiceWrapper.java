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

package com.liferay.expando.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ExpandoColumnService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoColumnService
 * @generated
 */
public class ExpandoColumnServiceWrapper
	implements ExpandoColumnService, ServiceWrapper<ExpandoColumnService> {

	public ExpandoColumnServiceWrapper() {
		this(null);
	}

	public ExpandoColumnServiceWrapper(
		ExpandoColumnService expandoColumnService) {

		_expandoColumnService = expandoColumnService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoColumnService.getOSGiServiceIdentifier();
	}

	@Override
	public ExpandoColumnService getWrappedService() {
		return _expandoColumnService;
	}

	@Override
	public void setWrappedService(ExpandoColumnService expandoColumnService) {
		_expandoColumnService = expandoColumnService;
	}

	private ExpandoColumnService _expandoColumnService;

}