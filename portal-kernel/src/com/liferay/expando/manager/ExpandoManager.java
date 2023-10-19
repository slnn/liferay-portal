/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.manager;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;

/**
 * @author Lily Chi
 */
public interface ExpandoManager {

	public ExpandoTable fetchDefaultTable(long companyId, String className);

	public ExpandoRow fetchRow(long tableId, long classPK);

}