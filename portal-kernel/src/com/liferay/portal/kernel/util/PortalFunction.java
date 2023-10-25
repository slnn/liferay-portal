/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Lily Chi
 */
@FunctionalInterface
public interface PortalFunction<T, R> {

	public R apply(T t) throws PortalException;

}