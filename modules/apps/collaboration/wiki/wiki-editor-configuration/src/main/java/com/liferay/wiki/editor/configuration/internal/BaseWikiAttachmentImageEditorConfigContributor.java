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

package com.liferay.wiki.editor.configuration.internal;

import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CachedSupplier;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.item.selector.criterion.WikiAttachmentItemSelectorCriterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;

/**
 * @author Sergio González
 * @author Roberto Díaz
 */
public abstract class BaseWikiAttachmentImageEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		boolean allowBrowseDocuments = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:allowBrowseDocuments"));

		if (!allowBrowseDocuments) {
			return;
		}

		Map<String, String> fileBrowserParamsMap =
			(Map<String, String>)inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:fileBrowserParams");

		final long wikiPageResourcePrimKey;

		if (fileBrowserParamsMap == null) {
			wikiPageResourcePrimKey = 0;
		}
		else {
			wikiPageResourcePrimKey = GetterUtil.getLong(
				fileBrowserParamsMap.get("wikiPageResourcePrimKey"));
		}

		if (wikiPageResourcePrimKey == 0) {
			collector.collect(
				"removePlugins",
				(String removePlugins) -> {
					if (Validator.isNotNull(removePlugins)) {
						removePlugins += ",ae_addimages";
					}
					else {
						removePlugins = "ae_addimages";
					}

					return removePlugins;
				});
		}

		CachedSupplier<String> cachedSupplier = new CachedSupplier<String>() {

			@Override
			protected String doGet() {
				String name = GetterUtil.getString(
					inputEditorTaglibAttributes.get(
						"liferay-ui:input-editor:name"));

				boolean inlineEdit = GetterUtil.getBoolean(
					inputEditorTaglibAttributes.get(
						"liferay-ui:input-editor:inlineEdit"));

				if (!inlineEdit) {
					String namespace = GetterUtil.getString(
						inputEditorTaglibAttributes.get(
							"liferay-ui:input-editor:namespace"));

					name = namespace + name;
				}

				return getItemSelectorURL(
					requestBackedPortletURLFactory, name + "selectItem",
					wikiPageResourcePrimKey, themeDisplay);
			}

		};

		collector.collect("filebrowserImageBrowseLinkUrl", cachedSupplier);
		collector.collect("filebrowserImageBrowseUrl", cachedSupplier);
	}

	protected ItemSelectorCriterion getImageItemSelectorCriterion(
		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion imageItemSelectorCriterion =
			new ImageItemSelectorCriterion();

		imageItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return imageItemSelectorCriterion;
	}

	protected abstract String getItemSelectorURL(
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String itemSelectedEventName, long wikiPageResourcePrimKey,
		ThemeDisplay themeDisplay);

	protected ItemSelectorCriterion getUploadItemSelectorCriterion(
		long wikiPageResourcePrimKey, ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		PortletURL uploadURL = requestBackedPortletURLFactory.createActionURL(
			WikiPortletKeys.WIKI);

		uploadURL.setParameter(
			ActionRequest.ACTION_NAME, "/wiki/upload_page_attachment");
		uploadURL.setParameter(
			"resourcePrimKey", String.valueOf(wikiPageResourcePrimKey));

		ItemSelectorCriterion uploadItemSelectorCriterion =
			new UploadItemSelectorCriterion(
				WikiPortletKeys.WIKI, uploadURL.toString(),
				LanguageUtil.get(themeDisplay.getLocale(), "page-attachments"));

		List<ItemSelectorReturnType> uploadDesiredItemSelectorReturnTypes =
			new ArrayList<>();

		uploadDesiredItemSelectorReturnTypes.add(
			new FileEntryItemSelectorReturnType());

		uploadItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			uploadDesiredItemSelectorReturnTypes);

		return uploadItemSelectorCriterion;
	}

	protected ItemSelectorCriterion getURLItemSelectorCriterion() {
		ItemSelectorCriterion urlItemSelectorCriterion =
			new URLItemSelectorCriterion();

		List<ItemSelectorReturnType> urlDesiredItemSelectorReturnTypes =
			new ArrayList<>();

		urlDesiredItemSelectorReturnTypes.add(new URLItemSelectorReturnType());

		urlItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			urlDesiredItemSelectorReturnTypes);

		return urlItemSelectorCriterion;
	}

	protected ItemSelectorCriterion getWikiAttachmentItemSelectorCriterion(
		long wikiPageResourcePrimKey,
		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes) {

		ItemSelectorCriterion attachmentItemSelectorCriterion =
			new WikiAttachmentItemSelectorCriterion(
				wikiPageResourcePrimKey,
				PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES);

		attachmentItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		return attachmentItemSelectorCriterion;
	}

}