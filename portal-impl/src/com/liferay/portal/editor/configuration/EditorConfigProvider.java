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

package com.liferay.portal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Sergio González
 * @author Preston Crary
 */
public class EditorConfigProvider
	extends BaseEditorProvider<EditorConfigContributor> {

	public EditorConfigProvider() {
		super(EditorConfigContributor.class);
	}

	public JSONObject getConfigJSONObject(
		String portletName, String editorConfigKey, String editorName,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		ContributorsCollector contributorsCollector =
			new ContributorsCollector();

		filterEditorContributors(
			editorConfigContributor -> {
				editorConfigContributor.collectEditorConfigElementContributors(
					contributorsCollector, inputEditorTaglibAttributes,
					themeDisplay, requestBackedPortletURLFactory);

				contributorsCollector.endCollecting(editorConfigContributor);
			},
			portletName, editorConfigKey, editorName);

		JSONObject configJSONObject = JSONFactoryUtil.createJSONObject();

		contributorsCollector.populateConfigJSONObject(
			configJSONObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		return configJSONObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditorConfigProvider.class);

	private static final Method _interfaceMethod;

	static {
		try {
			_interfaceMethod = EditorConfigContributor.class.getMethod(
				"collectEditorConfigElementContributors",
				EditorConfigElementContributorCollector.class, Map.class,
				ThemeDisplay.class, RequestBackedPortletURLFactory.class);
		}
		catch (ReflectiveOperationException roe) {
			throw new ExceptionInInitializerError(roe);
		}
	}

	private static class ContributorsCollector
		implements EditorConfigContributor,
				   EditorConfigElementContributorCollector {

		@Override
		public <T> void collect(String key, Function<T, T> elementFunction) {
			EditorConfigContributorFunctionAdaptor<T> adaptor =
				new EditorConfigContributorFunctionAdaptor<>(
					key, elementFunction);

			_contributorsIndex.compute(
				key,
				(k, adaptors) -> {
					if (adaptors == null) {
						adaptors = new ArrayList<>(1);
					}

					adaptors.add(adaptor);

					return adaptors;
				});

			_contributors.add(adaptor);
		}

		@Override
		public <T> void collect(String key, Supplier<T> elementSupplier) {
			EditorConfigContributorSupplierAdaptor<T> adaptor =
				new EditorConfigContributorSupplierAdaptor<>(
					key, elementSupplier);

			_contributorsIndex.compute(
				key,
				(k, adaptors) -> {
					if (adaptors == null) {
						adaptors = new ArrayList<>(1);
					}
					else {
						_contributors.removeAll(adaptors);

						adaptors.clear();
					}

					adaptors.add(adaptor);

					return adaptors;
				});

			_contributors.add(adaptor);
		}

		public void endCollecting(
			EditorConfigContributor editorConfigContributor) {

			if (_collected) {
				_collected = false;
			}
			else {
				Class<? extends EditorConfigContributor> clazz =
					editorConfigContributor.getClass();

				Method method = null;

				try {
					method = clazz.getMethod(
						"collectEditorConfigElementContributors",
						EditorConfigElementContributorCollector.class,
						Map.class, ThemeDisplay.class,
						RequestBackedPortletURLFactory.class);
				}
				catch (ReflectiveOperationException roe) {
					_log.error(roe, roe);
				}

				if (_interfaceMethod.equals(method)) {
					_contributorsIndex.clear();

					_contributors.add(editorConfigContributor);
				}
			}
		}

		@Override
		public void populateConfigJSONObject(
			JSONObject jsonObject,
			Map<String, Object> inputEditorTaglibAttributes,
			ThemeDisplay themeDisplay,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

			for (EditorConfigContributor contributor : _contributors) {
				contributor.populateConfigJSONObject(
					jsonObject, inputEditorTaglibAttributes, themeDisplay,
					requestBackedPortletURLFactory);
			}

			_contributors.clear();

			for (List<EditorConfigContributor> list :
					_contributorsIndex.values()) {

				list.clear();
			}
		}

		private boolean _collected;
		private final List<EditorConfigContributor> _contributors =
			new ArrayList<>();
		private final Map<String, List<EditorConfigContributor>>
			_contributorsIndex = new HashMap<>();

	}

	private static class EditorConfigContributorFunctionAdaptor<T>
		implements EditorConfigContributor {

		@Override
		public void collectEditorConfigElementContributors(
			EditorConfigElementContributorCollector collector,
			Map<String, Object> inputEditorTaglibAttributes,
			ThemeDisplay themeDisplay,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

			collector.collect(_key, _function);
		}

		private EditorConfigContributorFunctionAdaptor(
			String key, Function<T, T> function) {

			_key = key;
			_function = function;
		}

		private final Function<T, T> _function;
		private final String _key;

	}

	private static class EditorConfigContributorSupplierAdaptor<T>
		implements EditorConfigContributor {

		@Override
		public void collectEditorConfigElementContributors(
			EditorConfigElementContributorCollector collector,
			Map<String, Object> inputEditorTaglibAttributes,
			ThemeDisplay themeDisplay,
			RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

			collector.collect(_key, _supplier);
		}

		private EditorConfigContributorSupplierAdaptor(
			String key, Supplier<T> supplier) {

			_key = key;
			_supplier = supplier;
		}

		private final String _key;
		private final Supplier<T> _supplier;

	}

}