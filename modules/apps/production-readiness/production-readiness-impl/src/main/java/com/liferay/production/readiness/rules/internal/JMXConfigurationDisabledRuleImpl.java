/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rules.internal;

import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class JMXConfigurationDisabledRuleImpl
	implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		List<String> inputArguments = runtimeMXBean.getInputArguments();

		boolean jmxEnabled = false;
		String enabledArg = null;

		for (String arg : inputArguments) {
			if (arg.startsWith("-Dcom.sun.management.jmxremote")) {
				jmxEnabled = true;
				enabledArg = arg;

				break;
			}
		}

		if (jmxEnabled) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					"JMX Configuration has been enabled (" + enabledArg + ")",
					null, getKey(), new Object[] {"JMX should be disabled"},
					null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.LOW, getCategory(), null,
				null, getKey(), new Object[] {"JMX has been disabled"}, null));
	}

	@Override
	public String getCategory() {
		return "jvm-&-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "jmx-configuration-disabled";
	}

}