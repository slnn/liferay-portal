/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rules.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class SecurityEnabledRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		File elasticsearchConfigurationFile = _getFile(
			"com.liferay.portal.search.elasticsearch8.configuration." +
				"ElasticsearchConfiguration.config");

		if (!elasticsearchConfigurationFile.exists()) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {
						elasticsearchConfigurationFile.getName() + " is missing"
					},
					null));
		}

		String elasticsearchConfigurationContent = _read(
			elasticsearchConfigurationFile);

		if (elasticsearchConfigurationContent.contains(
				"authenticationEnabled=B\"false\"") ||
			elasticsearchConfigurationContent.contains(
				"httpSSLEnabled=B\"false\"")) {

			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {
						"Authentication or SSL is disabled in " +
							elasticsearchConfigurationFile.getName()
					},
					null));
		}

		File elasticsearchConnectionConfigurationFile = _getFile(
			"com.liferay.portal.search.elasticsearch8.configuration." +
				"ElasticsearchConnectionConfiguration.config");

		if (!elasticsearchConnectionConfigurationFile.exists()) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {
						elasticsearchConnectionConfigurationFile.getName() +
							" is missing"
					},
					null));
		}

		String elasticsearchConnectionConfigurationContent = _read(
			elasticsearchConnectionConfigurationFile);

		if (elasticsearchConnectionConfigurationContent.contains(
				"authenticationEnabled=B\"false\"")) {

			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {
						"Authentication is disabled in " +
							elasticsearchConnectionConfigurationFile.getName()
					},
					null));
		}

		String connectionId = _extractConnectionId(
			elasticsearchConfigurationContent);

		File connectionIdFile = _getFile(
			StringBundler.concat(
				"com.liferay.portal.search.elasticsearch8.configuration.",
				"ElasticsearchConnectionConfiguration-", connectionId,
				".config"));

		if (!connectionIdFile.exists()) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {connectionIdFile.getName() + " is missing"},
					null));
		}

		String connectionIdContent = _read(connectionIdFile);

		if (connectionIdContent.contains("httpSSLEnabled=B\"false\"")) {
			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.HIGH, getCategory(),
					null, null, getKey(),
					new Object[] {
						"SSL is disabled in " + connectionIdFile.getName()
					},
					null));
		}

		return Collections.singletonList(
			new Result(
				Result.Status.PASS, Result.Severity.HIGH, getCategory(), null,
				null, getKey(), new Object[] {"Security Enabled"}, null));
	}

	@Override
	public String getCategory() {
		return "search-engine-connectivity-validation";
	}

	@Override
	public String getKey() {
		return "security-enabled";
	}

	private String _extractConnectionId(String content) {
		Matcher matcher = _connectionIdPattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return "__REMOTE__";
	}

	private File _getFile(String fileName) {
		return new File(PropsValues.LIFERAY_HOME, "osgi/configs/" + fileName);
	}

	private String _read(File file) {
		try {
			return FileUtil.read(file);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return "";
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecurityEnabledRuleImpl.class);

	private static final Pattern _connectionIdPattern = Pattern.compile(
		"remoteClusterConnectionId=\"(.*)\"");

}