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

package com.liferay.portal.tools.sample.sql.builder;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.headless.delivery.dto.v1_0.FragmentLink;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderFragmentLayoutStructureItemImporter {

	public void updateFragmentEntryLinkModels(
			List<FragmentEntryLinkModel> fragmentEntryLinkModels,
			PageElement pageElement, ObjectMapper objectMapper,
			List<SampleSQLBuilderFragmentEntryProcessor>
				fragmentEntryProcessorList)
		throws Exception {

		Map<String, Object> definitionMap = _getDefinitionMap(
			pageElement.getDefinition(), objectMapper);

		JSONObject defaultEditableValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		String html = StringPool.BLANK;
		String configuration = StringPool.BLANK;

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			html = fragmentEntryLinkModel.getHtml();
			configuration = fragmentEntryLinkModel.getConfiguration();

			for (SampleSQLBuilderFragmentEntryProcessor fragmentEntryProcessor :
					fragmentEntryProcessorList) {

				JSONObject jsonObject =
					fragmentEntryProcessor.getDefaultEditableValuesJSONObject(
						html, configuration);

				defaultEditableValuesJSONObject.put(
					fragmentEntryProcessor.getClassName(), jsonObject);
			}

			Map<String, String> editableTypes =
				EditableFragmentEntryProcessorUtil.getEditableTypes(html);

			JSONObject fragmentEntryProcessorValuesJSONObject = JSONUtil.put(
				"com.liferay.fragment.entry.processor.background.image." +
					"BackgroundImageFragmentEntryProcessor",
				JSONFactoryUtil.createJSONObject());

			JSONObject editableFragmentEntryProcessorJSONObject =
				_toEditableFragmentEntryProcessorJSONObject(
					editableTypes,
					(List<Object>)definitionMap.get("fragmentFields"));

			fragmentEntryProcessorValuesJSONObject.put(
				"com.liferay.fragment.entry.processor.editable." +
					"EditableFragmentEntryProcessor",
				editableFragmentEntryProcessorJSONObject);

			Map<String, String> configurationTypes = _getConfigurationTypes(
				configuration);

			JSONObject freeMarkerFragmentEntryProcessorJSONObject =
				_toFreeMarkerFragmentEntryProcessorJSONObject(
					configurationTypes,
					(Map<String, Object>)definitionMap.get("fragmentConfig"));

			fragmentEntryProcessorValuesJSONObject.put(
				"com.liferay.fragment.entry.processor.freemarker." +
					"FreeMarkerFragmentEntryProcessor",
				freeMarkerFragmentEntryProcessorJSONObject);

			JSONObject jsonObject = _deepMerge(
				defaultEditableValuesJSONObject,
				fragmentEntryProcessorValuesJSONObject);

			fragmentEntryLinkModel.setEditableValues(jsonObject.toString());
		}
	}

	private JSONObject _createBaseFragmentFieldJSONObject(
		Map<String, Object> map) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (map == null) {
			return jsonObject;
		}

		Map<String, Object> valueMap = (Map<String, Object>)map.get("value");

		if (valueMap != null) {
			String title = String.valueOf(valueMap.get("title"));

			if (title != null) {
				jsonObject.put("defaultValue", title);
			}
		}

		Map<String, Object> defaultFragmentInlineValueMap =
			(Map<String, Object>)map.get("defaultFragmentInlineValue");

		if (defaultFragmentInlineValueMap == null) {
			defaultFragmentInlineValueMap = (Map<String, Object>)map.get(
				"defaultValue");
		}

		if (defaultFragmentInlineValueMap != null) {
			jsonObject.put(
				"defaultValue", defaultFragmentInlineValueMap.get("value"));
		}

		Map<String, Object> valueI18nMap = (Map<String, Object>)map.get(
			"value_i18n");

		if (valueI18nMap != null) {
			for (Map.Entry<String, Object> entry : valueI18nMap.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}

			return jsonObject;
		}

		processMapping(jsonObject, (Map<String, Object>)map.get("mapping"));

		return jsonObject;
	}

	private JSONObject _createFragmentConfigJSONObject(
		Map<String, Object> fragmentImageMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentImageMap == null) {
			return jsonObject;
		}

		Map<String, Object> descriptionMap =
			(Map<String, Object>)fragmentImageMap.get("description");

		if (descriptionMap == null) {
			return jsonObject;
		}

		String value = (String)descriptionMap.get("value");

		if (value != null) {
			jsonObject.put("alt", value);
		}

		Map<String, Object> localizedDescriptionMap =
			(Map<String, Object>)descriptionMap.get("value_i18n");

		if (localizedDescriptionMap == null) {
			return jsonObject;
		}

		JSONObject localizedDescriptionJSONObject =
			JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, Object> entry :
				localizedDescriptionMap.entrySet()) {

			localizedDescriptionJSONObject.put(
				entry.getKey(), entry.getValue());
		}

		jsonObject.put("alt", localizedDescriptionJSONObject);

		return jsonObject;
	}

	private JSONObject _createFragmentLinkConfigJSONObject(
		Map<String, Object> fragmentLinkMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentLinkMap == null) {
			return jsonObject;
		}

		Map<String, Object> valueI18nMap =
			(Map<String, Object>)fragmentLinkMap.get("value_i18n");

		if (valueI18nMap != null) {
			for (Map.Entry<String, Object> entry : valueI18nMap.entrySet()) {
				Map<String, Object> fragmentLinkValueMap =
					(Map<String, Object>)entry.getValue();

				jsonObject.put(
					entry.getKey(),
					_createFragmentLinkValueConfigJSONObject(
						fragmentLinkValueMap));
			}
		}

		Map<String, Object> valueMap = (Map<String, Object>)fragmentLinkMap.get(
			"value");

		if (valueMap != null) {
			jsonObject = JSONUtil.merge(
				jsonObject, _createFragmentLinkValueConfigJSONObject(valueMap));
		}

		jsonObject = JSONUtil.merge(
			jsonObject,
			_createFragmentLinkValueConfigJSONObject(fragmentLinkMap));

		jsonObject.put("mapperType", "link");

		return jsonObject;
	}

	private JSONObject _createFragmentLinkValueConfigJSONObject(
		Map<String, Object> fragmentLinkValueMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentLinkValueMap == null) {
			return jsonObject;
		}

		Map<String, Object> hrefMap =
			(Map<String, Object>)fragmentLinkValueMap.get("href");

		if (hrefMap == null) {
			return jsonObject;
		}

		Map<String, Object> defaultFragmentInlineValueMap =
			(Map<String, Object>)hrefMap.get("defaultFragmentInlineValue");

		if (defaultFragmentInlineValueMap == null) {
			defaultFragmentInlineValueMap = (Map<String, Object>)hrefMap.get(
				"defaultValue");
		}

		String target = (String)fragmentLinkValueMap.get("target");

		if (target != null) {
			if (Objects.equals(target, FragmentLink.Target.PARENT.getValue()) ||
				Objects.equals(target, FragmentLink.Target.TOP.getValue())) {

				target = FragmentLink.Target.SELF.getValue();
			}

			jsonObject.put(
				"target", "_" + StringUtil.lowerCaseFirstLetter(target));
		}

		Object value = hrefMap.get("value");

		if (value != null) {
			jsonObject.put("href", value);

			return jsonObject;
		}

		if (defaultFragmentInlineValueMap != null) {
			value = defaultFragmentInlineValueMap.get("value");
		}

		if (value != null) {
			jsonObject.put("href", value);
		}

		processMapping(jsonObject, (Map<String, Object>)hrefMap.get("mapping"));

		return jsonObject;
	}

	private JSONObject _createImageJSONObject(
		Map<String, Object> classPKReferencesMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (classPKReferencesMap == null) {
			return jsonObject;
		}

		for (Map.Entry<String, Object> entry :
				classPKReferencesMap.entrySet()) {

			Map<String, Object> classPKReferenceMap =
				(Map<String, Object>)entry.getValue();

			if (Objects.equals(
					classPKReferenceMap.get("className"),
					FileEntry.class.getName())) {

				jsonObject.put(
					entry.getKey(),
					JSONUtil.put("url", "/welcome_bg_benchmark.png"));
			}
		}

		return jsonObject;
	}

	private JSONObject _deepMerge(
			JSONObject jsonObject1, JSONObject jsonObject2)
		throws Exception {

		if (jsonObject1 == null) {
			return JSONFactoryUtil.createJSONObject(jsonObject2.toString());
		}

		if (jsonObject2 == null) {
			return JSONFactoryUtil.createJSONObject(jsonObject1.toString());
		}

		JSONObject jsonObject3 = JSONFactoryUtil.createJSONObject(
			jsonObject1.toString());

		Iterator<String> iterator = jsonObject2.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (!jsonObject3.has(key)) {
				jsonObject3.put(key, jsonObject2.get(key));
			}
			else {
				Object value1 = jsonObject1.get(key);
				Object value2 = jsonObject2.get(key);

				if ((value1 instanceof JSONObject) &&
					(value2 instanceof JSONObject)) {

					jsonObject3.put(
						key,
						_deepMerge(
							(JSONObject)value1,
							jsonObject2.getJSONObject(key)));
				}
				else {
					jsonObject3.put(key, value2);
				}
			}
		}

		return jsonObject3;
	}

	private Map<String, String> _getConfigurationTypes(String configuration)
		throws Exception {

		Map<String, String> configurationTypes = new HashMap<>();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(configuration);

		JSONArray fieldSetsJSONArray = jsonObject.getJSONArray("fieldSets");

		if (fieldSetsJSONArray == null) {
			return configurationTypes;
		}

		for (int i = 0; i < fieldSetsJSONArray.length(); i++) {
			JSONObject fieldsJSONObject = fieldSetsJSONArray.getJSONObject(i);

			JSONArray fieldsJSONArray = fieldsJSONObject.getJSONArray("fields");

			for (int j = 0; j < fieldsJSONArray.length(); j++) {
				JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(j);

				configurationTypes.put(
					fieldJSONObject.getString("name"),
					fieldJSONObject.getString("type"));
			}
		}

		return configurationTypes;
	}

	private Map<String, Object> _getDefinitionMap(
			Object definition, ObjectMapper objectMapper)
		throws Exception {

		Map<String, Object> definitionMap = null;

		if (definition instanceof Map) {
			definitionMap = (Map<String, Object>)definition;
		}
		else {
			definitionMap = objectMapper.readValue(
				definition.toString(), Map.class);
		}

		return definitionMap;
	}

	private JSONObject _toEditableFragmentEntryProcessorJSONObject(
			Map<String, String> editableTypes, List<Object> fragmentFields)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (Object fragmentField : fragmentFields) {
			JSONObject fragmentFieldJSONObject =
				JSONFactoryUtil.createJSONObject();

			Map<String, Object> fragmentFieldMap =
				(Map<String, Object>)fragmentField;

			String fragmentFieldId = (String)fragmentFieldMap.get("id");

			Map<String, Object> valueMap =
				(Map<String, Object>)fragmentFieldMap.get("value");

			JSONObject editableFieldConfigJSONObject =
				_createFragmentLinkConfigJSONObject(
					(Map<String, Object>)valueMap.get("fragmentLink"));

			JSONObject baseFragmentFieldJSONObject =
				_createBaseFragmentFieldJSONObject(
					(Map<String, Object>)valueMap.get("text"));

			if (Objects.equals(editableTypes.get(fragmentFieldId), "image")) {
				Map<String, Object> fragmentImageMap =
					(Map<String, Object>)valueMap.get("fragmentImage");

				baseFragmentFieldJSONObject =
					JSONFactoryUtil.createJSONObject();

				if ((fragmentImageMap != null) &&
					fragmentImageMap.containsKey(
						"fragmentImageClassPKReference")) {

					Map<String, Object> fragmentImageClassPKReferenceMap =
						(Map<String, Object>)fragmentImageMap.get(
							"fragmentImageClassPKReference");

					baseFragmentFieldJSONObject = _createImageJSONObject(
						(Map<String, Object>)
							fragmentImageClassPKReferenceMap.get(
								"classPKReferences"));

					Map<String, String> fragmentImageConfigurationMap =
						(Map<String, String>)
							fragmentImageClassPKReferenceMap.get(
								"fragmentImageConfiguration");

					JSONObject amImageConfigurationJSONObject =
						JSONFactoryUtil.createJSONObject();

					for (Map.Entry<String, String> entry :
							fragmentImageConfigurationMap.entrySet()) {

						amImageConfigurationJSONObject.put(
							entry.getKey(), entry.getValue());
					}

					editableFieldConfigJSONObject = JSONUtil.merge(
						editableFieldConfigJSONObject,
						JSONUtil.put(
							"imageConfiguration",
							amImageConfigurationJSONObject));
				}

				editableFieldConfigJSONObject = JSONUtil.merge(
					editableFieldConfigJSONObject,
					_createFragmentConfigJSONObject(fragmentImageMap));
			}

			if (editableFieldConfigJSONObject.length() > 0) {
				fragmentFieldJSONObject.put(
					"config", editableFieldConfigJSONObject);
			}

			jsonObject.put(
				fragmentFieldId,
				JSONUtil.merge(
					fragmentFieldJSONObject, baseFragmentFieldJSONObject));
		}

		return jsonObject;
	}

	private JSONObject _toFreeMarkerFragmentEntryProcessorJSONObject(
		Map<String, String> configurationTypes,
		Map<String, Object> fragmentConfigMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentConfigMap == null) {
			return jsonObject;
		}

		for (Map.Entry<String, Object> entry : fragmentConfigMap.entrySet()) {
			if (entry.getValue() instanceof HashMap) {
				Map<String, Object> childFragmentConfigMap =
					(Map<String, Object>)entry.getValue();

				jsonObject.put(
					entry.getKey(),
					_toFreeMarkerFragmentEntryProcessorJSONObject(
						configurationTypes, childFragmentConfigMap));
			}
			else {
				String type = configurationTypes.get(entry.getKey());

				if (Objects.equals(type, "colorPalette")) {
					jsonObject.put(
						entry.getKey(),
						JSONUtil.put("color", entry.getValue()));
				}
				else {
					jsonObject.put(entry.getKey(), entry.getValue());
				}
			}
		}

		return jsonObject;
	}

}