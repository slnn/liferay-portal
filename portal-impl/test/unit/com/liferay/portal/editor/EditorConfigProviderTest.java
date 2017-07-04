/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.editor;

import com.liferay.portal.editor.configuration.EditorConfigProvider;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.registry.BasicRegistryImpl;
import com.liferay.registry.RegistryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public class EditorConfigProviderTest {

	@Before
	public void setUp() {
		RegistryUtil.setRegistry(new BasicRegistryImpl());

		ReflectionTestUtil.setFieldValue(
			JSONFactoryUtil.class, "_jsonFactory",
			ProxyUtil.newProxyInstance(
				JSONFactory.class.getClassLoader(),
				new Class<?>[] {JSONFactory.class},
				(proxy, method, args) -> new JSONObjectImpl()));

		_testEditorConfigProvider = new TestEditorConfigProvider();
	}

	@Test
	public void testPopulateMiddle() {
		TestSupplier testSupplier1 = new TestSupplier(_key1, _value1);

		TestEditorConfigContributorWithPopulate
			testEditorConfigContributorWithPopulate =
				new TestEditorConfigContributorWithPopulate(_key1, _value2);

		TestSupplier testSupplier2 = new TestSupplier(_key1, _value3);

		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier1, null));
		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier1, null));
		addContributor(testEditorConfigContributorWithPopulate);
		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier2, null));
		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier2, null));

		JSONObject jsonObject = _testEditorConfigProvider.getConfigJSONObject(
			null, null, null, null, null, null);

		Assert.assertEquals(1, jsonObject.length());

		Assert.assertEquals(_value3, jsonObject.getString(_key1));

		Assert.assertEquals(1, testSupplier1._callCount);

		Assert.assertEquals(
			1, testEditorConfigContributorWithPopulate._callCount);
		Assert.assertSame(
			_value1, testEditorConfigContributorWithPopulate._oldValue);

		Assert.assertEquals(1, testSupplier2._callCount);
	}

	@Test
	public void testSetSet() {
		TestSupplier testSupplier1 = new TestSupplier(_key1, _value1);
		TestSupplier testSupplier2 = new TestSupplier(_key1, _value2);

		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier1, null));
		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier2, null));

		JSONObject jsonObject = _testEditorConfigProvider.getConfigJSONObject(
			null, null, null, null, null, null);

		Assert.assertEquals(1, jsonObject.length());

		Assert.assertEquals(_value2, jsonObject.getString(_key1));

		Assert.assertEquals(0, testSupplier1._callCount);

		Assert.assertEquals(1, testSupplier2._callCount);
	}

	@Test
	public void testSetUpdate() {
		TestSupplier testSupplier1 = new TestSupplier(_key1, _value1);
		TestFunction testFunction1 = new TestFunction(_key1, _value2);

		TestSupplier testSupplier2 = new TestSupplier(_key2, _value3);
		TestFunction testFunction2 = new TestFunction(_key1, _value3);

		addContributor(
			new TestEditorConfigContributorWithCollect(
				testSupplier1, testFunction1));
		addContributor(
			new TestEditorConfigContributorWithCollect(
				testSupplier2, testFunction2));

		JSONObject jsonObject = _testEditorConfigProvider.getConfigJSONObject(
			null, null, null, null, null, null);

		Assert.assertEquals(2, jsonObject.length());

		Assert.assertEquals(_value3, jsonObject.getString(_key1));
		Assert.assertEquals(_value3, jsonObject.getString(_key2));

		Assert.assertEquals(1, testSupplier1._callCount);

		Assert.assertEquals(1, testFunction1._callCount);
		Assert.assertSame(_value1, testFunction1._oldValue);

		Assert.assertEquals(1, testSupplier2._callCount);

		Assert.assertEquals(1, testFunction2._callCount);
		Assert.assertSame(_value2, testFunction2._oldValue);
	}

	@Test
	public void testUpdateSet() {
		TestSupplier testSupplier1 = new TestSupplier(_key1, _value1);
		TestFunction testFunction1 = new TestFunction(_key2, _value1);

		TestSupplier testSupplier2 = new TestSupplier(_key2, _value2);

		addContributor(
			new TestEditorConfigContributorWithCollect(
				testSupplier1, testFunction1));
		addContributor(
			new TestEditorConfigContributorWithCollect(testSupplier2, null));

		JSONObject jsonObject = _testEditorConfigProvider.getConfigJSONObject(
			null, null, null, null, null, null);

		Assert.assertEquals(2, jsonObject.length());

		Assert.assertEquals(_value1, jsonObject.getString(_key1));
		Assert.assertEquals(_value2, jsonObject.getString(_key2));

		Assert.assertEquals(1, testSupplier1._callCount);

		Assert.assertEquals(0, testFunction1._callCount);
		Assert.assertNull(testFunction1._oldValue);

		Assert.assertEquals(1, testSupplier2._callCount);
	}

	@Test
	public void testUpdateUpdate() {
		TestFunction testFunction1 = new TestFunction(_key1, _value1);
		TestFunction testFunction2 = new TestFunction(_key1, _value2);

		addContributor(
			new TestEditorConfigContributorWithCollect(null, testFunction1));
		addContributor(
			new TestEditorConfigContributorWithCollect(null, testFunction2));
		addContributor(
			new TestEditorConfigContributorWithCollect(null, testFunction2));

		JSONObject jsonObject = _testEditorConfigProvider.getConfigJSONObject(
			null, null, null, null, null, null);

		Assert.assertEquals(1, jsonObject.length());

		Assert.assertEquals(_value2, jsonObject.getString(_key1));

		Assert.assertEquals(1, testFunction1._callCount);
		Assert.assertNull(testFunction1._oldValue);

		Assert.assertEquals(2, testFunction2._callCount);
		Assert.assertSame(_value2, testFunction2._oldValue);
	}

	protected void addContributor(
		EditorConfigContributor editorConfigContributor) {

		_testEditorConfigProvider._editorConfigContributors.add(
			editorConfigContributor);
	}

	private static final String _key1 = "Key 1";
	private static final String _key2 = "Key 2";
	private static final String _value1 = "Value 1";
	private static final String _value2 = "Value 2";
	private static final String _value3 = "Value 3";

	private TestEditorConfigProvider _testEditorConfigProvider;

	private static class TestEditorConfigContributorWithCollect
		implements EditorConfigContributor {

		@Override
		public void collectEditorConfigElementContributors(
			EditorConfigElementContributorCollector collector,
			Map<String, Object> inputEditorTaglibAttributes,
			ThemeDisplay themeDisplay,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

			if (_testSupplier != null) {
				collector.collect(_testSupplier._key, _testSupplier);
			}

			if (_testFunction != null) {
				collector.collect(_testFunction._key, _testFunction);
			}
		}

		private TestEditorConfigContributorWithCollect(
			TestSupplier testSupplier, TestFunction testFunction) {

			_testSupplier = testSupplier;
			_testFunction = testFunction;
		}

		private final TestFunction _testFunction;
		private final TestSupplier _testSupplier;

	}

	private static class TestEditorConfigContributorWithPopulate
		implements EditorConfigContributor {

		@Override
		public void populateConfigJSONObject(
			JSONObject jsonObject,
			Map<String, Object> inputEditorTaglibAttributes,
			ThemeDisplay themeDisplay,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

			_callCount++;

			_oldValue = jsonObject.getString(_key);

			jsonObject.put(_key, _value);
		}

		private TestEditorConfigContributorWithPopulate(
			String key, String value) {

			_key = key;
			_value = value;
		}

		private int _callCount;
		private final String _key;
		private String _oldValue;
		private final String _value;

	}

	private static class TestEditorConfigProvider extends EditorConfigProvider {

		@Override
		protected void filterEditorContributors(
			Consumer<EditorConfigContributor> consumer, String portletName,
			String editorConfigKey, String editorName) {

			for (EditorConfigContributor editorConfigContributor :
					_editorConfigContributors) {

				consumer.accept(editorConfigContributor);
			}
		}

		private final List<EditorConfigContributor> _editorConfigContributors =
			new ArrayList<>();

	}

	private static class TestFunction implements Function<Object, Object> {

		@Override
		public Object apply(Object object) {
			_callCount++;

			_oldValue = object;

			return _value;
		}

		private TestFunction(String key, String value) {
			_key = key;
			_value = value;
		}

		private int _callCount;
		private final String _key;
		private Object _oldValue;
		private final String _value;

	}

	private static class TestSupplier implements Supplier<Object> {

		@Override
		public Object get() {
			_callCount++;

			return _value;
		}

		private TestSupplier(String key, String value) {
			_key = key;
			_value = value;
		}

		private int _callCount;
		private final String _key;
		private final String _value;

	}

}