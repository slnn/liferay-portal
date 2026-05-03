/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.rules.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.production.readiness.ProductionReadinessRule;
import com.liferay.production.readiness.Result;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(service = ProductionReadinessRule.class)
public class JSPEngineSettingsRuleImpl implements ProductionReadinessRule {

	@Override
	public Collection<Result> check(long companyId) {
		String catalinaBase = System.getProperty("catalina.base");

		if (Validator.isNull(catalinaBase)) {
			catalinaBase = System.getProperty("catalina.home");
		}

		if (Validator.isNull(catalinaBase)) {
			return Collections.emptyList();
		}

		File webXmlFile = new File(catalinaBase, "conf/web.xml");

		if (!webXmlFile.exists()) {
			return Collections.emptyList();
		}

		try {
			String content = FileUtil.read(webXmlFile);

			Document document = SAXReaderUtil.read(content);

			Element rootElement = document.getRootElement();

			boolean development = true;
			boolean mappedFile = true;

			List<Element> allElements = rootElement.elements();

			for (Element element : allElements) {
				String elementName = element.getName();

				if (!elementName.equals("servlet")) {
					continue;
				}

				String servletName = element.elementText("servlet-name");

				if (!servletName.equals("jsp")) {
					continue;
				}

				List<Element> initParams = element.elements("init-param");

				for (Element param : initParams) {
					String paramName = param.elementText("param-name");
					String paramValue = param.elementText("param-value");

					if (paramName.equals("development")) {
						development = GetterUtil.getBoolean(paramValue, true);
					}
					else if (paramName.equals("mappedfile")) {
						mappedFile = GetterUtil.getBoolean(paramValue, true);
					}
				}
			}

			if (!development && !mappedFile) {
				return Collections.singletonList(
					new Result(
						Result.Status.PASS, Result.Severity.LOW, getCategory(),
						StringBundler.concat(
							"development=", development, ", mappedfile=",
							mappedFile),
						"development=false, mappedfile=false", getKey(),
						new Object[] {
							"Development Mode and Mapped File have been " +
								"disabled"
						},
						null));
			}

			return Collections.singletonList(
				new Result(
					Result.Status.FAIL, Result.Severity.LOW, getCategory(),
					StringBundler.concat(
						"development=", development, ", mappedfile=",
						mappedFile),
					"development=false, mappedfile=false", getKey(),
					new Object[] {
						StringBundler.concat(
							"Disable Development Mode will stop the server ",
							"from polling for JSP file changes, that disable ",
							"mappedFile will reduces the amount of generated ",
							"servlet code")
					},
					null));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return Collections.emptyList();
		}
	}

	@Override
	public String getCategory() {
		return "jvm-&-infrastructure-validation";
	}

	@Override
	public String getKey() {
		return "jsp-engine-settings";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSPEngineSettingsRuleImpl.class);

}