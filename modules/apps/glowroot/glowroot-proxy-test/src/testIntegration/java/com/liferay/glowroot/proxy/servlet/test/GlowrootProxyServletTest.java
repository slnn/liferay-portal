/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.proxy.servlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.http.util.HttpResponse;
import com.liferay.portal.test.http.util.HttpUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
	public void testVisitGlowrootPageWithCustomizeContext() throws Exception {
		_deployBundle(
			System.getProperty("liferay.home"), "myportal", "9080", "9005");
		_loginWithAdminUser();
		_visitGlowrootPage(_CUSTOMIZE_CONTEXT_GLOWROOT_PAGE_PATH);
	}

	@Test
	public void testVisitGlowrootPageWithRootContext() throws Exception {
		_deployBundle(System.getProperty("liferay.home"), "root", "7080", "7005");
		_loginWithAdminUser();
		_visitGlowrootPage(_ROOT_CONTEXT_GLOWROOT_PAGE_PATH);
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

	private boolean _clean(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						_clean(file);
					}
					else {
						if (!file.delete()) {
							return false;
						}
					}
				}
			}
		}

		return directory.delete();
	}

	private void _deployBundle(
		String liferayHome, String targetBundleNameSuffix,
		String tomcatStartupPort, String tomcatShutdownPort) {

		String parentDir = liferayHome.substring(
			0, liferayHome.lastIndexOf(File.separator));

		String tomcatDirName = liferayHome.substring(
			liferayHome.lastIndexOf(File.separator) + 1);

		File sourceRootFile = new File(liferayHome);

		String targetTomcatName = tomcatDirName + targetBundleNameSuffix;

		File targetRootFile = new File(parentDir, targetTomcatName);

		if (targetRootFile.exists() && !_clean(targetRootFile)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Failed to delete " + targetRootFile.toString());
			}
		}

		targetRootFile.mkdir();

		Path source = sourceRootFile.toPath();
		Path target = targetRootFile.toPath();

		try {
			Files.walk(
				source
			).forEach(
				sourceFile -> {
					Path targetFile = target.resolve(
						source.relativize(sourceFile));

					String pathString = targetFile.toString();

					if (pathString.contains("ROOT") &&
						targetBundleNameSuffix.contains("myportal")) {

						targetFile = Paths.get(
							StringUtil.replace(pathString, "ROOT", "myportal"));
					}

					if (targetFile.endsWith("ROOT.xml") &&
						targetBundleNameSuffix.contains("myportal")) {

						targetFile = Paths.get(
							StringUtil.replace(
								pathString, "ROOT.xml", "myportal.xml"));
					}

					if (targetFile.endsWith("server.xml")) {
						try (BufferedReader reader = Files.newBufferedReader(
								sourceFile);
							BufferedWriter writer = Files.newBufferedWriter(
								targetFile)) {

							String line;

							while ((line = reader.readLine()) != null) {
								if (line.contains(
										"<Connector maxThreads=\"75\" " +
											"port=\"8080\"")) {

									line = StringUtil.replace(
										line,
										"<Connector maxThreads=\"75\" " +
											"port=\"8080\"",
										StringBundler.concat(
											"<Connector maxThreads=\"75\" ",
											"port=\"", tomcatStartupPort,
											"\""));

									_tomcatStartupPort = Integer.valueOf(
										tomcatStartupPort);
								}

								if (line.contains(
										"<Server port=\"8005\" " +
											"shutdown=\"SHUTDOWN\">")) {

									line = StringUtil.replace(
										line,
										"<Server port=\"8005\" " +
											"shutdown=\"SHUTDOWN\">",
										StringBundler.concat(
											"<Server port=\"",
											tomcatShutdownPort, "\" shutdown=",
											"\"SHUTDOWN\">"));

									_tomcatShutdownPort = Integer.valueOf(
										tomcatShutdownPort);
								}

								writer.write(line);
								writer.newLine();
							}
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
					else if (targetFile.endsWith("catalina.properties") &&
							 targetBundleNameSuffix.contains("myportal")) {

						try (BufferedReader reader = Files.newBufferedReader(
								sourceFile);
							BufferedWriter writer = Files.newBufferedWriter(
								targetFile)) {

							String line;

							while ((line = reader.readLine()) != null) {
								if (line.contains("ROOT")) {
									line = StringUtil.replace(
										line, "ROOT", "myportal");
								}

								writer.write(line);
								writer.newLine();
							}
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
					else {
						try {
							Files.copy(
								sourceFile, targetFile,
								StandardCopyOption.REPLACE_EXISTING);
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(ioException.getCause());
							}
						}
					}
				}
			);

			File portalExtPropertiesFile = new File(
				targetRootFile, "portal-ext.properties");

			if (portalExtPropertiesFile.exists()) {
				portalExtPropertiesFile.delete();
			}

			try (BufferedWriter writer = Files.newBufferedWriter(
					portalExtPropertiesFile.toPath())) {

				writer.write("browser.launcher.url=");
				writer.newLine();
				writer.write("setup.wizard.enabled=false");
				writer.newLine();
				writer.write("terms.of.use.required=false");
				writer.newLine();
				writer.write("users.reminder.queries.enabled=false");
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException.getCause());
				}
			}

			File osgiConfigsFile = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"configs"));

			if (!osgiConfigsFile.exists()) {
				osgiConfigsFile.mkdir();
			}

			File esConfigFile = new File(
				osgiConfigsFile,
				"com.liferay.portal.search.elasticsearch7.configuration." +
					"ElasticsearchConfiguration.config");

			if (esConfigFile.exists()) {
				esConfigFile.delete();
			}

			try (BufferedWriter writer = Files.newBufferedWriter(
					esConfigFile.toPath())) {

				writer.write("sidecarHttpPort=\"AUTO\"");
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException.getCause());
				}
			}

			File osgiStateFolder = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"state"));

			if (osgiStateFolder.exists() && !_clean(osgiStateFolder)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Failed to delete " + osgiStateFolder.toString());
				}
			}

			_deleteTestJars(
				"com.liferay.arquillian.extension.junit.bridge.connector.jar",
				"com.liferay.data.guard.connector.jar");
		}
		catch (IOException ioException) {
			if (_log.isWarnEnabled()) {
				_log.warn(ioException.getCause());
			}
		}
	}

	private URL _createURL(String... strings) throws Exception {
		return new URL(
			"http", "localhost", _tomcatStartupPort,
			StringBundler.concat(strings));
	}

	private void _deleteTestJars(String... jarNames) {
		for (String jarName : jarNames) {
			File jarFile = new File(
				StringBundler.concat(
					targetRootFile, File.separator, "osgi", File.separator,
					"modules", File.separator, jarName));

			if (jarFile.exists()) {
				jarFile.delete();
			}
		}
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

	private static final Log _log = LogFactoryUtil.getLog(
		GlowrootProxyServletTest.class);

	private static int _tomcatShutdownPort;
	private static int _tomcatStartupPort;

}