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

package com.liferay.frontend.editor.alloyeditor.web.internal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.Map;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
public class BaseAlloyEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		collector.collect("allowedContent", () -> Boolean.TRUE);
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

				return contentsLanguageId.replace("iw_", "he_");
			});
		collector.collect("disableNativeSpellChecker", () -> Boolean.FALSE);
		collector.collect(
			"extraPlugins",
			() ->
				"ae_autolink,ae_dragresize,ae_addimages,ae_imagealignment," +
					"ae_placeholder,ae_selectionregion,ae_tableresize," +
						"ae_tabletools,ae_uicore");
		collector.collect("imageScaleResize", () -> "scale");
		collector.collect(
			"language",
			() -> {
				String languageId = getLanguageId(themeDisplay);

				return languageId.replace("iw_", "he_");
			});
		collector.collect(
			"removePlugins",
			() ->
				"contextmenu,elementspath,image,link,liststyle,magicline," +
					"resize,tabletools,toolbar");
		collector.collect(
			"srcNode",
			() -> {
				String namespace = GetterUtil.getString(
					inputEditorTaglibAttributes.get(
						"liferay-ui:input-editor:namespace"));

				return namespace +
					GetterUtil.getString(
						inputEditorTaglibAttributes.get(
							"liferay-ui:input-editor:name"));
			});
	}

}