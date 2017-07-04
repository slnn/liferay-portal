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

package com.liferay.frontend.editor.ckeditor.web.internal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Map;

/**
 * @author Ambrin Chaudhary
 */
public class BaseCKEditorConfigContributor extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		collector.collect("allowedContent", () -> Boolean.TRUE);
		collector.collect(
			"bodyClass",
			() -> {
				String cssClasses = GetterUtil.getString(
					inputEditorTaglibAttributes.get(
						"liferay-ui:input-editor:cssClasses"));

				return "html-editor " + HtmlUtil.escape(cssClasses);
			});
		collector.collect(
			"contentsCss",
			() -> {
				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				jsonArray.put(
					HtmlUtil.escape(
						PortalUtil.getStaticResourceURL(
							themeDisplay.getRequest(),
							themeDisplay.getPathThemeCss() + "/aui.css")));
				jsonArray.put(
					HtmlUtil.escape(
						PortalUtil.getStaticResourceURL(
							themeDisplay.getRequest(),
							themeDisplay.getPathThemeCss() + "/main.css")));

				return jsonArray;
			});
		collector.collect(
			"contentsLangDirection",
			() -> {
				String contentsLanguageDir = getContentsLanguageDir(
					inputEditorTaglibAttributes);

				return HtmlUtil.escapeJS(contentsLanguageDir);
			});
		collector.collect(
			"contentsLanguage",
			() -> {
				String contentsLanguageId = getContentsLanguageId(
					inputEditorTaglibAttributes);

				contentsLanguageId = contentsLanguageId.replace("iw", "he");
				contentsLanguageId = contentsLanguageId.replace("_", "-");

				return contentsLanguageId;
			});
		collector.collect("height", () -> 265);
		collector.collect(
			"language",
			() -> {
				String languageId = getLanguageId(themeDisplay);

				languageId = languageId.replace("iw", "he");
				languageId = languageId.replace("_", "-");

				return languageId;
			});

		boolean resizable = GetterUtil.getBoolean(
			(String)inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:resizable"));

		if (resizable) {
			collector.collect("resize_dir", () -> "vertical");
		}

		collector.collect("resize_enabled", () -> resizable);
	}

	protected boolean isShowSource(
		Map<String, Object> inputEditorTaglibAttributes) {

		return GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:showSource"));
	}

}