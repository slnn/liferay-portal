<#setting number_format = "computer">

<#assign
	assetDataFactory = dataFactory.getDataFactoryInstance("assetDataFactory")
	blogDataFactory = dataFactory.getDataFactoryInstance("blogDataFactory")
	classNameDataFactory = dataFactory.getDataFactoryInstance("classNameDataFactory")
	commerceDataFactory = dataFactory.getDataFactoryInstance("commerceDataFactory")
	counterDataFactory = dataFactory.getDataFactoryInstance("counterDataFactory")
	ddlDDMDataFactory = dataFactory.getDataFactoryInstance("ddlDDMDataFactory")
	dlDataFactory = dataFactory.getDataFactoryInstance("dlDataFactory")
	fragmentDataFactory = dataFactory.getDataFactoryInstance("fragmentDataFactory")
	journalDataFactory = dataFactory.getDataFactoryInstance("journalDataFactory")
	layoutDataFactory = dataFactory.getDataFactoryInstance("layoutDataFactory")
	messageBoardDataFactory = dataFactory.getDataFactoryInstance("messageBoardDataFactory")
	portletPreferenceDataFactory = dataFactory.getDataFactoryInstance("portletPreferenceDataFactory")
	releaseDataFactory = dataFactory.getDataFactoryInstance("releaseDataFactory")
	resourcePermissionDataFactory = dataFactory.getDataFactoryInstance("resourcePermissionDataFactory")
	socialActivityDataFactory = dataFactory.getDataFactoryInstance("socialActivityDataFactory")
	subscriptionDataFactory = dataFactory.getDataFactoryInstance("subscriptionDataFactory")
	userDataFactory = dataFactory.getDataFactoryInstance("userDataFactory")
	wikiDataFactory = dataFactory.getDataFactoryInstance("wikiDataFactory")
/>

<#macro insertAssetEntry
	_assetCategoryModelsMaps
	_assetTagModelsMaps
	_entry
	_classNameIds = []
	_categoryAndTag = false
>
	<#local assetEntryModel = assetDataFactory.newAssetEntryModel(_entry, _classNameIds)>

	${resourcePermissionDataFactory.toInsertSQL(assetEntryModel)}

	<#if _categoryAndTag>
		<#local assetCategoryIds = assetDataFactory.getAssetCategoryIds(assetEntryModel, _assetCategoryModelsMaps)>

		<#list assetCategoryIds as assetCategoryId>
			<#local assetEntryAssetCategoryRelId = counterDataFactory.getCounterNext()>

			insert into AssetEntryAssetCategoryRel values (0, 0, ${assetEntryAssetCategoryRelId}, ${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetCategoryId}, 0);
		</#list>

		<#local assetTagIds = assetDataFactory.getAssetTagIds(assetEntryModel, _assetTagModelsMaps)>

		<#list assetTagIds as assetTagId>
			${resourcePermissionDataFactory.toInsertSQL("AssetEntries_AssetTags", assetEntryModel.companyId, assetEntryModel.entryId, assetTagId)}
		</#list>
	</#if>
</#macro>

<#macro insertContentLayout
	_layoutModel
	_fragmentEntryModel
	_portletPreferencesFactory
>
	${resourcePermissionDataFactory.toInsertSQL(_layoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}

	<#local fragmentEntryLinkModel = fragmentDataFactory.newFragmentEntryLinkModel(_layoutModel, _fragmentEntryModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Layout"))>

	${resourcePermissionDataFactory.toInsertSQL(fragmentEntryLinkModel)}

	${resourcePermissionDataFactory.toInsertSQL(journalDataFactory.newJournalContentPortletPreferencesModel(fragmentEntryLinkModel, _portletPreferencesFactory))}

	<#local layoutPageTemplateStructureModel = fragmentDataFactory.newLayoutPageTemplateStructureModel(_layoutModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Layout"))>

	${resourcePermissionDataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	<#local layoutPageTemplateStructureRelModel = fragmentDataFactory.newLayoutPageTemplateStructureRelModel(_layoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModel)>

	${resourcePermissionDataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}
</#macro>

<#macro insertDDMContent
	_ddmStorageLinkId
	_ddmStructureId
	_entry
	_currentIndex = -1
>
	<#if _currentIndex = -1>
		<#local ddmContentModel = dlDataFactory.newDDMContentModel(_entry)>
	<#else>
		<#local ddmContentModel = ddlDDMDataFactory.newDDMContentModel(_entry, _currentIndex)>
	</#if>

	${resourcePermissionDataFactory.toInsertSQL(ddmContentModel)}

	${resourcePermissionDataFactory.toInsertSQL(dlDataFactory.newDDMStorageLinkModel(_ddmStorageLinkId, ddmContentModel, _ddmStructureId, classNameDataFactory.getClassNameId("com.liferay.dynamic.data.mapping.model.DDMContent")))}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureModel, classNameDataFactory.getClassName(_ddmStructureModel))}

	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(_ddmStructureVersionModel)}
</#macro>

<#macro insertDLFolder
	_ddmStructureId
	_dlAssetCategoryModelsMaps
	_dlAssetTagModelsMaps
	_dlFolderDepth
	_groupId
	_parentDLFolderId
>
	<#if _dlFolderDepth <= dlDataFactory.maxDLFolderDepth>
		<#local dlFolderModels = dlDataFactory.newDLFolderModels(_groupId, _parentDLFolderId)>

		<#list dlFolderModels as dlFolderModel>
			${resourcePermissionDataFactory.toInsertSQL(dlFolderModel)}

			<@insertAssetEntry
				_assetCategoryModelsMaps=_dlAssetCategoryModelsMaps
				_assetTagModelsMaps=_dlAssetTagModelsMaps
				_classNameIds=[classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFolder")]
				_entry=dlFolderModel
			/>

			<#local dlFileEntryModels = dlDataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${resourcePermissionDataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dlDataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${resourcePermissionDataFactory.toInsertSQL(dlFileVersionModel)}

				<@insertAssetEntry
					_assetCategoryModelsMaps=_dlAssetCategoryModelsMaps
					_assetTagModelsMaps=_dlAssetTagModelsMaps
					_classNameIds=[classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")]
					_entry=dlFileEntryModel
				/>

				<#local ddmStorageLinkId = counterDataFactory.getCounterNext()>

				<@insertDDMContent
					_ddmStorageLinkId=ddmStorageLinkId
					_ddmStructureId=_ddmStructureId
					_entry=dlFileEntryModel
				/>

				<@insertMBDiscussion
					_classNameId=classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")
					_classPK=dlFileEntryModel.fileEntryId
					_groupId=dlFileEntryModel.groupId
					_maxCommentCount=0
					_mbDiscussionAssetCategoryModelsMaps=_dlAssetCategoryModelsMaps
					_mbDiscussionAssetTagModelsMaps=_dlAssetTagModelsMaps
					_mbRootMessageId=counterDataFactory.getCounterNext()
					_mbThreadId=counterDataFactory.getCounterNext()
				/>

				${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(dlFileEntryModel, classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")))}

				<#local dlFileEntryMetadataModel = dlDataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${resourcePermissionDataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${resourcePermissionDataFactory.toInsertSQL(ddlDDMDataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel, classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntryMetadata")))}

				${csvFileWriter.write("documentLibrary", dlFileEntryModel.uuid + "," + dlFolderModel.folderId + "," + dlFileEntryModel.name + "," + dlFileEntryModel.fileEntryId + "\n")}
			</#list>

			<@insertDLFolder
				_ddmStructureId=_ddmStructureId
				_dlAssetCategoryModelsMaps=_dlAssetCategoryModelsMaps
				_dlAssetTagModelsMaps=_dlAssetTagModelsMaps
				_dlFolderDepth=_dlFolderDepth + 1
				_groupId=groupId
				_parentDLFolderId=dlFolderModel.folderId
			/>
		</#list>
	</#if>
</#macro>

<#macro insertGroup
	_groupModel
>
	${resourcePermissionDataFactory.toInsertSQL(_groupModel)}

	<#local layoutSetModels = layoutDataFactory.newLayoutSetModels(_groupModel.groupId)>

	<#list layoutSetModels as layoutSetModel>
		${resourcePermissionDataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${resourcePermissionDataFactory.toInsertSQL(_layoutModel)}

	${resourcePermissionDataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}
</#macro>

<#macro insertMBDiscussion
	_classNameId
	_classPK
	_groupId
	_maxCommentCount
	_mbDiscussionAssetCategoryModelsMaps
	_mbDiscussionAssetTagModelsMaps
	_mbRootMessageId
	_mbThreadId
>
	<#local mbThreadModel = messageBoardDataFactory.newMBThreadModel(_mbThreadId, _groupId, _mbRootMessageId)>

	${resourcePermissionDataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = messageBoardDataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage
		_mbMessageAssetCategoryModelsMaps=_mbDiscussionAssetCategoryModelsMaps
		_mbMessageAssetTagModelsMaps=_mbDiscussionAssetTagModelsMaps
		_mbMessageModel=mbRootMessageModel
	/>

	<#local mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage
			_mbMessageAssetCategoryModelsMaps=_mbDiscussionAssetCategoryModelsMaps
			_mbMessageAssetTagModelsMaps=_mbDiscussionAssetTagModelsMaps
			_mbMessageModel=mbMessageModel
		/>

		${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel, classNameDataFactory.getClassNameId("com.liferay.wiki.model.WikiPage"), classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")))}
	</#list>

	${resourcePermissionDataFactory.toInsertSQL(messageBoardDataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId))}
</#macro>

<#macro insertMBMessage
	_mbMessageAssetCategoryModelsMaps
	_mbMessageAssetTagModelsMaps
	_mbMessageModel
>
	${resourcePermissionDataFactory.toInsertSQL(_mbMessageModel)}

	<@insertAssetEntry
		_assetCategoryModelsMaps=_mbMessageAssetCategoryModelsMaps
		_assetTagModelsMaps=_mbMessageAssetTagModelsMaps
		_classNameIds=[classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion"), classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")]
		_entry=_mbMessageModel
	/>
</#macro>

<#macro insertUser
	_userModel
	_groupIds = []
	_roleIds = []
>
	${resourcePermissionDataFactory.toInsertSQL(_userModel)}

	${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newContactModel(_userModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.User")))}

	<#list _roleIds as roleId>
		${resourcePermissionDataFactory.toInsertSQL("Users_Roles", 0, roleId, _userModel.userId)}
	</#list>

	<#list _groupIds as groupId>
		${resourcePermissionDataFactory.toInsertSQL("Users_Groups", 0, groupId, _userModel.userId)}
	</#list>
</#macro>