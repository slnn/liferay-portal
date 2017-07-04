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

import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrin Chaudhary
 */
@Component(
	property = {"editor.name=ckeditor_creole"},
	service = EditorConfigContributor.class
)
public class CKEditorCreoleConfigContributor
	extends BaseCKEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		super.collectEditorConfigElementContributors(
			collector, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		collector.collect(
			"allowedContent",
			() ->
				"b strong i hr h1 h2 h3 h4 h5 h6 em ul ol li pre table tr " +
					"th; img a[*]");

		Map<String, String> fileBrowserParams =
			(Map<String, String>)inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:fileBrowserParams");

		if (fileBrowserParams != null) {
			String attachmentURLPrefix = fileBrowserParams.get(
				"attachmentURLPrefix");

			if (Validator.isNotNull(attachmentURLPrefix)) {
				collector.collect(
					"attachmentURLPrefix", () -> attachmentURLPrefix);
			}
		}

		collector.collect("decodeLinks", () -> Boolean.TRUE);
		collector.collect("disableObjectResizing", () -> Boolean.TRUE);
		collector.collect(
			"extraPlugins",
			() -> "a11yhelpbtn,creole,itemselector,lfrpopup,wikilink");
		collector.collect(
			"filebrowserWindowFeatures",
			() ->
				"title=" +
					LanguageUtil.get(themeDisplay.getLocale(), "browse"));
		collector.collect("format_tags", () -> "p;h1;h2;h3;h4;h5;h6;pre");
		collector.collect(
			"removePlugins",
			() -> {
				StringBundler sb = new StringBundler(4);

				sb.append("bidi,colorbutton,colordialog,div,elementspath,");
				sb.append("flash,font,forms,indentblock,justify,keystrokes,");
				sb.append("link,maximize,newpage,pagebreak,preview,print,");
				sb.append("save,showblocks,smiley,stylescombo,templates,video");

				return sb.toString();
			});
		collector.collect(
			"toolbar_creole",
			() -> getToolbarsCreoleJSONArray(inputEditorTaglibAttributes));
		collector.collect(
			"toolbar_phone",
			() -> getToolbarsPhoneJSONArray(inputEditorTaglibAttributes));
		collector.collect(
			"toolbar_tablet",
			() -> getToolbarsTabletJSONArray(inputEditorTaglibAttributes));
	}

	protected JSONArray getToolbarsCreoleJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		jsonArray.put(toJSONArray("['Bold', 'Italic', '-' ,'RemoveFormat']"));
		jsonArray.put(
			toJSONArray(
				"['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent']"));
		jsonArray.put(toJSONArray("['Format']"));
		jsonArray.put(toJSONArray("['Link', 'Unlink']"));
		jsonArray.put(
			toJSONArray(
				"['Table', '-','ImageSelector', '-', 'HorizontalRule', '-', " +
					"'SpecialChar']"));
		jsonArray.put("/");
		jsonArray.put(
			toJSONArray(
				"['Cut', 'Copy', 'Paste', '-', 'PasteText', 'PasteFromWord', " +
					"'-', 'SelectAll', '-', 'Undo', 'Redo']"));
		jsonArray.put(toJSONArray("['Find','Replace']"));

		if (isShowSource(inputEditorTaglibAttributes)) {
			jsonArray.put(toJSONArray("['Source']"));
		}

		jsonArray.put(toJSONArray("['A11YBtn']"));

		return jsonArray;
	}

	protected JSONArray getToolbarsPhoneJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		jsonArray.put(toJSONArray("['Bold', 'Italic']"));
		jsonArray.put(toJSONArray("['NumberedList', 'BulletedList']"));
		jsonArray.put(toJSONArray("['Link', 'Unlink']"));
		jsonArray.put(toJSONArray("['ImageSelector']"));

		if (isShowSource(inputEditorTaglibAttributes)) {
			jsonArray.put(toJSONArray("['Source']"));
		}

		return jsonArray;
	}

	protected JSONArray getToolbarsTabletJSONArray(
		Map<String, Object> inputEditorTaglibAttributes) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		jsonArray.put(toJSONArray("['Bold', 'Italic']"));
		jsonArray.put(
			toJSONArray(
				"['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent']"));
		jsonArray.put(toJSONArray("['Format']"));
		jsonArray.put(toJSONArray("['Link', 'Unlink']"));
		jsonArray.put(toJSONArray("['ImageSelector']"));

		if (isShowSource(inputEditorTaglibAttributes)) {
			jsonArray.put(toJSONArray("['Source']"));
		}

		return jsonArray;
	}

}