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

import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderFreeMarkerFragmentEntryProcessor {

	public String getClassName() {
		return "com.liferay.fragment.entry.processor.freemarker." +
			"FreeMarkerFragmentEntryProcessor";
	}

	public JSONObject getConfigurationDefaultValuesJSONObject(
		String configuration) {

		List<FragmentConfigurationField> fragmentConfigurationFields =
			getFragmentConfigurationFields(configuration);

		JSONObject defaultValuesJSONObject = JSONFactoryUtil.createJSONObject();

		for (FragmentConfigurationField fragmentConfigurationField :
				fragmentConfigurationFields) {

			defaultValuesJSONObject.put(
				fragmentConfigurationField.getName(),
				getFieldValue(fragmentConfigurationField, null));
		}

		return defaultValuesJSONObject;
	}

	public JSONObject getDefaultEditableValuesJSONObject(
		String html, String configuration) {

		return getConfigurationDefaultValuesJSONObject(configuration);
	}

	public JSONArray getFieldSetsJSONArray(String configuration)
		throws Exception {

		JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(
			configuration);

		return configurationJSONObject.getJSONArray("fieldSets");
	}

	public List<FragmentConfigurationField> getFragmentConfigurationFields(
			String configuration)
		throws Exception {

		JSONArray fieldSetsJSONArray = getFieldSetsJSONArray(configuration);

		if (fieldSetsJSONArray == null) {
			return Collections.emptyList();
		}

		List<FragmentConfigurationField> fragmentConfigurationFields =
			new ArrayList<>();

		Iterator<JSONObject> iterator1 = fieldSetsJSONArray.iterator();

		iterator1.forEachRemaining(
			fieldSetJSONObject -> {
				JSONArray fieldSetFieldsJSONArray =
					fieldSetJSONObject.getJSONArray("fields");

				Iterator<JSONObject> iterator2 =
					fieldSetFieldsJSONArray.iterator();

				iterator2.forEachRemaining(
					fieldSetFieldsJSONObject -> fragmentConfigurationFields.add(
						new FragmentConfigurationField(
							fieldSetFieldsJSONObject)));
			});

		return fragmentConfigurationFields;
	}

}