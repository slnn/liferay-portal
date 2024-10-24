/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFolder;
import com.liferay.object.admin.rest.resource.v1_0.ObjectFolderResource;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
		_objectFolderJSON = StringUtil.read(
			DeleteObjectEntryPerformanceTest.class.getClassLoader(),
			"/object-folder-definition.json");
	}

	@Before
	public void setUp() throws Exception {
		_importObjectFolder();
		_addObjectEntry(100);
	}

	@Test
	public void testDeleteObjectEntry() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_deleteObjectEntry();
		}
	}

	private void _addObjectEntry(int objectEntryCount) {
		try {
			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(
				_createObjectEntryJSON(objectEntryCount), "application/json");
			httpInvoker.userNameAndPassword(_USER_NAME_AND_PASSWORD);
			httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
			httpInvoker.path(_PATH);

			httpInvoker.invoke();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _createObjectEntryJSON(int objectEntryCount)
		throws Exception {

		if (objectEntryCount < 1) {
			throw new IllegalArgumentException(
				"Must at least create 1 Object Entry!");
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < objectEntryCount; i++) {
			jsonArray.put(JSONUtil.put("alpha", "foo"));
		}

		return jsonArray.toString();
	}

	private void _deleteObjectEntry() {
		try {
			JSONArray jsonArray = _getObjectEntryIdJSONArray();

			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.body(jsonArray.toString(), "application/json");
			httpInvoker.userNameAndPassword(_USER_NAME_AND_PASSWORD);
			httpInvoker.httpMethod(HttpInvoker.HttpMethod.DELETE);
			httpInvoker.path(_PATH);

			httpInvoker.invoke();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private JSONArray _getObjectEntryIdJSONArray() {
		JSONArray jsonArray = null;

		try {
			HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

			httpInvoker.userNameAndPassword(_USER_NAME_AND_PASSWORD);
			httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
			httpInvoker.path("http://localhost:8080/o/c/foos/?fields=id");

			HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				httpResponse.getContent());

			jsonArray = (JSONArray)jsonObject.get("items");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return jsonArray;
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

	private static final String _PATH = "http://localhost:8080/o/c/foos/batch";

	private static final String _USER_NAME_AND_PASSWORD =
		"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD;

	private static String _objectFolderJSON;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectFolderResource.Factory _objectFolderResourceFactory;

}