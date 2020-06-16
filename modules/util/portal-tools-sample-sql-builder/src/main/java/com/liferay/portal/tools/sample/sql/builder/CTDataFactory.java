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

import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.model.CTPreferencesModel;
import com.liferay.change.tracking.model.impl.CTCollectionModelImpl;
import com.liferay.change.tracking.model.impl.CTPreferencesModelImpl;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.model.FriendlyURLEntryMapping;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivitySet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CTDataFactory extends BaseDataFactory {

	public List<CTCollectionModel> newCTCollectionModels(UserModel userModel) {
		List<CTCollectionModel> cTCollectionModels = new ArrayList<>(
			PropsValues.MAX_CT_COUNT);

		for (int i = 0; i < PropsValues.MAX_CT_COUNT; i++) {
			StringBundler sb = new StringBundler(4);

			sb.append("Change List ");
			sb.append(i + 1);
			sb.append(" of ");
			sb.append(userModel.getScreenName());

			cTCollectionModels.add(
				newCTCollectionModel(userModel, sb.toString()));
		}

		return cTCollectionModels;
	}

	public CTPreferencesModel newCTPreferencesModel() {
		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
		cTPreferencesModel.setCompanyId(COMPANY_ID);

		return cTPreferencesModel;
	}

	public CTPreferencesModel newCTPreferencesModel(
		List<CTCollectionModel> cTCollectionModels) {

		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		CTCollectionModel cTCollectionModel = cTCollectionModels.get(0);

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
		cTPreferencesModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTPreferencesModel.setUserId(cTCollectionModel.getUserId());
		cTPreferencesModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());

		return cTPreferencesModel;
	}

	protected CTCollectionModel newCTCollectionModel(
		UserModel userModel, String name) {

		CTCollectionModel cTCollectionModel = new CTCollectionModelImpl();

		cTCollectionModel.setCtCollectionId(cTCollectionCounter.get());
		cTCollectionModel.setCompanyId(userModel.getCompanyId());
		cTCollectionModel.setCreateDate(new Date());
		cTCollectionModel.setModifiedDate(new Date());
		cTCollectionModel.setName(name);
		cTCollectionModel.setStatus(2);
		cTCollectionModel.setStatusByUserId(0);
		cTCollectionModel.setUserId(userModel.getUserId());

		return cTCollectionModel;
	}

	protected CTPreferencesModel newCTPreferencesModel(
		CTCollectionModel cTCollectionModel) {

		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());
		cTPreferencesModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTPreferencesModel.setUserId(cTCollectionModel.getUserId());
		cTPreferencesModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());

		return cTPreferencesModel;
	}

	private static void _initJournalArticleClassNames() {
		_journalArticleClassNames.add(User.class.getName());
		_journalArticleClassNames.add(JournalArticleResource.class.getName());
		_journalArticleClassNames.add(JournalArticle.class.getName());
		_journalArticleClassNames.add(FriendlyURLEntryMapping.class.getName());
		_journalArticleClassNames.add(FriendlyURLEntry.class.getName());
		_journalArticleClassNames.add(
			FriendlyURLEntryLocalization.class.getName());
		_journalArticleClassNames.add(
			JournalArticleLocalization.class.getName());
		_journalArticleClassNames.add(ResourcePermission.class.getName());
		_journalArticleClassNames.add(AssetEntry.class.getName());
		_journalArticleClassNames.add(DDMStorageLink.class.getName());
		_journalArticleClassNames.add(DDMStructureLink.class.getName());
		_journalArticleClassNames.add(DDMTemplateLink.class.getName());
		_journalArticleClassNames.add(SocialActivity.class.getName());
		_journalArticleClassNames.add(SocialActivitySet.class.getName());
		_journalArticleClassNames.add(AssetDisplayPageEntry.class.getName());
	}

	private static void _initJournalFolderClassNames() {
		_journalFolderClassNames.add(ResourcePermission.class.getName());
		_journalFolderClassNames.add(PortletPreferences.class.getName());
		_journalFolderClassNames.add(JournalFolder.class.getName());
		_journalFolderClassNames.add(AssetEntry.class.getName());
	}

	private static void _initWebContentDisplayClassNames() {
		_webContentDisplayClassNames.add(PortletPreferences.class.getName());
		_webContentDisplayClassNames.add(ResourcePermission.class.getName());
		_webContentDisplayClassNames.add(AssetEntry.class.getName());
	}

	private static void _initWidgetPageClassNames() {
		_widgetPageClassNames.add(Layout.class.getName());
		_widgetPageClassNames.add(LayoutFriendlyURL.class.getName());
		_widgetPageClassNames.add(FriendlyURLEntryMapping.class.getName());
		_widgetPageClassNames.add(FriendlyURLEntry.class.getName());
		_widgetPageClassNames.add(FriendlyURLEntryLocalization.class.getName());
		_widgetPageClassNames.add(ResourcePermission.class.getName());
		_widgetPageClassNames.add(AssetEntry.class.getName());
	}

	private static final List<String> _journalArticleClassNames =
		new ArrayList<>();
	private static final List<String> _journalFolderClassNames =
		new ArrayList<>();
	private static final List<String> _webContentDisplayClassNames =
		new ArrayList<>();
	private static final List<String> _widgetPageClassNames = new ArrayList<>();

	static {
		_initJournalFolderClassNames();
		_initJournalArticleClassNames();
		_initWidgetPageClassNames();
		_initWebContentDisplayClassNames();
	}

}