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
import com.liferay.asset.kernel.model.AssetEntryModel;
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
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.journal.model.impl.JournalFolderModelImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivitySet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CTDataFactory extends BaseDataFactory {

	public AssetEntryModel newAssetEntryModel(
		JournalFolderModel journalFolderModel,
		CTCollectionModel cTCollectionModel) {

		return newAssetEntryModel(
			journalFolderModel.getGroupId(), journalFolderModel.getCreateDate(),
			journalFolderModel.getModifiedDate(),
			getClassNameId(JournalFolder.class),
			journalFolderModel.getFolderId(), journalFolderModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_PLAIN, journalFolderModel.getName(),
			cTCollectionModel);
	}

	public List<CTCollectionModel> newCTCollectionModels(UserModel userModel) {
		List<CTCollectionModel> cTCollectionModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_COUNT; i++) {
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

	public List<JournalFolderModel> newJournalFolderModels(
		CTCollectionModel cTCollectionModel, long groupId) {

		List<JournalFolderModel> journalFolderModels = new ArrayList<>(
			BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT);

		for (int i = 0; i < BenchmarksPropsValues.MAX_CT_JOURNAL_FOLDER_COUNT;
			 i++) {

			journalFolderModels.add(
				newJournalFolderModel(
					cTCollectionModel, groupId, "Journal Folder " + (i + 1)));
		}

		return journalFolderModels;
	}

	public PortletPreferencesModel newPortletPreferencesModel(
		List<CTCollectionModel> cTCollectionModels, long plid) {

		return newPortletPreferencesModel(
			plid,
			ProductNavigationProductMenuPortletKeys.
				PRODUCT_NAVIGATION_PRODUCT_MENU,
			"<portlet-preferences />", cTCollectionModels);
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title,
		CTCollectionModel cTCollectionModel) {

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(COMPANY_ID);
		assetEntryModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());
		assetEntryModel.setUserId(SAMPLE_USER_ID);
		assetEntryModel.setUserName(SAMPLE_USER_NAME);
		assetEntryModel.setCreateDate(createDate);
		assetEntryModel.setModifiedDate(modifiedDate);
		assetEntryModel.setClassNameId(classNameId);
		assetEntryModel.setClassPK(classPK);
		assetEntryModel.setClassUuid(uuid);
		assetEntryModel.setClassTypeId(classTypeId);
		assetEntryModel.setListable(listable);
		assetEntryModel.setVisible(visible);
		assetEntryModel.setStartDate(createDate);
		assetEntryModel.setEndDate(nextFutureDate());
		assetEntryModel.setPublishDate(createDate);
		assetEntryModel.setExpirationDate(nextFutureDate());
		assetEntryModel.setMimeType(mimeType);
		assetEntryModel.setTitle(title);

		return assetEntryModel;
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

	protected JournalFolderModel newJournalFolderModel(
		CTCollectionModel cTCollectionModel, long groupId, String name) {

		JournalFolderModel journalFolderModel = new JournalFolderModelImpl();

		long folderId = counter.get();

		journalFolderModel.setUuid(SequentialUUID.generate());
		journalFolderModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());
		journalFolderModel.setFolderId(folderId);
		journalFolderModel.setGroupId(groupId);
		journalFolderModel.setCompanyId(cTCollectionModel.getCompanyId());
		journalFolderModel.setUserId(cTCollectionModel.getUserId());
		journalFolderModel.setCreateDate(new Date());
		journalFolderModel.setModifiedDate(new Date());
		journalFolderModel.setParentFolderId(0);
		journalFolderModel.setTreePath("/" + folderId + "/");
		journalFolderModel.setName(name);

		return journalFolderModel;
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences,
		List<CTCollectionModel> cTCollectionModels) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		CTCollectionModel cTCollectionModel = cTCollectionModels.get(0);
		portletPreferencesModel.setCompanyId(COMPANY_ID);
		portletPreferencesModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());
		portletPreferencesModel.setPortletPreferencesId(counter.get());
		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
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