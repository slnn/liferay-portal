/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.proxy.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.http.util.HttpResponse;
import com.liferay.portal.test.http.util.HttpUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.net.URL;

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
	public void testVisitGlowrootPageWithRootContext() throws Exception {
		_loginWithAdminUser();
		_visitGlowrootPage(_ROOT_CONTEXT_GLOWROOT_PAGE_PATH);
		_visitGlowrootPage(_CUSTOMIZE_CONTEXT_GLOWROOT_PAGE_PATH);
	}

	private void _assertContent(HttpResponse httpResponse, String key) {
		Assert.assertEquals(200, httpResponse.getStatusCode());

		String httpResponseString = httpResponse.toString();

		Assert.assertTrue(httpResponseString.contains(key));
	}

	private URL _assertRedirect(HttpResponse httpResponse, String redirect)
		throws Exception {

		return _assertRedirect(httpResponse, _createURL(redirect));
	}

	private URL _assertRedirect(HttpResponse httpResponse, URL url)
		throws Exception {

		Assert.assertEquals(url.toString(), httpResponse.getRedirect());
		Assert.assertEquals(302, httpResponse.getStatusCode());

		return url;
	}

	private URL _createURL(String... strings) throws Exception {
		return new URL(
			"http", "localhost", 8080, StringBundler.concat(strings));
	}

	private void _loginWithAdminUser() throws Exception {
		HttpResponse httpResponse1 = HttpUtil.doGet(
			null, _createURL(StringPool.FORWARD_SLASH));

		_assertContent(httpResponse1, "Liferay.currentURL");

		String csrfToken = httpResponse1.getCSRFToken();

		HttpResponse httpResponse2 = HttpUtil.doPost(
			null, csrfToken,
			new String[][] {
				{_P_P_ID_NAMESPACE + "_checkboxNames", "rememberMe"},
				{_P_P_ID_NAMESPACE + "_doActionAfterLogin", StringPool.FALSE},
				{
					_P_P_ID_NAMESPACE + "_formDate",
					String.valueOf(System.currentTimeMillis())
				},
				{_P_P_ID_NAMESPACE + "_login", "test@liferay.com"},
				{_P_P_ID_NAMESPACE + "_password", "test"},
				{_P_P_ID_NAMESPACE + "_redirect", StringPool.BLANK},
				{_P_P_ID_NAMESPACE + "_saveLastPath", StringPool.FALSE}
			},
			_createURL(
				"/home?", _P_P_ID_NAMESPACE,
				"_javax.portlet.action=/login/login&", _P_P_ID_NAMESPACE,
				"_mvcRenderCommandName=/login/login&p_p_id=", _P_P_ID,
				"&p_p_lifecycle=1&p_p_mode=view&p_p_state=normal"));

		_assertRedirect(httpResponse2, "/c");

		HttpResponse httpResponse3 = HttpUtil.doGet(
			csrfToken, _createURL("/c"));

		_assertRedirect(httpResponse3, StringPool.SLASH);

		HttpResponse httpResponse4 = HttpUtil.doGet(
			csrfToken, _createURL(StringPool.SLASH));

		_assertContent(
			httpResponse4, "ProductNavigationUserPersonalBarPortlet");
	}

	private void _visitGlowrootPage(String path) throws Exception {
		String content = String.valueOf(HttpUtil.doGet(null, _createURL(path)));

		Assert.assertTrue(
			"Failed to visit " + path, content.contains("Glowroot UI"));
	}

	private static final String _CUSTOMIZE_CONEXT_NAME = "myportal";

	private static final String _CUSTOMIZE_CONTEXT_GLOWROOT_PAGE_PATH =
		"/" + _CUSTOMIZE_CONEXT_NAME + "/o/glowroot";

	private static final String _P_P_ID =
		"com_liferay_login_web_portlet_LoginPortlet";

	private static final String _P_P_ID_NAMESPACE =
		StringPool.UNDERLINE + _P_P_ID;

	private static final String _ROOT_CONTEXT_GLOWROOT_PAGE_PATH =
		"/o/glowroot";

}