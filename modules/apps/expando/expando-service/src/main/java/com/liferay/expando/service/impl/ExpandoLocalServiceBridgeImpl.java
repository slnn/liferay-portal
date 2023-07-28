/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.service.impl;

import com.liferay.expando.kernel.service.ExpandoLocalServiceBridge;
import com.liferay.expando.model.ExpandoColumn;
import com.liferay.expando.model.ExpandoRow;
import com.liferay.expando.model.ExpandoTable;
import com.liferay.expando.service.ExpandoColumnLocalService;
import com.liferay.expando.service.ExpandoRowLocalService;
import com.liferay.expando.service.ExpandoTableLocalService;
import com.liferay.expando.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(service = ExpandoLocalServiceBridge.class)
public class ExpandoLocalServiceBridgeImpl
	implements ExpandoLocalServiceBridge {

	@Override
	public void deleteColumn(Object object) throws PortalException {
		_expandoColumnLocalService.deleteColumn((ExpandoColumn)object);
	}

	@Override
	public Object deleteExpandoTable(Object object) throws PortalException {
		return _expandoTableLocalService.deleteExpandoTable(
			(ExpandoTable)object);
	}

	@Override
	public void deleteRows(long classPK) {
		_expandoRowLocalService.deleteRows(classPK);
	}

	@Override
	public void deleteRows(long companyId, long classNameId, long classPK) {
		_expandoRowLocalService.deleteRows(companyId, classNameId, classPK);
	}

	@Override
	public void deleteValues(String className, long classPK) {
		_expandoValueLocalService.deleteValues(className, classPK);
	}

	@Override
	public Object fetchDefaultTable(long companyId, String className) {
		return _expandoTableLocalService.fetchDefaultTable(
			companyId, className);
	}

	@Override
	public Object fetchRow(long tableId, long classPK) {
		return _expandoRowLocalService.fetchRow(tableId, classPK);
	}

	@Override
	public ActionableDynamicQuery getExpandColumnActionableDynamicQuery() {
		return _expandoColumnLocalService.getActionableDynamicQuery();
	}

	@Override
	public ActionableDynamicQuery getExpandTableActionableDynamicQuery() {
		return _expandoTableLocalService.getActionableDynamicQuery();
	}

	@Override
	public Object updateExpandoRow(Object object) {
		return _expandoRowLocalService.updateExpandoRow((ExpandoRow)object);
	}

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

}