/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.proxy.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.http.invoker.HttpInvoker;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lily Chi
 */
@RunWith(Arquillian.class)
public class GlowrootProxyServletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testVisitGlowrootPageWithNonrootContext() throws Exception {
		_loginWithAdminUser()
		_visitGlowrootPage(_NONROOT_CONTEXT_GLOWROOT_PAGE_PATH);
	}

	@Test
	public void testVisitGlowrootPageWithRootContext() throws Exception {
		_loginWithAdminUser();
		_visitGlowrootPage(_ROOT_CONTEXT_GLOWROOT_PAGE_PATH);
	}

	private void _loginWithAdminUser() throws Exception {
	}

	private void _visitGlowrootPage(String path) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(path);

		HttpInvoker.HttpResponse response = httpInvoker.invoke();

		String content = response.getContent();

		Assert.assertTrue(
			"Failed to visit " + path, content.contains("Glowroot UI"));
	}

	private static final String _NONROOT_CONTEXT_GLOWROOT_PAGE_PATH =
		"http://localhost:8080/myportal/o/glowroot";

	private static final String _ROOT_CONTEXT_GLOWROOT_PAGE_PATH =
		"http://localhost:8080/o/glowroot";

}