/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.display.context;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;
import com.liferay.production.readiness.ignore.service.ProductionReadinessIgnoreLocalServiceUtil;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Lily Chi
 */
public class ProductionReadinessDisplayContext {

	public ProductionReadinessDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public int getFailedCount() {
		_calculate();

		return _failedCount;
	}

	public int getIgnoredCount() {
		_calculate();

		return _ignoredCount;
	}

	public int getPassedCount() {
		_calculate();

		return _passedCount;
	}

	public List<RuleResult> getRuleResults() {
		_calculate();

		String status = ParamUtil.getString(_renderRequest, "status");

		if (status.isEmpty()) {
			return _ruleResults;
		}

		List<RuleResult> filteredResults = new ArrayList<>();

		for (RuleResult ruleResult : _ruleResults) {
			if (ruleResult.ignored) {
				if (status.equals("ignored")) {
					filteredResults.add(ruleResult);
				}
			}
			else {
				if (status.equals("passed") &&
					(ruleResult.result.getStatus() == Result.Status.PASS)) {

					filteredResults.add(ruleResult);
				}
				else if (status.equals("failed") &&
						 (ruleResult.result.getStatus() == Result.Status.FAIL)) {

					filteredResults.add(ruleResult);
				}
			}
		}

		return filteredResults;
	}

	private void _calculate() {
		if (_ruleResults != null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		Set<String> ignoredRuleKeys = new HashSet<>();

		try {
			ignoredRuleKeys.addAll(
				ProductionReadinessIgnoreLocalServiceUtil.getIgnoredRuleKeys(
					companyId));
		}
		catch (Exception e) {
		}

		List<ProductionReadinessRule> rules = _getRules();

		_ruleResults = new ArrayList<>();
		_passedCount = 0;
		_failedCount = 0;
		_ignoredCount = 0;

		for (ProductionReadinessRule rule : rules) {
			Collection<Result> results = rule.check(companyId);

			boolean ignored = ignoredRuleKeys.contains(rule.getKey());

			if (ignored) {
				_ignoredCount++;
			}

			for (Result result : results) {
				if (!ignored) {
					if (result.getStatus() == Result.Status.PASS) {
						_passedCount++;
					}
					else {
						_failedCount++;
					}
				}

				_ruleResults.add(new RuleResult(rule, result, ignored));
			}
		}
	}

	private List<ProductionReadinessRule> _getRules() {
		BundleContext bundleContext = FrameworkUtil.getBundle(
			getClass()).getBundleContext();

		List<ProductionReadinessRule> rules = new ArrayList<>();

		try {
			Collection<ServiceReference<ProductionReadinessRule>> references =
				bundleContext.getServiceReferences(
					ProductionReadinessRule.class, null);

			for (ServiceReference<ProductionReadinessRule> reference :
					references) {

				rules.add(bundleContext.getService(reference));
			}
		}
		catch (Exception e) {
		}

		return rules;
	}

	public static class RuleResult {

		public RuleResult(
			ProductionReadinessRule rule, Result result, boolean ignored) {

			this.rule = rule;
			this.result = result;
			this.ignored = ignored;
		}

		public final boolean ignored;
		public final Result result;
		public final ProductionReadinessRule rule;

	}

	private int _failedCount;
	private int _ignoredCount;
	private int _passedCount;
	private final RenderRequest _renderRequest;
	private List<RuleResult> _ruleResults;

}
