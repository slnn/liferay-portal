/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.model.helper.impl;

import com.liferay.expando.model.ExpandoValue;
import com.liferay.expando.model.helper.ExpandoModelHelper;
import com.liferay.expando.model.impl.ExpandoColumnImpl;
import com.liferay.expando.model.impl.ExpandoTableImpl;
import com.liferay.expando.model.impl.ExpandoValueImpl;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ExpandoModelHelper.class)
public class ExpandoModelHelperImpl implements ExpandoModelHelper {

	@Override
	public Class<?> getExpandoColumnImplClass() {
		return ExpandoColumnImpl.class;
	}

	@Override
	public Class<?> getExpandoTableImplClass() {
		return ExpandoTableImpl.class;
	}

	@Override
	public Class<?> getExpandoValueImplClass() {
		return ExpandoValueImpl.class;
	}

	@Override
	public ExpandoValue getExpandoValueInstance() {
		return new ExpandoValueImpl();
	}

}