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

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetCategoryModel;
import com.liferay.asset.kernel.model.AssetEntryModel;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.AssetTagStatsModel;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyModel;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.BlogsEntryModel;
import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryModel;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetModel;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureModel;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateModel;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleLocalizationModel;
import com.liferay.journal.model.JournalArticleModel;
import com.liferay.journal.model.JournalArticleResourceModel;
import com.liferay.message.boards.kernel.model.MBCategory;
import com.liferay.message.boards.kernel.model.MBCategoryConstants;
import com.liferay.message.boards.kernel.model.MBCategoryModel;
import com.liferay.message.boards.kernel.model.MBDiscussion;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBMessageModel;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.message.boards.kernel.model.MBThreadModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassNameModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferencesModel;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.ResourcePermissionModel;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.model.impl.PortletPreferencesModelImpl;
import com.liferay.portal.model.impl.ResourcePermissionModelImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletPreferencesFactoryImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.asset.model.impl.AssetCategoryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetEntryModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagModelImpl;
import com.liferay.portlet.asset.model.impl.AssetTagStatsModelImpl;
import com.liferay.portlet.asset.model.impl.AssetVocabularyModelImpl;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.util.SimpleCounter;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiNodeModel;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDataFactory {

	public DataFactory(InitContext initContext) throws Exception {
		super(initContext);

		_initContext = initContext;

		_resourcePermissionCounter = new SimpleCounter();

		_userDataFactory = new UserDataFactory(initContext);

		_journalDataFactory = new JournalDataFactory(
			initContext, _userDataFactory);

		_blogDataFactory = new BlogDataFactory(initContext);
		_dDLDataFactory = new DDLDataFactory(initContext);
		_dLDataFactory = new DLDataFactory(initContext, _userDataFactory);
		_layoutDataFactory = new LayoutDataFactory(initContext);
		_messageBoardDataFactory = new MessageBoardDataFactory(initContext);
		_releaseDataFactory = new ReleaseDataFactory(initContext);
		_socialActivityDataFactory = new SocialActivityDataFactory(initContext);
		_subscriptionDataFactory = new SubscriptionDataFactory(initContext);
		_wikiDataFactory = new WikiDataFactory(initContext);

		_dDLDataFactory.setUserDataFactory(_userDataFactory);
		_messageBoardDataFactory.setUserDataFactory(_userDataFactory);

		_dataFactories.put("blogDataFactory", _blogDataFactory);
		_dataFactories.put("dDLDataFactory", _dDLDataFactory);
		_dataFactories.put("dLDataFactory", _dLDataFactory);
		_dataFactories.put("journalDataFactory", _journalDataFactory);
		_dataFactories.put("layoutDataFactory", _layoutDataFactory);
		_dataFactories.put("messageBoardDataFactory", _messageBoardDataFactory);
		_dataFactories.put("releaseDataFactory", _releaseDataFactory);
		_dataFactories.put(
			"socialActivityDataFactory", _socialActivityDataFactory);
		_dataFactories.put("subscriptionDataFactory", _subscriptionDataFactory);
		_dataFactories.put("userDataFactory", _userDataFactory);
		_dataFactories.put("wikiDataFactory", _wikiDataFactory);

		_assetClassNameIds = new long[] {
			getClassNameId(BlogsEntry.class),
			getClassNameId(JournalArticle.class), getClassNameId(WikiPage.class)
		};

		List<String> lines = new ArrayList<>();

		String defaultAssetPublisherPreference = StringUtil.read(
			getResourceInputStream(
				DataFactoryConstants.DEFAULT_ASSET_PUBLISHER_PREFERENCE));

		_defaultAssetPublisherPortletPreference =
			(PortletPreferencesImpl)_portletPreferencesFactory.fromDefaultXML(
				defaultAssetPublisherPreference);

		initAssetCategoryModels();
		initAssetTagModels();
	}

	public List<Long> getAssetCategoryIds(AssetEntryModel assetEntryModel) {
		int maxGroupsCount = _initContext.getMaxGroupsCount();

		int maxAssetEntryToAssetCategoryCount =
			_initContext.getMaxAssetEntryToAssetCategoryCount();

		Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
			_assetCategoryModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		if ((assetCategoryModelsMap == null) ||
			assetCategoryModelsMap.isEmpty()) {

			return Collections.emptyList();
		}

		List<AssetCategoryModel> assetCategoryModels =
			assetCategoryModelsMap.get(assetEntryModel.getClassNameId());

		if ((assetCategoryModels == null) || assetCategoryModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetCategoryCounters == null) {
			_assetCategoryCounters =
				(Map<Long, SimpleCounter>[])new HashMap<?, ?>[maxGroupsCount];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetCategoryCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetCategoryIds = new ArrayList<>(
			maxAssetEntryToAssetCategoryCount);

		for (int i = 0; i < maxAssetEntryToAssetCategoryCount; i++) {
			int index = (int)counter.get() % assetCategoryModels.size();

			AssetCategoryModel assetCategoryModel = assetCategoryModels.get(
				index);

			assetCategoryIds.add(assetCategoryModel.getCategoryId());
		}

		return assetCategoryIds;
	}

	public List<AssetCategoryModel> getAssetCategoryModels() {
		List<AssetCategoryModel> allAssetCategoryModels = new ArrayList<>();

		for (Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap :
				_assetCategoryModelsMaps) {

			for (List<AssetCategoryModel> assetCategoryModels :
					assetCategoryModelsMap.values()) {

				allAssetCategoryModels.addAll(assetCategoryModels);
			}
		}

		return allAssetCategoryModels;
	}

	public List<Long> getAssetTagIds(AssetEntryModel assetEntryModel) {
		int maxAssetEntryToAssetTagCount =
			_initContext.getMaxAssetEntryToAssetTagCount();

		Map<Long, List<AssetTagModel>> assetTagModelsMap =
			_assetTagModelsMaps[(int)assetEntryModel.getGroupId() - 1];

		int maxGroupsCount = _initContext.getMaxGroupsCount();

		if ((assetTagModelsMap == null) || assetTagModelsMap.isEmpty()) {
			return Collections.emptyList();
		}

		List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
			assetEntryModel.getClassNameId());

		if ((assetTagModels == null) || assetTagModels.isEmpty()) {
			return Collections.emptyList();
		}

		if (_assetTagCounters == null) {
			_assetTagCounters =
				(Map<Long, SimpleCounter>[])new HashMap<?, ?>[maxGroupsCount];
		}

		SimpleCounter counter = getSimpleCounter(
			_assetTagCounters, assetEntryModel.getGroupId(),
			assetEntryModel.getClassNameId());

		List<Long> assetTagIds = new ArrayList<>(maxAssetEntryToAssetTagCount);

		for (int i = 0; i < maxAssetEntryToAssetTagCount; i++) {
			int index = (int)counter.get() % assetTagModels.size();

			AssetTagModel assetTagModel = assetTagModels.get(index);

			assetTagIds.add(assetTagModel.getTagId());
		}

		return assetTagIds;
	}

	public List<AssetTagModel> getAssetTagModels() {
		List<AssetTagModel> allAssetTagModels = new ArrayList<>();

		for (Map<Long, List<AssetTagModel>> assetTagModelsMap :
				_assetTagModelsMaps) {

			for (List<AssetTagModel> assetTagModels :
					assetTagModelsMap.values()) {

				allAssetTagModels.addAll(assetTagModels);
			}
		}

		return allAssetTagModels;
	}

	public List<AssetTagStatsModel> getAssetTagStatsModels() {
		List<AssetTagStatsModel> allAssetTagStatsModels = new ArrayList<>();

		for (List<AssetTagStatsModel> assetTagStatsModels :
				_assetTagStatsModelsArray) {

			allAssetTagStatsModels.addAll(assetTagStatsModels);
		}

		return allAssetTagStatsModels;
	}

	public List<AssetVocabularyModel> getAssetVocabularyModels() {
		List<AssetVocabularyModel> allAssetVocabularyModels = new ArrayList<>();

		allAssetVocabularyModels.add(_defaultAssetVocabularyModel);

		for (List<AssetVocabularyModel> assetVocabularyModels :
				_assetVocabularyModelsArray) {

			allAssetVocabularyModels.addAll(assetVocabularyModels);
		}

		return allAssetVocabularyModels;
	}

	public long getCounterNext() {
		SimpleCounter counter = _initContext.getCounter();

		return counter.get();
	}

	public Map<String, Object> getDataFactories() {
		return _dataFactories;
	}

	public long getNextAssetClassNameId(long groupId) {
		Integer index = _assetClassNameIdsIndexes.get(groupId);

		if (index == null) {
			index = 0;
		}

		long classNameId =
			_assetClassNameIds[index % _assetClassNameIds.length];

		_assetClassNameIdsIndexes.put(groupId, ++index);

		return classNameId;
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public void initAssetCategoryModels() {
		int maxAssetCategoryCount = _initContext.getMaxAssetCategoryCount();
		int maxAssetVocabularyCount = _initContext.getMaxAssetVocabularyCount();
		int maxGroupsCount = _initContext.getMaxGroupsCount();
		long defaultUserId = _initContext.getDefaultUserId();
		long globalGroupId = _userDataFactory.getGlobalGroupId();
		long sampleUserId = _initContext.getSampleUserId();

		_assetCategoryModelsArray =
			(List<AssetCategoryModel>[])new List<?>[maxGroupsCount];
		_assetCategoryModelsMaps =
			(Map<Long, List<AssetCategoryModel>>[])
				new HashMap<?, ?>[maxGroupsCount];
		_assetVocabularyModelsArray =
			(List<AssetVocabularyModel>[])new List<?>[maxGroupsCount];
		_defaultAssetVocabularyModel = newAssetVocabularyModel(
			globalGroupId, defaultUserId, null,
			PropsValues.ASSET_VOCABULARY_DEFAULT);

		StringBundler sb = new StringBundler(4);

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetVocabularyModel> assetVocabularyModels = new ArrayList<>(
				maxAssetVocabularyCount);
			List<AssetCategoryModel> assetCategoryModels = new ArrayList<>(
				maxAssetVocabularyCount * maxAssetCategoryCount);

			long lastRightCategoryId = 2;

			for (int j = 0; j < maxAssetVocabularyCount; j++) {
				sb.setIndex(0);

				sb.append(DataFactoryConstants.ASSET_VOCABULARY_NAME_PREFIX);
				sb.append(i);
				sb.append(StringPool.UNDERLINE);
				sb.append(j);

				AssetVocabularyModel assetVocabularyModel =
					newAssetVocabularyModel(
						i, sampleUserId, DataFactoryConstants.SAMPLE_USER_NAME,
						sb.toString());

				assetVocabularyModels.add(assetVocabularyModel);

				for (int k = 0; k < maxAssetCategoryCount; k++) {
					sb.setIndex(0);

					sb.append(DataFactoryConstants.ASSET_CATEGORY_NAME_PREFIX);
					sb.append(assetVocabularyModel.getVocabularyId());
					sb.append(StringPool.UNDERLINE);
					sb.append(k);

					AssetCategoryModel assetCategoryModel =
						newAssetCategoryModel(
							i, lastRightCategoryId, sb.toString(),
							assetVocabularyModel.getVocabularyId());

					lastRightCategoryId += 2;

					assetCategoryModels.add(assetCategoryModel);
				}
			}

			_assetCategoryModelsArray[i - 1] = assetCategoryModels;
			_assetVocabularyModelsArray[i - 1] = assetVocabularyModels;

			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				new HashMap<>();

			int pageSize =
				assetCategoryModels.size() / _assetClassNameIds.length;

			for (int j = 0; j < _assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;
				int toIndex = (j + 1) * pageSize;

				if (j == (_assetClassNameIds.length - 1)) {
					toIndex = assetCategoryModels.size();
				}

				assetCategoryModelsMap.put(
					_assetClassNameIds[j],
					assetCategoryModels.subList(fromIndex, toIndex));
			}

			_assetCategoryModelsMaps[i - 1] = assetCategoryModelsMap;
		}
	}

	public void initAssetTagModels() {
		int maxAssetTagCount = _initContext.getMaxAssetTagCount();
		int maxGroupsCount = _initContext.getMaxGroupsCount();
		SimpleCounter counter = _initContext.getCounter();
		long sampleUserId = _initContext.getSampleUserId();

		_assetTagModelsArray =
			(List<AssetTagModel>[])new List<?>[maxGroupsCount];
		_assetTagModelsMaps =
			(Map<Long, List<AssetTagModel>>[])
				new HashMap<?, ?>[maxGroupsCount];
		_assetTagStatsModelsArray =
			(List<AssetTagStatsModel>[])new List<?>[maxGroupsCount];

		for (int i = 1; i <= maxGroupsCount; i++) {
			List<AssetTagModel> assetTagModels = new ArrayList<>(
				maxAssetTagCount);
			List<AssetTagStatsModel> assetTagStatsModels = new ArrayList<>(
				maxAssetTagCount * 3);

			for (int j = 0; j < maxAssetTagCount; j++) {
				AssetTagModel assetTagModel = new AssetTagModelImpl();

				assetTagModel.setUuid(SequentialUUID.generate());
				assetTagModel.setTagId(counter.get());
				assetTagModel.setGroupId(i);
				assetTagModel.setCompanyId(_initContext.getCompanyId());
				assetTagModel.setUserId(sampleUserId);
				assetTagModel.setUserName(
					DataFactoryConstants.SAMPLE_USER_NAME);
				assetTagModel.setCreateDate(new Date());
				assetTagModel.setModifiedDate(new Date());
				assetTagModel.setName(
					DataFactoryConstants.ASSET_TAG_NAME_PREFIX + i + "_" + j);
				assetTagModel.setLastPublishDate(new Date());

				assetTagModels.add(assetTagModel);

				AssetTagStatsModel assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(BlogsEntry.class));

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(),
					getClassNameId(JournalArticle.class));

				assetTagStatsModels.add(assetTagStatsModel);

				assetTagStatsModel = newAssetTagStatsModel(
					assetTagModel.getTagId(), getClassNameId(WikiPage.class));

				assetTagStatsModels.add(assetTagStatsModel);
			}

			_assetTagModelsArray[i - 1] = assetTagModels;
			_assetTagStatsModelsArray[i - 1] = assetTagStatsModels;

			Map<Long, List<AssetTagModel>> assetTagModelsMap = new HashMap<>();

			int pageSize = assetTagModels.size() / _assetClassNameIds.length;

			for (int j = 0; j < _assetClassNameIds.length; j++) {
				int fromIndex = j * pageSize;
				int toIndex = (j + 1) * pageSize;

				if (j == (_assetClassNameIds.length - 1)) {
					toIndex = assetTagModels.size();
				}

				assetTagModelsMap.put(
					_assetClassNameIds[j],
					assetTagModels.subList(fromIndex, toIndex));
			}

			_assetTagModelsMaps[i - 1] = assetTagModelsMap;
		}
	}

	public AssetEntryModel newAssetEntryModel(BlogsEntryModel blogsEntryModel) {
		return newAssetEntryModel(
			blogsEntryModel.getGroupId(), blogsEntryModel.getCreateDate(),
			blogsEntryModel.getModifiedDate(), getClassNameId(BlogsEntry.class),
			blogsEntryModel.getEntryId(), blogsEntryModel.getUuid(), 0, true,
			true, ContentTypes.TEXT_HTML, blogsEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(
		DLFileEntryModel dLFileEntryModel) {

		return newAssetEntryModel(
			dLFileEntryModel.getGroupId(), dLFileEntryModel.getCreateDate(),
			dLFileEntryModel.getModifiedDate(),
			getClassNameId(DLFileEntry.class),
			dLFileEntryModel.getFileEntryId(), dLFileEntryModel.getUuid(),
			dLFileEntryModel.getFileEntryTypeId(), true, true,
			dLFileEntryModel.getMimeType(), dLFileEntryModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(DLFolderModel dLFolderModel) {
		return newAssetEntryModel(
			dLFolderModel.getGroupId(), dLFolderModel.getCreateDate(),
			dLFolderModel.getModifiedDate(), getClassNameId(DLFolder.class),
			dLFolderModel.getFolderId(), dLFolderModel.getUuid(), 0, true, true,
			null, dLFolderModel.getName());
	}

	public AssetEntryModel newAssetEntryModel(MBMessageModel mbMessageModel) {
		long classNameId = 0;
		boolean visible = false;

		if (mbMessageModel.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			classNameId = getClassNameId(MBDiscussion.class);
		}
		else {
			classNameId = getClassNameId(MBMessage.class);
			visible = true;
		}

		return newAssetEntryModel(
			mbMessageModel.getGroupId(), mbMessageModel.getCreateDate(),
			mbMessageModel.getModifiedDate(), classNameId,
			mbMessageModel.getMessageId(), mbMessageModel.getUuid(), 0, true,
			visible, ContentTypes.TEXT_HTML, mbMessageModel.getSubject());
	}

	public AssetEntryModel newAssetEntryModel(MBThreadModel mbThreadModel) {
		return newAssetEntryModel(
			mbThreadModel.getGroupId(), mbThreadModel.getCreateDate(),
			mbThreadModel.getModifiedDate(), getClassNameId(MBThread.class),
			mbThreadModel.getThreadId(), mbThreadModel.getUuid(), 0, true,
			false, StringPool.BLANK,
			String.valueOf(mbThreadModel.getRootMessageId()));
	}

	public AssetEntryModel newAssetEntryModel(
		ObjectValuePair<JournalArticleModel, JournalArticleLocalizationModel>
			objectValuePair) {

		DDMStructureModel defaultJournalDDMStructureModel =
			_journalDataFactory.getDefaultJournalDDMStructureModel();

		JournalArticleModel journalArticleModel = objectValuePair.getKey();
		JournalArticleLocalizationModel journalArticleLocalizationModel =
			objectValuePair.getValue();

		long resourcePrimKey = journalArticleModel.getResourcePrimKey();

		Map<Long, String> journalArticleResourceUUIDs =
			_journalDataFactory.getJournalArticleResourceUUIDs();

		String resourceUUID = journalArticleResourceUUIDs.get(resourcePrimKey);

		return newAssetEntryModel(
			journalArticleModel.getGroupId(),
			journalArticleModel.getCreateDate(),
			journalArticleModel.getModifiedDate(),
			getClassNameId(JournalArticle.class), resourcePrimKey, resourceUUID,
			defaultJournalDDMStructureModel.getStructureId(),
			journalArticleModel.isIndexable(), true, ContentTypes.TEXT_HTML,
			journalArticleLocalizationModel.getTitle());
	}

	public AssetEntryModel newAssetEntryModel(WikiPageModel wikiPageModel) {
		return newAssetEntryModel(
			wikiPageModel.getGroupId(), wikiPageModel.getCreateDate(),
			wikiPageModel.getModifiedDate(), getClassNameId(WikiPage.class),
			wikiPageModel.getResourcePrimKey(), wikiPageModel.getUuid(), 0,
			true, true, ContentTypes.TEXT_HTML, wikiPageModel.getTitle());
	}

	public List<PortletPreferencesModel>
		newAssetPublisherPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, BlogsPortletKeys.BLOGS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, WikiPortletKeys.WIKI,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public List<CounterModel> newCounterModels() {
		SimpleCounter counter = _initContext.getCounter();
		SimpleCounter socialActivityCounter =
			_socialActivityDataFactory.getSocialActivityCounter();

		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(_resourcePermissionCounter.get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(socialActivityCounter.get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public List<PortletPreferencesModel>
		newDDLPortletPreferencesModels(long plid) {

		List<PortletPreferencesModel> portletPreferencesModels =
			new ArrayList<>(3);

		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDLPortletKeys.DYNAMIC_DATA_LISTS,
				PortletConstants.DEFAULT_PREFERENCES));
		portletPreferencesModels.add(
			newPortletPreferencesModel(
				plid, DDMPortletKeys.DYNAMIC_DATA_MAPPING,
				PortletConstants.DEFAULT_PREFERENCES));

		return portletPreferencesModels;
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	public List<PortletPreferencesModel>
		newJournalPortletPreferencesModels(long plid) {

		return Collections.singletonList(
			newPortletPreferencesModel(
				plid, JournalPortletKeys.JOURNAL,
				PortletConstants.DEFAULT_PREFERENCES));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, long groupId, String portletId, int currentIndex)
		throws Exception {

		if (currentIndex == 1) {
			return newPortletPreferencesModel(
				plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
		}

		String assetPublisherQueryName = "assetCategories";

		if ((currentIndex % 2) == 0) {
			assetPublisherQueryName = "assetTags";
		}

		ObjectValuePair<String[], Integer> objectValuePair = null;

		Integer startIndex = _assetPublisherQueryStartIndexes.get(groupId);

		if (startIndex == null) {
			startIndex = 0;
		}

		if (assetPublisherQueryName.equals("assetCategories")) {
			Map<Long, List<AssetCategoryModel>> assetCategoryModelsMap =
				_assetCategoryModelsMaps[(int)groupId - 1];

			List<AssetCategoryModel> assetCategoryModels =
				assetCategoryModelsMap.get(getNextAssetClassNameId(groupId));

			if ((assetCategoryModels == null) ||
				assetCategoryModels.isEmpty()) {

				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetCategoriesQueryValues(
				assetCategoryModels, startIndex);
		}
		else {
			Map<Long, List<AssetTagModel>> assetTagModelsMap =
				_assetTagModelsMaps[(int)groupId - 1];

			List<AssetTagModel> assetTagModels = assetTagModelsMap.get(
				getNextAssetClassNameId(groupId));

			if ((assetTagModels == null) || assetTagModels.isEmpty()) {
				return newPortletPreferencesModel(
					plid, portletId, PortletConstants.DEFAULT_PREFERENCES);
			}

			objectValuePair = getAssetPublisherAssetTagsQueryValues(
				assetTagModels, startIndex);
		}

		String[] assetPublisherQueryValues = objectValuePair.getKey();

		_assetPublisherQueryStartIndexes.put(
			groupId, objectValuePair.getValue());

		PortletPreferences jxPortletPreferences =
			(PortletPreferences)_defaultAssetPublisherPortletPreference.clone();

		jxPortletPreferences.setValue("queryAndOperator0", "false");
		jxPortletPreferences.setValue("queryContains0", "true");
		jxPortletPreferences.setValue("queryName0", assetPublisherQueryName);
		jxPortletPreferences.setValues(
			"queryValues0",
			new String[] {
				assetPublisherQueryValues[0], assetPublisherQueryValues[1],
				assetPublisherQueryValues[2]
			});
		jxPortletPreferences.setValue("queryAndOperator1", "false");
		jxPortletPreferences.setValue("queryContains1", "false");
		jxPortletPreferences.setValue("queryName1", assetPublisherQueryName);
		jxPortletPreferences.setValue(
			"queryValues1", assetPublisherQueryValues[3]);

		return newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId, DDLRecordSetModel ddlRecordSetModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue("editable", "true");
		jxPortletPreferences.setValue(
			"recordSetId", String.valueOf(ddlRecordSetModel.getRecordSetId()));
		jxPortletPreferences.setValue("spreadsheet", "false");

		return newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public PortletPreferencesModel newPortletPreferencesModel(
			long plid, String portletId,
			JournalArticleResourceModel journalArticleResourceModel)
		throws Exception {

		PortletPreferences jxPortletPreferences = new PortletPreferencesImpl();

		jxPortletPreferences.setValue(
			"articleId", journalArticleResourceModel.getArticleId());
		jxPortletPreferences.setValue(
			"groupId",
			String.valueOf(journalArticleResourceModel.getGroupId()));

		return newPortletPreferencesModel(
			plid, portletId,
			_portletPreferencesFactory.toXML(jxPortletPreferences));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetCategoryModel assetCategoryModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			AssetCategory.class.getName(),
			String.valueOf(assetCategoryModel.getCategoryId()), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetTagModel assetTagModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			AssetTag.class.getName(), String.valueOf(assetTagModel.getTagId()),
			sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		AssetVocabularyModel assetVocabularyModel) {

		long defaultUserId = _initContext.getDefaultUserId();
		long sampleUserId = _initContext.getSampleUserId();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		if (assetVocabularyModel.getUserId() == defaultUserId) {
			return Collections.singletonList(
				newResourcePermissionModel(
					AssetVocabulary.class.getName(),
					String.valueOf(assetVocabularyModel.getVocabularyId()),
					ownerRoleModel.getRoleId(), defaultUserId));
		}

		return newResourcePermissionModels(
			AssetVocabulary.class.getName(),
			String.valueOf(assetVocabularyModel.getVocabularyId()),
			sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		BlogsEntryModel blogsEntryModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			BlogsEntry.class.getName(),
			String.valueOf(blogsEntryModel.getEntryId()), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDLRecordSetModel ddlRecordSetModel) {

		long defaultUserId = _initContext.getDefaultUserId();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			newResourcePermissionModel(
				DDLRecordSet.class.getName(),
				String.valueOf(ddlRecordSetModel.getRecordSetId()),
				ownerRoleModel.getRoleId(), defaultUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMStructureModel ddmStructureModel) {

		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();
		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();
		RoleModel userRoleModel = _userDataFactory.getUserRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMStructure.class.getName(),
			getClassName(ddmStructureModel.getClassNameId()));
		String primKey = String.valueOf(ddmStructureModel.getStructureId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(),
				ddmStructureModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, userRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DDMTemplateModel ddmTemplateModel) {

		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();
		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();
		RoleModel userRoleModel = _userDataFactory.getUserRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		String name = _getResourcePermissionModelName(
			DDMTemplate.class.getName(),
			getClassName(ddmTemplateModel.getResourceClassNameId()));
		String primKey = String.valueOf(ddmTemplateModel.getTemplateId());

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(),
				ddmTemplateModel.getUserId()));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, userRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFileEntryModel dlFileEntryModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			DLFileEntry.class.getName(),
			String.valueOf(dlFileEntryModel.getFileEntryId()), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		DLFolderModel dlFolderModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			DLFolder.class.getName(),
			String.valueOf(dlFolderModel.getFolderId()), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		GroupModel groupModel) {

		long sampleUserId = _initContext.getSampleUserId();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			newResourcePermissionModel(
				Group.class.getName(), String.valueOf(groupModel.getGroupId()),
				ownerRoleModel.getRoleId(), sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		JournalArticleResourceModel journalArticleResourceModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			JournalArticle.class.getName(),
			String.valueOf(journalArticleResourceModel.getResourcePrimKey()),
			sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		LayoutModel layoutModel) {

		return newResourcePermissionModels(
			Layout.class.getName(), String.valueOf(layoutModel.getPlid()), 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBCategoryModel mbCategoryModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			MBCategory.class.getName(),
			String.valueOf(mbCategoryModel.getCategoryId()), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		MBMessageModel mbMessageModel) {

		long sampleUserId = _initContext.getSampleUserId();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			newResourcePermissionModel(
				MBMessage.class.getName(),
				String.valueOf(mbMessageModel.getMessageId()),
				ownerRoleModel.getRoleId(), sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		PortletPreferencesModel portletPreferencesModel) {

		String portletId = portletPreferencesModel.getPortletId();

		String name = portletId;

		int index = portletId.indexOf(StringPool.UNDERLINE);

		if (index > 0) {
			name = portletId.substring(0, index);
		}

		String primKey = PortletPermissionUtil.getPrimaryKey(
			portletPreferencesModel.getPlid(), portletId);

		return newResourcePermissionModels(name, primKey, 0);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		RoleModel roleModel) {

		long sampleUserId = _initContext.getSampleUserId();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			newResourcePermissionModel(
				Role.class.getName(), String.valueOf(roleModel.getRoleId()),
				ownerRoleModel.getRoleId(), sampleUserId));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		String name, long primKey) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			name, String.valueOf(primKey), sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		UserModel userModel) {

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		return Collections.singletonList(
			newResourcePermissionModel(
				User.class.getName(), String.valueOf(userModel.getUserId()),
				ownerRoleModel.getRoleId(), userModel.getUserId()));
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiNodeModel wikiNodeModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			WikiNode.class.getName(), String.valueOf(wikiNodeModel.getNodeId()),
			sampleUserId);
	}

	public List<ResourcePermissionModel> newResourcePermissionModels(
		WikiPageModel wikiPageModel) {

		long sampleUserId = _initContext.getSampleUserId();

		return newResourcePermissionModels(
			WikiPage.class.getName(),
			String.valueOf(wikiPageModel.getResourcePrimKey()), sampleUserId);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetCategoriesQueryValues(
			List<AssetCategoryModel> assetCategoryModels, int index) {

		int maxAssetEntryToAssetCategoryCount =
			_initContext.getMaxAssetEntryToAssetCategoryCount();

		AssetCategoryModel assetCategoryModel0 = assetCategoryModels.get(
			index % assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel1 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount) %
				assetCategoryModels.size());
		AssetCategoryModel assetCategoryModel2 = assetCategoryModels.get(
			(index + maxAssetEntryToAssetCategoryCount * 2) %
				assetCategoryModels.size());

		int lastIndex =
			(index + maxAssetEntryToAssetCategoryCount * 3) %
				assetCategoryModels.size();

		AssetCategoryModel assetCategoryModel3 = assetCategoryModels.get(
			lastIndex);

		return new ObjectValuePair<>(
			new String[] {
				String.valueOf(assetCategoryModel0.getCategoryId()),
				String.valueOf(assetCategoryModel1.getCategoryId()),
				String.valueOf(assetCategoryModel2.getCategoryId()),
				String.valueOf(assetCategoryModel3.getCategoryId())
			},
			lastIndex + maxAssetEntryToAssetCategoryCount);
	}

	protected ObjectValuePair<String[], Integer>
		getAssetPublisherAssetTagsQueryValues(
			List<AssetTagModel> assetTagModels, int index) {

		int maxAssetEntryToAssetTagCount =
			_initContext.getMaxAssetEntryToAssetTagCount();

		AssetTagModel assetTagModel0 = assetTagModels.get(
			index % assetTagModels.size());
		AssetTagModel assetTagModel1 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount) % assetTagModels.size());
		AssetTagModel assetTagModel2 = assetTagModels.get(
			(index + maxAssetEntryToAssetTagCount * 2) % assetTagModels.size());

		int lastIndex =
			(index + maxAssetEntryToAssetTagCount * 3) % assetTagModels.size();

		AssetTagModel assetTagModel3 = assetTagModels.get(lastIndex);

		return new ObjectValuePair<>(
			new String[] {
				assetTagModel0.getName(), assetTagModel1.getName(),
				assetTagModel2.getName(), assetTagModel3.getName()
			},
			lastIndex + maxAssetEntryToAssetTagCount);
	}

	protected String getClassName(long classNameId) {
		for (ClassNameModel classNameModel :
				_initContext.getClassNameModelValues()) {

			if (classNameModel.getClassNameId() == classNameId) {
				return classNameModel.getValue();
			}
		}

		throw new RuntimeException(
			"Unable to find class name for id " + classNameId);
	}

	protected SimpleCounter getSimpleCounter(
		Map<Long, SimpleCounter>[] simpleCountersArray, long groupId,
		long classNameId) {

		Map<Long, SimpleCounter> simpleCounters =
			simpleCountersArray[(int)groupId - 1];

		if (simpleCounters == null) {
			simpleCounters = new HashMap<>();

			simpleCountersArray[(int)groupId - 1] = simpleCounters;
		}

		SimpleCounter simpleCounter = simpleCounters.get(classNameId);

		if (simpleCounter == null) {
			simpleCounter = new SimpleCounter(0);

			simpleCounters.put(classNameId, simpleCounter);
		}

		return simpleCounter;
	}

	protected AssetCategoryModel newAssetCategoryModel(
		long groupId, long lastRightCategoryId, String name,
		long vocabularyId) {

		long sampleUserId = _initContext.getSampleUserId();
		SimpleCounter counter = _initContext.getCounter();

		AssetCategoryModel assetCategoryModel = new AssetCategoryModelImpl();

		assetCategoryModel.setUuid(SequentialUUID.generate());
		assetCategoryModel.setCategoryId(counter.get());
		assetCategoryModel.setGroupId(groupId);
		assetCategoryModel.setCompanyId(_initContext.getCompanyId());
		assetCategoryModel.setUserId(sampleUserId);
		assetCategoryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
		assetCategoryModel.setCreateDate(new Date());
		assetCategoryModel.setModifiedDate(new Date());
		assetCategoryModel.setParentCategoryId(
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
		assetCategoryModel.setLeftCategoryId(lastRightCategoryId++);
		assetCategoryModel.setRightCategoryId(lastRightCategoryId++);
		assetCategoryModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetCategoryModel.setTitle(sb.toString());

		assetCategoryModel.setVocabularyId(vocabularyId);
		assetCategoryModel.setLastPublishDate(new Date());

		return assetCategoryModel;
	}

	protected AssetEntryModel newAssetEntryModel(
		long groupId, Date createDate, Date modifiedDate, long classNameId,
		long classPK, String uuid, long classTypeId, boolean listable,
		boolean visible, String mimeType, String title) {

		long sampleUserId = _initContext.getSampleUserId();
		SimpleCounter counter = _initContext.getCounter();

		AssetEntryModel assetEntryModel = new AssetEntryModelImpl();

		assetEntryModel.setEntryId(counter.get());
		assetEntryModel.setGroupId(groupId);
		assetEntryModel.setCompanyId(_initContext.getCompanyId());
		assetEntryModel.setUserId(sampleUserId);
		assetEntryModel.setUserName(DataFactoryConstants.SAMPLE_USER_NAME);
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

	protected AssetTagStatsModel newAssetTagStatsModel(
		long tagId, long classNameId) {

		AssetTagStatsModel assetTagStatsModel = new AssetTagStatsModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		assetTagStatsModel.setTagStatsId(counter.get());

		assetTagStatsModel.setTagId(tagId);
		assetTagStatsModel.setClassNameId(classNameId);

		return assetTagStatsModel;
	}

	protected AssetVocabularyModel newAssetVocabularyModel(
		long grouId, long userId, String userName, String name) {

		AssetVocabularyModel assetVocabularyModel =
			new AssetVocabularyModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		assetVocabularyModel.setUuid(SequentialUUID.generate());
		assetVocabularyModel.setVocabularyId(counter.get());
		assetVocabularyModel.setGroupId(grouId);
		assetVocabularyModel.setCompanyId(_initContext.getCompanyId());
		assetVocabularyModel.setUserId(userId);
		assetVocabularyModel.setUserName(userName);
		assetVocabularyModel.setCreateDate(new Date());
		assetVocabularyModel.setModifiedDate(new Date());
		assetVocabularyModel.setName(name);

		StringBundler sb = new StringBundler(4);

		sb.append("<?xml version=\"1.0\"?><root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"><Title language-id=\"en_US\">");
		sb.append(name);
		sb.append("</Title></root>");

		assetVocabularyModel.setTitle(sb.toString());

		assetVocabularyModel.setSettings(
			"multiValued=true\\nselectedClassNameIds=0");
		assetVocabularyModel.setLastPublishDate(new Date());

		return assetVocabularyModel;
	}

	protected PortletPreferencesModel newPortletPreferencesModel(
		long plid, String portletId, String preferences) {

		PortletPreferencesModel portletPreferencesModel =
			new PortletPreferencesModelImpl();

		SimpleCounter counter = _initContext.getCounter();

		portletPreferencesModel.setPortletPreferencesId(counter.get());

		portletPreferencesModel.setOwnerId(PortletKeys.PREFS_OWNER_ID_DEFAULT);
		portletPreferencesModel.setOwnerType(
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT);
		portletPreferencesModel.setPlid(plid);
		portletPreferencesModel.setPortletId(portletId);
		portletPreferencesModel.setPreferences(preferences);

		return portletPreferencesModel;
	}

	protected ResourcePermissionModel newResourcePermissionModel(
		String name, String primKey, long roleId, long ownerId) {

		ResourcePermissionModel resourcePermissionModel =
			new ResourcePermissionModelImpl();

		resourcePermissionModel.setResourcePermissionId(
			_resourcePermissionCounter.get());
		resourcePermissionModel.setCompanyId(_initContext.getCompanyId());
		resourcePermissionModel.setName(name);
		resourcePermissionModel.setScope(ResourceConstants.SCOPE_INDIVIDUAL);
		resourcePermissionModel.setPrimKey(primKey);
		resourcePermissionModel.setPrimKeyId(GetterUtil.getLong(primKey));
		resourcePermissionModel.setRoleId(roleId);
		resourcePermissionModel.setOwnerId(ownerId);
		resourcePermissionModel.setActionIds(1);
		resourcePermissionModel.setViewActionId(true);

		return resourcePermissionModel;
	}

	protected List<ResourcePermissionModel> newResourcePermissionModels(
		String name, String primKey, long ownerId) {

		RoleModel guestRoleModel = _userDataFactory.getGuestRoleModel();

		RoleModel ownerRoleModel = _userDataFactory.getOwnerRoleModel();

		RoleModel siteMemberRoleModel =
			_userDataFactory.getSiteMemberRoleModel();

		List<ResourcePermissionModel> resourcePermissionModels =
			new ArrayList<>(3);

		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, guestRoleModel.getRoleId(), 0));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, ownerRoleModel.getRoleId(), ownerId));
		resourcePermissionModels.add(
			newResourcePermissionModel(
				name, primKey, siteMemberRoleModel.getRoleId(), 0));

		return resourcePermissionModels;
	}

	protected void toInsertSQL(StringBundler sb, BaseModel<?> baseModel) {
		try {
			sb.append("insert into ");

			Class<?> clazz = baseModel.getClass();

			Field tableNameField = clazz.getField("TABLE_NAME");

			sb.append(tableNameField.get(null));

			sb.append(" values (");

			Field tableColumnsField = clazz.getField("TABLE_COLUMNS");

			for (Object[] tableColumn :
					(Object[][])tableColumnsField.get(null)) {

				String name = TextFormatter.format(
					(String)tableColumn[0], TextFormatter.G);

				if (name.endsWith(StringPool.UNDERLINE)) {
					name = name.substring(0, name.length() - 1);
				}

				int type = (int)tableColumn[1];

				if (type == Types.TIMESTAMP) {
					Method method = clazz.getMethod("get".concat(name));

					Date date = (Date)method.invoke(baseModel);

					if (date == null) {
						sb.append("null");
					}
					else {
						sb.append("'");
						sb.append(getDateString(date));
						sb.append("'");
					}
				}
				else if ((type == Types.VARCHAR) || (type == Types.CLOB)) {
					Method method = clazz.getMethod("get".concat(name));

					sb.append("'");
					sb.append(method.invoke(baseModel));
					sb.append("'");
				}
				else if (type == Types.BOOLEAN) {
					Method method = clazz.getMethod("is".concat(name));

					sb.append(method.invoke(baseModel));
				}
				else {
					Method method = clazz.getMethod("get".concat(name));

					sb.append(method.invoke(baseModel));
				}

				sb.append(", ");
			}

			sb.setIndex(sb.index() - 1);

			sb.append(");");
		}
		catch (ReflectiveOperationException roe) {
			ReflectionUtil.throwException(roe);
		}
	}

	private String _getResourcePermissionModelName(String... classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return StringPool.BLANK;
		}

		Arrays.sort(classNames);

		StringBundler sb = new StringBundler(classNames.length * 2);

		for (String className : classNames) {
			sb.append(className);
			sb.append(StringPool.DASH);
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private static final PortletPreferencesFactory _portletPreferencesFactory =
		new PortletPreferencesFactoryImpl();

	private Map<Long, SimpleCounter>[] _assetCategoryCounters;
	private List<AssetCategoryModel>[] _assetCategoryModelsArray;
	private Map<Long, List<AssetCategoryModel>>[] _assetCategoryModelsMaps;
	private final long[] _assetClassNameIds;
	private final Map<Long, Integer> _assetClassNameIdsIndexes =
		new HashMap<>();
	private final Map<Long, Integer> _assetPublisherQueryStartIndexes =
		new HashMap<>();
	private Map<Long, SimpleCounter>[] _assetTagCounters;
	private List<AssetTagModel>[] _assetTagModelsArray;
	private Map<Long, List<AssetTagModel>>[] _assetTagModelsMaps;
	private List<AssetTagStatsModel>[] _assetTagStatsModelsArray;
	private List<AssetVocabularyModel>[] _assetVocabularyModelsArray;
	private final BlogDataFactory _blogDataFactory;
	private final Map<String, Object> _dataFactories = new HashMap<>();
	private final DDLDataFactory _dDLDataFactory;
	private final PortletPreferencesImpl
		_defaultAssetPublisherPortletPreference;
	private AssetVocabularyModel _defaultAssetVocabularyModel;
	private final DLDataFactory _dLDataFactory;
	private final InitContext _initContext;
	private final JournalDataFactory _journalDataFactory;
	private final LayoutDataFactory _layoutDataFactory;
	private final MessageBoardDataFactory _messageBoardDataFactory;
	private final ReleaseDataFactory _releaseDataFactory;
	private final SimpleCounter _resourcePermissionCounter;
	private final SocialActivityDataFactory _socialActivityDataFactory;
	private final SubscriptionDataFactory _subscriptionDataFactory;
	private final UserDataFactory _userDataFactory;
	private final WikiDataFactory _wikiDataFactory;

}