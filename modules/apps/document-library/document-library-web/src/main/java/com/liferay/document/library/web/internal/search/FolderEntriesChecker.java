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

package com.liferay.document.library.web.internal.search;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Tina Tian
 */
public class FolderEntriesChecker extends EntriesChecker {

	public FolderEntriesChecker(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(liferayPortletRequest, liferayPortletResponse);
	}

	protected String getName(String primaryKey) {
		try {
			Folder folder = DLAppServiceUtil.getFolder(
				GetterUtil.getLong(primaryKey));

			if (folder != null) {
				return Folder.class.getSimpleName();
			}

			return null;
		}
		catch (PortalException pe) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to find folder " + primaryKey, pe);
			}

			return StringPool.BLANK;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FolderEntriesChecker.class);

}