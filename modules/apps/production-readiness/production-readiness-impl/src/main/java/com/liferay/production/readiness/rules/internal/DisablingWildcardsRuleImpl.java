/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rules.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.util.Collection;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class DisablingWildcardsRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(), null,
				null, getKey(),
				new Object[] {
					StringBundler.concat(
						"In order to prevent accidental deletion of all ",
						"indices, please set action. destructive_requires_",
						"name to true. Igone this warn if action.",
						"destructive_requires_name has been set with true")
				},
				"https://www.elastic.co/docs/api/doc/elasticsearch-serverless" +
					"/operation/operation-indices-delete"));
	}

	@Override
	public String getCategory() {
		return "search-engine-settings-validation";
	}

	@Override
	public String getKey() {
		return "disable-wildcards";
	}

}