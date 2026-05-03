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
public class ESVirtualMemorySizeUpperLimitRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(), null,
				null, getKey(),
				new Object[] {
					StringBundler.concat(
						"The value of vm.max_map_count for the Elasticsearch ",
						"server should be not less than 262144 to avoid the ",
						"Out of Memory issue in Linux. Igone this warn if the ",
						"virtual memory of Elasticsearch has been set ",
						"appropriatly")
				},
				"https://www.elastic.co/docs/deploy-manage/deploy/self-" +
					"managed/vm-max-map-count"));
	}

	@Override
	public String getCategory() {
		return "search-engine-settings-validation";
	}

	@Override
	public String getKey() {
		return "es-virtual-memory-size-upper-limit";
	}

}