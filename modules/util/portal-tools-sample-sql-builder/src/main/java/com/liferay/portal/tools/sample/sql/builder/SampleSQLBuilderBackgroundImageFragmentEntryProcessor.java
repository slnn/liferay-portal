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

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderBackgroundImageFragmentEntryProcessor
	extends SampleSQLBuilderFragmentEntryProcessor {

	public String getClassName() {
		return "com.liferay.fragment.entry.processor.background.image." +
			"BackgroundImageFragmentEntryProcessor";
	}

	public JSONObject getDefaultEditableValuesJSONObject(
		String html, String configuration) {

		JSONObject defaultEditableValuesJSONObject =
			JSONFactoryUtil.createJSONObject();

		Document document = getDocument(html);

		for (Element element :
				document.select("[data-lfr-background-image-id]")) {

			String id = element.attr("data-lfr-background-image-id");

			defaultEditableValuesJSONObject.put(
				id, JSONFactoryUtil.createJSONObject());
		}

		return defaultEditableValuesJSONObject;
	}

}