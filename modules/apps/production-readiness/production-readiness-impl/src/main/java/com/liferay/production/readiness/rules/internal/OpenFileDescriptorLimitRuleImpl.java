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
public class OpenFileDescriptorLimitRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		return Collections.singletonList(
			new Result(
				Result.Status.FAIL, Result.Severity.HIGH, getCategory(), null,
				null, getKey(),
				new Object[] {
					StringBundler.concat(
						"Please Increase the file descriptor limit for ",
						"Elasticsearch in Linux by ulimit, make sure the ",
						"value of ulimit -n is not less than 65536. Igone ",
						"this warn if the file descriptor limit has been set ",
						"with an appropriate value.")
				},
				"https://www.elastic.co/docs/deploy-manage/deploy/self-" +
					"managed/file-descriptors"));
	}

	@Override
	public String getCategory() {
		return "search-engine-settings-validation";
	}

	@Override
	public String getKey() {
		return "open=file-descriptior-limit";
	}

}