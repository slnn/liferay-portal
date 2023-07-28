/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Lily Chi
 */
public interface ExpandoLocalServiceBridge {

	public void deleteColumn(Object object) throws PortalException;

	public Object deleteExpandoTable(Object object) throws PortalException;

	public void deleteRows(long classPK);

	public void deleteRows(long companyId, long classNameId, long classPK);

	public void deleteValues(String className, long classPK);

	public Object fetchDefaultTable(long companyId, String className);

	public Object fetchRow(long tableId, long classPK);

	public ActionableDynamicQuery getExpandColumnActionableDynamicQuery();

	public ActionableDynamicQuery getExpandTableActionableDynamicQuery();

	public Object updateExpandoRow(Object object);

}