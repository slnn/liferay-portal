/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rules.internal;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class PortalDeveloperPropertiesRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String[] includeAndOverrides = PropsUtil.getArray(
			"include-and-override");

		boolean hasDeveloperProperties = false;

		for (String includeAndOverride : includeAndOverrides) {
			if (includeAndOverride.equals("portal-developer.properties")) {
				hasDeveloperProperties = true;

				break;
			}
		}

		if (hasDeveloperProperties) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.MEDIUM, getCategory(),
					"portal-developer.properties included", null, getKey(),
					new Object[] {
						"Do not include portal-developer.properties in " +
							"product environment."
					},
					null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(),
				"portal-developer.properties is not included", null, getKey(),
				new Object[] {
					"portal-developer.properties is not included in product " +
						"environment."
				},
				null));
	}

	@Override
	public String getCategory() {
		return "portal-properties-configuration";
	}

	@Override
	public String getKey() {
		return "portal-developer-properties";
	}

}