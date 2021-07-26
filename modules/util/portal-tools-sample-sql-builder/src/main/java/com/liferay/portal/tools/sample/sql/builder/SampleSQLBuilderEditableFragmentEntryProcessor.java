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

import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderEditableFragmentEntryProcessor
	extends SampleSQLBuilderFragmentEntryProcessor {

	public String getClassName() {
		return "com.liferay.fragment.entry.processor.editable." +
			"EditableFragmentEntryProcessor";
	}

	public JSONObject getDefaultEditableValuesJSONObject(String html) {
		JSONObject defaultEditableValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		Document document = getDocument(html);

		for (Element element :
				document.select("lfr-editable,*[data-lfr-editable-id]")) {

			EditableElementParser editableElementParser =
				_getEditableElementParser(element);

			if (editableElementParser == null) {
				continue;
			}

			JSONObject defaultValueJSONObject = JSONUtil.put(
				"config", editableElementParser.getAttributes(element)
			).put(
				"defaultValue", editableElementParser.getValue(element)
			);

			defaultEditableValuesJSONObject.put(
				EditableFragmentEntryProcessorUtil.getElementId(element),
				defaultValueJSONObject);
		}

		return defaultEditableValuesJSONObject;
	}

	public JSONObject getDefaultEditableValuesJSONObject(
		String html, String configuration) {

		return getDefaultEditableValuesJSONObject(html);
	}

}