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
	portletPreferenceDataFactory = dataFactory.getDataFactoryInstance("portletPreferenceDataFactory")
/>

<#macro insertAssetEntry
	_assetCategoryModelsMaps
	_assetTagModelsMaps
	_entry
	_classNameIds = []
	_categoryAndTag = false
>
	<#local assetEntryModel = assetDataFactory.newAssetEntryModel(_entry, _classNameIds)>

	${dataFactory.toInsertSQL(assetEntryModel)}

	<#if _categoryAndTag>
		<#local assetCategoryIds = assetDataFactory.getAssetCategoryIds(assetEntryModel, _assetCategoryModelsMaps)>

		<#list assetCategoryIds as assetCategoryId>
			<#local assetEntryAssetCategoryRelId = counterDataFactory.getCounterNext()>

			insert into AssetEntryAssetCategoryRel values (0, 0, ${assetEntryAssetCategoryRelId}, ${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetCategoryId}, 0);
		</#list>

		<#local assetTagIds = assetDataFactory.getAssetTagIds(assetEntryModel, _assetTagModelsMaps)>

		<#list assetTagIds as assetTagId>
			${dataFactory.toInsertSQL("AssetEntries_AssetTags", assetEntryModel.companyId, assetEntryModel.entryId, assetTagId)}
		</#list>
	</#if>
</#macro>

<#macro insertContentLayout
	_layoutModel
	_fragmentEntryModel
	_portletPreferencesFactory
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}

	<#local fragmentEntryLinkModel = fragmentDataFactory.newFragmentEntryLinkModel(_layoutModel, _fragmentEntryModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Layout"))>

	${dataFactory.toInsertSQL(fragmentEntryLinkModel)}

	${dataFactory.toInsertSQL(journalDataFactory.newJournalContentPortletPreferencesModel(fragmentEntryLinkModel, _portletPreferencesFactory))}

	<#local layoutPageTemplateStructureModel = fragmentDataFactory.newLayoutPageTemplateStructureModel(_layoutModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Layout"))>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	<#local layoutPageTemplateStructureRelModel = fragmentDataFactory.newLayoutPageTemplateStructureRelModel(_layoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModel)>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}
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

	${dataFactory.toInsertSQL(ddmContentModel)}

	${dataFactory.toInsertSQL(dlDataFactory.newDDMStorageLinkModel(_ddmStorageLinkId, ddmContentModel, _ddmStructureId, classNameDataFactory.getClassNameId("com.liferay.dynamic.data.mapping.model.DDMContent")))}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${dataFactory.toInsertSQL(_ddmStructureModel, classNameDataFactory.getClassName(_ddmStructureModel))}

	${dataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${dataFactory.toInsertSQL(_ddmStructureVersionModel)}
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
			${dataFactory.toInsertSQL(dlFolderModel)}

			<@insertAssetEntry
				_assetCategoryModelsMaps=_dlAssetCategoryModelsMaps
				_assetTagModelsMaps=_dlAssetTagModelsMaps
				_classNameIds=[classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFolder")]
				_entry=dlFolderModel
			/>

			<#local dlFileEntryModels = dlDataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${dataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dlDataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${dataFactory.toInsertSQL(dlFileVersionModel)}

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

				${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(dlFileEntryModel, classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")))}

				<#local dlFileEntryMetadataModel = dlDataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${dataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${dataFactory.toInsertSQL(ddlDDMDataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel, classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntryMetadata")))}

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
	${dataFactory.toInsertSQL(_groupModel)}

	<#local layoutSetModels = layoutDataFactory.newLayoutSetModels(_groupModel.groupId)>

	<#list layoutSetModels as layoutSetModel>
		${dataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(layoutDataFactory.newLayoutFriendlyURLModel(_layoutModel))}
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
	<#local mbThreadModel = dataFactory.newMBThreadModel(_mbThreadId, _groupId, _mbRootMessageId)>

	${dataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = dataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage
		_mbMessageAssetCategoryModelsMaps=_mbDiscussionAssetCategoryModelsMaps
		_mbMessageAssetTagModelsMaps=_mbDiscussionAssetTagModelsMaps
		_mbMessageModel=mbRootMessageModel
	/>

	<#local mbMessageModels = dataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage
			_mbMessageAssetCategoryModelsMaps=_mbDiscussionAssetCategoryModelsMaps
			_mbMessageAssetTagModelsMaps=_mbDiscussionAssetTagModelsMaps
			_mbMessageModel=mbMessageModel
		/>

		${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel, classNameDataFactory.getClassNameId("com.liferay.wiki.model.WikiPage"), classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")))}
	</#list>

	${dataFactory.toInsertSQL(dataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId))}
</#macro>

<#macro insertMBMessage
	_mbMessageAssetCategoryModelsMaps
	_mbMessageAssetTagModelsMaps
	_mbMessageModel
>
	${dataFactory.toInsertSQL(_mbMessageModel)}

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
	${dataFactory.toInsertSQL(_userModel)}

	${dataFactory.toInsertSQL(dataFactory.newContactModel(_userModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.User")))}

	<#list _roleIds as roleId>
		${dataFactory.toInsertSQL("Users_Roles", 0, roleId, _userModel.userId)}
	</#list>

	<#list _groupIds as groupId>
		${dataFactory.toInsertSQL("Users_Groups", 0, groupId, _userModel.userId)}
	</#list>
</#macro>