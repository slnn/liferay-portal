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
public class AnalyzerPluginsRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(), null,
				null, getKey(),
				new Object[] {
					StringBundler.concat(
						"Elasticsearch (and OpenSearch) must have the ",
						"following analyzer plugins installed to operate ",
						"Liferay DXP properly:analysis-icu,",
						"analysis-kuromoji,analysis-smartcn,analysis-stempel. ",
						"Igone this warn if these plugins have been installed ",
						"well")
				},
				null));
	}

	@Override
	public String getCategory() {
		return "search-engine-settings-validation";
	}

	@Override
	public String getKey() {
		return "analyzer-plugins";
	}

}