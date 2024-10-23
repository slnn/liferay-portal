/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;

import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;


import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.service.component.annotations.Reference;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Lily Chi
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class DeleteObjectEntryPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		Assume.assumeTrue(Validator.isNull(System.getenv("JENKINS_HOME")));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectFolderJSON =  StringUtil.read(
				DeleteObjectEntryPerformanceTest.class.getClassLoader(), "/object-folder-definition.json");

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
				"com.liferay.oauth2.provider.rest.internal.spi.bearer.token." +
						"provider.configuration." +
						"DefaultBearerTokenProviderConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
						"access.token.expires.in", Integer.MAX_VALUE
				).build());

	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_pid);
	}

	@Before
	public void setUp() throws Exception {
		User user = TestPropsValues.getUser();

		_oAuth2Application =
				_oAuth2ApplicationLocalService.addOAuth2Application(
						user.getCompanyId(), user.getUserId(), user.getFullName(),
						Arrays.asList(
								GrantType.CLIENT_CREDENTIALS, GrantType.REFRESH_TOKEN,
								GrantType.JWT_BEARER, GrantType.RESOURCE_OWNER_PASSWORD,
								GrantType.AUTHORIZATION_CODE),
						"client_secret_post", user.getUserId(),
						RandomTestUtil.randomString(), 0, RandomTestUtil.randomString(),
						"", Collections.emptyList(), "", 0, "", "rest_token", "",
						Arrays.asList("http://localhost:8080"), false,
						Arrays.asList(
								"Liferay.Headless.Admin.User.everything",
								"Liferay.Headless.Admin.User.everything.read",
								"Liferay.Headless.Admin.User.everything.write"),
						false, new ServiceContext());

		_jsonObject = JSONFactoryUtil.createJSONObject(
				_localOAuthClient.requestTokens(
						_oAuth2Application, user.getUserId()));
	}


	@Test
	public void testDeleteObjectEntry() throws Exception {
		_importObjectFolder();

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(_json, "application/json");
		httpInvoker.header(
				"Authorization",
				"Bearer " + _jsonObject.getString("access_token"));
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path(
				"http://localhost:8080/o/headless-admin-user/v1.0" +
						"/user-accounts");

		httpInvoker.invoke();
	}

	private void _importObjectFolder() throws Exception {
		ObjectFolderResource.Builder builder =
				_objectFolderResourceFactory.create();

		ObjectFolderResource objectFolderResource = builder.user(
				TestPropsValues.getUser()
		).build();

		JSONObject objectFolderJSONObject = _jsonFactory.createJSONObject(
				_objectFolderJSON);

		ObjectFolder objectFolder = ObjectFolder.toDTO(
				objectFolderJSONObject.toString());

		objectFolder.setName("SampleObjectFolder");

		objectFolderResource.putObjectFolderByExternalReferenceCode(
				objectFolder.getExternalReferenceCode(), objectFolder);

	}

	private static final Log _log = LogFactoryUtil.getLog(
			DeleteObjectEntryPerformanceTest.class);

	private static String _objectFolderJSON;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

	private static String _pid;

	@Inject
	private CompanyLocalService _companyLocalService;

	private JSONObject _jsonObject;

	@Inject
	private LocalOAuthClient _localOAuthClient;

	@DeleteAfterTestRun
	private OAuth2Application _oAuth2Application;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}
