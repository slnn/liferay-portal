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
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderFragmentLayoutStructureItemImporter {

	public SampleSQLBuilderFragmentLayoutStructureItemImporter(
		ObjectMapper objectMapper) {

		_objectMapper = objectMapper;
	}

	public LayoutStructureItem addLayoutStructureItem(
			Layout layout, LayoutStructure layoutStructure,
			PageElement pageElement, String parentItemId, int position,
			Set<String> warningMessages,
			Map<Long, List<FragmentEntryLinkModel>> layoutFragmentEntryLinkMap)
		throws Exception {

		Map<String, Object> definitionMap = _getDefinitionMap(
			pageElement.getDefinition());

		Map<String, Object> fragmentDefinitionMap =
			(Map<String, Object>)definitionMap.get("fragment");

		String fragmentKey = (String)fragmentDefinitionMap.get("key");

		FragmentEntryLinkModel fragmentEntryLinkModel = null;

		for (FragmentEntryLinkModel entry :
				layoutFragmentEntryLinkMap.get(layout.getPlid())) {

			if (fragmentKey.equals(entry.getRendererKey())) {
				fragmentEntryLinkModel = entry;
			}
		}

		if (fragmentEntryLinkModel == null) {
			return null;
		}

		LayoutStructureItem layoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLinkModel.getFragmentEntryLinkId(), parentItemId,
				position);

		if (definitionMap != null) {
			Map<String, Object> fragmentConfigMap =
				(Map<String, Object>)definitionMap.get("fragmentConfig");
			Map<String, Object> fragmentStyleMap =
				(Map<String, Object>)definitionMap.get("fragmentStyle");

			if (MapUtil.isNotEmpty(fragmentConfigMap) ||
				MapUtil.isNotEmpty(fragmentStyleMap)) {

				JSONObject commonStylesJSONObject = _toStylesJSONObject(
					fragmentStyleMap);
				JSONObject configStylesJSONObject = _toStylesJSONObject(
					fragmentConfigMap);

				for (String key : commonStylesJSONObject.keySet()) {
					if (Validator.isNull(
							configStylesJSONObject.getString(key))) {

						configStylesJSONObject.put(
							key, commonStylesJSONObject.get(key));
					}
				}

				JSONObject jsonObject = JSONUtil.put(
					"styles",
					JSONUtil.merge(
						commonStylesJSONObject, configStylesJSONObject));

				layoutStructureItem.updateItemConfig(jsonObject);
			}

			if (definitionMap.containsKey("fragmentViewports")) {
				List<Map<String, Object>> fragmentViewports =
					(List<Map<String, Object>>)definitionMap.get(
						"fragmentViewports");

				for (Map<String, Object> fragmentViewport : fragmentViewports) {
					JSONObject jsonObject = JSONUtil.put(
						(String)fragmentViewport.get("id"),
						_toFragmentViewportStylesJSONObject(fragmentViewport));

					layoutStructureItem.updateItemConfig(jsonObject);
				}
			}
		}

		return layoutStructureItem;
	}

	public void updateFragmentEntryLinkModels(
			List<FragmentEntryLinkModel> fragmentEntryLinkModels,
			PageElement pageElement, ObjectMapper objectMapper,
			List<SampleSQLBuilderFragmentEntryProcessor>
				fragmentEntryProcessorList)
		throws Exception {

		Map<String, Object> definitionMap = _getDefinitionMap(
			pageElement.getDefinition());

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

	private JSONObject _toStylesJSONObject(Map<String, Object> styles) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (MapUtil.isEmpty(styles)) {
			return jsonObject;
		}

		jsonObject.put("backgroundColor", styles.get("backgroundColor"));

		Object borderColor = styles.get("borderColor");

		if (borderColor instanceof String) {
			borderColor = _colors.getOrDefault(
				borderColor, (String)borderColor);
		}

		String borderRadius = GetterUtil.getString(styles.get("borderRadius"));

		Object shadow = styles.getOrDefault("boxShadow", styles.get("shadow"));

		String textAlign = GetterUtil.getString(styles.get("textAlign"));

		if (Validator.isNull(textAlign)) {
			for (String alignKey : _ALIGN_KEYS) {
				if (styles.containsKey(alignKey)) {
					textAlign = GetterUtil.getString(styles.get(alignKey));

					break;
				}
			}
		}

		Object textColor = styles.get("textColor");

		if (textColor instanceof String) {
			textColor = _colors.getOrDefault(textColor, (String)textColor);
		}

		return jsonObject.put(
			"borderColor", borderColor
		).put(
			"borderRadius",
			_borderRadiuses.getOrDefault(borderRadius, borderRadius)
		).put(
			"borderWidth", styles.get("borderWidth")
		).put(
			"fontFamily", styles.get("fontFamily")
		).put(
			"fontSize", styles.get("fontSize")
		).put(
			"fontWeight", styles.get("fontWeight")
		).put(
			"height", styles.get("height")
		).put(
			"marginBottom", styles.get("marginBottom")
		).put(
			"marginLeft", styles.get("marginLeft")
		).put(
			"marginRight", styles.get("marginRight")
		).put(
			"marginTop", styles.get("marginTop")
		).put(
			"maxHeight", styles.get("maxHeight")
		).put(
			"maxWidth", styles.get("maxWidth")
		).put(
			"minHeight", styles.get("minHeight")
		).put(
			"minWidth", styles.get("minWidth")
		).put(
			"opacity", styles.get("opacity")
		).put(
			"overflow", styles.get("overflow")
		).put(
			"paddingBottom", styles.get("paddingBottom")
		).put(
			"paddingLeft", styles.get("paddingLeft")
		).put(
			"paddingRight", styles.get("paddingRight")
		).put(
			"paddingTop", styles.get("paddingTop")
		).put(
			"shadow",
			_shadows.getOrDefault(shadow, GetterUtil.getString(shadow))
		).put(
			"textAlign", textAlign
		).put(
			"textColor", textColor
		).put(
			"width", styles.get("width")
		);
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

		Map<String, Object> valueI18nMap = (Map<String, Object>)map.get(
			"value_i18n");

		if (valueI18nMap != null) {
			for (Map.Entry<String, Object> entry : valueI18nMap.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}

			return jsonObject;
		}

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
				jsonObject.put(
					entry.getKey(), JSONFactoryUtil.createJSONObject());
			}
		}

		Map<String, Object> valueMap = (Map<String, Object>)fragmentLinkMap.get(
			"value");

		if (valueMap != null) {
			jsonObject = JSONUtil.merge(
				jsonObject, JSONFactoryUtil.createJSONObject());
		}

		jsonObject = JSONUtil.merge(
			jsonObject, JSONFactoryUtil.createJSONObject());

		jsonObject.put("mapperType", "link");

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

	private Map<String, Object> _getDefinitionMap(Object definition)
		throws Exception {

		Map<String, Object> definitionMap = null;

		if (definition instanceof Map) {
			definitionMap = (Map<String, Object>)definition;
		}
		else {
			definitionMap = _objectMapper.readValue(
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

	private JSONObject _toFragmentViewportStylesJSONObject(
		Map<String, Object> fragmentViewport) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (MapUtil.isEmpty(fragmentViewport)) {
			return jsonObject;
		}

		Map<String, Object> fragmentViewportStyle =
			(Map<String, Object>)fragmentViewport.get("fragmentViewportStyle");

		if (MapUtil.isEmpty(fragmentViewportStyle)) {
			return jsonObject;
		}

		return JSONUtil.put(
			"styles",
			jsonObject.put(
				"marginBottom", fragmentViewportStyle.get("marginBottom")
			).put(
				"marginLeft", fragmentViewportStyle.get("marginLeft")
			).put(
				"marginRight", fragmentViewportStyle.get("marginRight")
			).put(
				"marginTop", fragmentViewportStyle.get("marginTop")
			).put(
				"maxHeight", fragmentViewportStyle.get("maxHeight")
			).put(
				"paddingBottom", fragmentViewportStyle.get("paddingBottom")
			).put(
				"paddingLeft", fragmentViewportStyle.get("paddingLeft")
			).put(
				"paddingRight", fragmentViewportStyle.get("paddingRight")
			).put(
				"paddingTop", fragmentViewportStyle.get("paddingTop")
			));
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

	private static final String[] _ALIGN_KEYS = {
		"buttonAlign", "contentAlign", "imageAlign", "textAlign"
	};

	private static final Map<String, String> _borderRadiuses =
		HashMapBuilder.put(
			"lg", "0.375rem"
		).put(
			"none", StringPool.BLANK
		).put(
			"sm", "0.1875rem"
		).build();
	private static final Map<String, String> _colors = HashMapBuilder.put(
		"danger", "#DA1414"
	).put(
		"dark", "#272833"
	).put(
		"gray-dark", "#393A4A"
	).put(
		"info", "#2E5AAC"
	).put(
		"light", "#F1F2F5"
	).put(
		"lighter", "#F7F8F9"
	).put(
		"primary", "#0B5FFF"
	).put(
		"secondary", "#6B6C7E"
	).put(
		"success", "#287D3C"
	).put(
		"warning", "#B95000"
	).put(
		"white", "#FFFFFF"
	).build();
	private static final Map<String, String> _shadows = HashMapBuilder.put(
		"lg", "0 1rem 3rem rgba(0, 0, 0, .175)"
	).put(
		"sm", "0 .125rem .25rem rgba(0, 0, 0, .075)"
	).build();

	private final ObjectMapper _objectMapper;

}