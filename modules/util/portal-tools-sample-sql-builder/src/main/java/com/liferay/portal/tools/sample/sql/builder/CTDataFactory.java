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

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionModel;
import com.liferay.change.tracking.model.CTEntryModel;
import com.liferay.change.tracking.model.CTPreferencesModel;
import com.liferay.change.tracking.model.impl.CTCollectionModelImpl;
import com.liferay.change.tracking.model.impl.CTEntryModelImpl;
import com.liferay.change.tracking.model.impl.CTPreferencesModelImpl;
import com.liferay.dynamic.data.mapping.model.DDMStorageLink;
import com.liferay.dynamic.data.mapping.model.DDMStorageLinkModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLinkModel;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderModel;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutFriendlyURLModel;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class CTDataFactory extends BaseDataFactory {

	public CTDataFactory() throws Exception {
	}

	public int getMaxCTCount() {
		return BenchmarksPropsValues.MAX_CT_COUNT;
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
				_newCTCollectionModel(userModel, sb.toString()));
		}

		return cTCollectionModels;
	}

	public List<CTEntryModel> newCTEntryModels(
		CTCollectionModel cTCollectionModel) {

		List<CTEntryModel> cTEntryModels = new ArrayList<>(cTEntryMap.size());

		cTEntryMap.forEach(
			(className, baseModels) -> {
				if (className.contains("-")) {
					int endIndex = className.lastIndexOf('-');

					className = className.substring(0, endIndex);
				}

				long modelClassNameId = getClassNameId(className);

				if (className.equals(JournalFolder.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							JournalFolderModel journalFolderModel =
								(JournalFolderModel)baseModel;

							long modelClassPK =
								journalFolderModel.getFolderId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(AssetEntry.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							AssetEntryModel assetEntryModel =
								(AssetEntryModel)baseModel;

							long modelClassPK = assetEntryModel.getEntryId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(
							JournalArticleResource.class.getName())) {

					baseModels.forEach(
						baseModel -> {
							JournalArticleResourceModel
								journalArticleResourceModel =
									(JournalArticleResourceModel)baseModel;

							long modelClassPK =
								journalArticleResourceModel.
									getResourcePrimKey();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(JournalArticle.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							JournalArticleModel journalArticleModel =
								(JournalArticleModel)baseModel;

							long modelClassPK = journalArticleModel.getId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(
							JournalArticleLocalization.class.getName())) {

					baseModels.forEach(
						baseModel -> {
							JournalArticleLocalizationModel
								journalArticleLocalizationModel =
									(JournalArticleLocalizationModel)baseModel;

							long modelClassPK =
								journalArticleLocalizationModel.
									getArticleLocalizationId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(DDMTemplateLink.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							DDMTemplateLinkModel dDMTemplateLinkModel =
								(DDMTemplateLinkModel)baseModel;

							long modelClassPK =
								dDMTemplateLinkModel.getTemplateLinkId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(DDMStorageLink.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							DDMStorageLinkModel dDMStorageLinkModel =
								(DDMStorageLinkModel)baseModel;

							long modelClassPK =
								dDMStorageLinkModel.getStorageLinkId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(SocialActivity.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							SocialActivityModel socialActivityModel =
								(SocialActivityModel)baseModel;

							long modelClassPK =
								socialActivityModel.getActivityId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(Layout.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							LayoutModel layoutModel = (LayoutModel)baseModel;

							long modelClassPK = layoutModel.getPlid();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(LayoutFriendlyURL.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							LayoutFriendlyURLModel layoutFriendlyURLModel =
								(LayoutFriendlyURLModel)baseModel;

							long modelClassPK =
								layoutFriendlyURLModel.getLayoutFriendlyURLId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
				else if (className.equals(PortletPreferences.class.getName())) {
					baseModels.forEach(
						baseModel -> {
							PortletPreferencesModel portletPreferencesModel =
								(PortletPreferencesModel)baseModel;

							long modelClassPK =
								portletPreferencesModel.
									getPortletPreferencesId();

							cTEntryModels.add(
								_newCTEntryModel(
									cTCollectionModel, modelClassNameId,
									modelClassPK));
						});
				}
			});

		return cTEntryModels;
	}

	public CTPreferencesModel newCTPreferencesModel() {
		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		// PK fields

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());

		// Audit fields

		cTPreferencesModel.setCompanyId(COMPANY_ID);

		return cTPreferencesModel;
	}

	public CTPreferencesModel newCTPreferencesModel(
		List<CTCollectionModel> cTCollectionModels) {

		return _newCTPreferencesModel(
			cTCollectionModels.get(BenchmarksPropsValues.CT_INDEX));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		CTCollectionModel cTCollectionModel) {

		return Collections.singletonList(
			newResourcePermissionModel(
				CTCollection.class.getName(),
				String.valueOf(cTCollectionModel.getCtCollectionId()),
				OWNER_ROLE_ID, cTCollectionModel.getUserId()));
	}

	private CTCollectionModel _newCTCollectionModel(
		UserModel userModel, String name) {

		CTCollectionModel cTCollectionModel = new CTCollectionModelImpl();

		// PK fields

		cTCollectionModel.setCtCollectionId(cTCollectionCounter.get());

		// Audit fields

		cTCollectionModel.setCompanyId(userModel.getCompanyId());
		cTCollectionModel.setCreateDate(new Date());
		cTCollectionModel.setModifiedDate(new Date());

		// Other fields

		cTCollectionModel.setName(name);
		cTCollectionModel.setStatus(2);
		cTCollectionModel.setStatusByUserId(0);
		cTCollectionModel.setUserId(userModel.getUserId());

		return cTCollectionModel;
	}

	private CTEntryModel _newCTEntryModel(
		CTCollectionModel cTCollectionModel, long modelClassNameId,
		long modelClassPK) {

		CTEntryModel cTEntryModel = new CTEntryModelImpl();

		cTEntryModel.setCtEntryId(cTEntryCounter.get());
		cTEntryModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTEntryModel.setUserId(cTCollectionModel.getUserId());
		cTEntryModel.setCreateDate(new Date());
		cTEntryModel.setModifiedDate(new Date());
		cTEntryModel.setCtCollectionId(cTCollectionModel.getCtCollectionId());
		cTEntryModel.setModelClassNameId(modelClassNameId);
		cTEntryModel.setModelClassPK(modelClassPK);

		return cTEntryModel;
	}

	private CTPreferencesModel _newCTPreferencesModel(
		CTCollectionModel cTCollectionModel) {

		CTPreferencesModel cTPreferencesModel = new CTPreferencesModelImpl();

		// PK fields

		cTPreferencesModel.setCtPreferencesId(cTPreferencesCounter.get());

		// Audit fields

		cTPreferencesModel.setCompanyId(cTCollectionModel.getCompanyId());
		cTPreferencesModel.setUserId(cTCollectionModel.getUserId());

		// Other fields

		cTPreferencesModel.setCtCollectionId(
			cTCollectionModel.getCtCollectionId());

		// Autogenerated fields

		cTPreferencesModel.setMvccVersion(1);

		return cTPreferencesModel;
	}

}