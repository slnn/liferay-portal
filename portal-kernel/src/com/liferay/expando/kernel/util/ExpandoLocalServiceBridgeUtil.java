/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.util;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoLocalServiceBridge;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ServiceProxyFactory;

/**
 * @author Lily Chi
 */
public class ExpandoLocalServiceBridgeUtil {

	public static void deleteColumn(ExpandoColumn column)
		throws PortalException {

		_expandoLocalServiceBridge.deleteColumn(column);
	}

	public static ExpandoTable deleteExpandoTable(ExpandoTable expandoTable)
		throws PortalException {

		return (ExpandoTable)_expandoLocalServiceBridge.deleteExpandoTable(
			expandoTable);
	}

	public static void deleteRows(long classPK) {
		_expandoLocalServiceBridge.deleteRows(classPK);
	}

	public static void deleteRows(
		long companyId, long classNameId, long classPK) {

		_expandoLocalServiceBridge.deleteRows(companyId, classNameId, classPK);
	}

	public static void deleteValues(String className, long classPK) {
		_expandoLocalServiceBridge.deleteValues(className, classPK);
	}

	public static ExpandoTable fetchDefaultTable(
		long companyId, String className) {

		return (ExpandoTable)_expandoLocalServiceBridge.fetchDefaultTable(
			companyId, className);
	}

	public static ExpandoRow fetchRow(long tableId, long classPK) {
		return (ExpandoRow)_expandoLocalServiceBridge.fetchRow(
			tableId, classPK);
	}

	public static ActionableDynamicQuery
		getExpandColumnActionableDynamicQuery() {

		return _expandoLocalServiceBridge.
			getExpandColumnActionableDynamicQuery();
	}

	public static ActionableDynamicQuery
		getExpandTableActionableDynamicQuery() {

		return _expandoLocalServiceBridge.
			getExpandTableActionableDynamicQuery();
	}

	public static ExpandoRow updateExpandoRow(ExpandoRow expandoRow) {
		return (ExpandoRow)_expandoLocalServiceBridge.updateExpandoRow(
			expandoRow);
	}

	private static volatile ExpandoLocalServiceBridge
		_expandoLocalServiceBridge =
			ServiceProxyFactory.newServiceTrackedInstance(
				ExpandoLocalServiceBridge.class,
				ExpandoLocalServiceBridgeUtil.class,
				"_expandoLocalServiceBridge", false);

}