<#setting number_format = "computer">

<#macro insertAssetEntry
	_entry
	_assetCategoryModels = {}
	_assetTagModels = {}
>
	<#local assetEntryModel = dataFactory.newAssetEntryModel(_entry)>

	${dataFactory.toInsertSQL(assetEntryModel)}

	<#if (_assetCategoryModels?size > 0)>
		<#local assetCategoryIds = dataFactory.getAssetCategoryIds(assetEntryModel, _assetCategoryModels)>

		<#list assetCategoryIds as assetCategoryId>
			<#local assetEntryAssetCategoryRelId = dataFactory.getCounterNext()>

			insert into AssetEntryAssetCategoryRel values (0, 0, ${assetEntryAssetCategoryRelId}, ${assetEntryModel.companyId}, ${assetEntryModel.entryId}, ${assetCategoryId}, 0);
		</#list>
	</#if>

	<#if (_assetTagModels?size > 0)>
		<#local assetTagIds = dataFactory.getAssetTagIds(assetEntryModel, _assetTagModels)>

		<#list assetTagIds as assetTagId>
			${dataFactory.toInsertSQL("AssetEntries_AssetTags", assetEntryModel.companyId, assetEntryModel.entryId, assetTagId)}
		</#list>
	</#if>
</#macro>

<#macro insertContentLayout
	_layoutModel
	_fragmentEntryModel
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(_layoutModel))}

	<#local fragmentEntryLinkModel = dataFactory.newFragmentEntryLinkModel(_layoutModel, _fragmentEntryModel)>

	${dataFactory.toInsertSQL(fragmentEntryLinkModel)}

	<#local journalContentPortletPreferencesModel = dataFactory.newJournalContentPortletPreferencesModel(fragmentEntryLinkModel)>

	${dataFactory.toInsertSQL(journalContentPortletPreferencesModel)}

	${dataFactory.toInsertSQL(dataFactory.newJournalContentPortletPreferenceValueModel(journalContentPortletPreferencesModel))}

	<#local layoutPageTemplateStructureModel = dataFactory.newLayoutPageTemplateStructureModel(_layoutModel)>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureModel)}

	<#local layoutPageTemplateStructureRelModel = dataFactory.newLayoutPageTemplateStructureRelModel(_layoutModel, layoutPageTemplateStructureModel, fragmentEntryLinkModel)>

	${dataFactory.toInsertSQL(layoutPageTemplateStructureRelModel)}
</#macro>

<#macro insertDDMContent
	_ddmStorageLinkId
	_ddmStructureId
	_entry
	_ddmStructureVersionId = 0
	_currentIndex = -1
>
	<#if _currentIndex = -1>
		<#local ddmStorageLinkModel = dataFactory.newDDMStorageLinkModel(_entry, _ddmStorageLinkId, _ddmStructureId, _ddmStructureVersionId)>

		<#local ddmFieldModels = dataFactory.newDDMFieldModels(_entry, ddmStorageLinkModel, _ddmStructureVersionId)>

		<#local ddmFieldAttributeModels = dataFactory.newDDMFieldAttributeModels(_entry, ddmFieldModels, ddmStorageLinkModel)>
	<#else>
		<#local ddmStorageLinkModel = dataFactory.newDDMStorageLinkModel(_entry, _ddmStorageLinkId, _ddmStructureId, _ddmStructureVersionId)>

		<#local ddmFieldModels = dataFactory.newDDMFieldModels(_currentIndex, _entry, ddmStorageLinkModel)>

		<#local ddmFieldAttributeModels = dataFactory.newDDMFieldAttributeModels(_currentIndex, _entry, ddmFieldModels, ddmStorageLinkModel)>
	</#if>

	<#list ddmFieldModels as ddmFieldModel>
		${dataFactory.toInsertSQL(ddmFieldModel)}
	</#list>

	<#list ddmFieldAttributeModels as ddmFieldAttributeModel>
		${dataFactory.toInsertSQL(ddmFieldAttributeModel)}
	</#list>

	${dataFactory.toInsertSQL(ddmStorageLinkModel)}
</#macro>

<#macro insertDDMStructure
	_ddmStructureModel
	_ddmStructureLayoutModel
	_ddmStructureVersionModel
>
	${dataFactory.toInsertSQL(_ddmStructureModel)}

	${dataFactory.toInsertSQL(_ddmStructureLayoutModel)}

	${dataFactory.toInsertSQL(_ddmStructureVersionModel)}
</#macro>

<#macro insertDLFolder
	_ddmStructureId
	_ddmStructureVersionId
	_dlFolderDepth
	_groupId
	_parentDLFolderId
>
	<#if _dlFolderDepth <= dataFactory.maxDLFolderDepth>
		<#local dlFolderModels = dataFactory.newDLFolderModels(_groupId, _parentDLFolderId, companyModel, sampleUserModel)>

		<#list dlFolderModels as dlFolderModel>
			${dataFactory.toInsertSQL(dlFolderModel)}

			<@insertAssetEntry _entry=dlFolderModel />

			<#local dlFileEntryModels = dataFactory.newDlFileEntryModels(dlFolderModel)>

			<#list dlFileEntryModels as dlFileEntryModel>
				${dataFactory.toInsertSQL(dlFileEntryModel)}

				<#local dlFileVersionModel = dataFactory.newDLFileVersionModel(dlFileEntryModel)>

				${dataFactory.toInsertSQL(dlFileVersionModel)}

				<@insertAssetEntry _entry=dlFileEntryModel />

				<#local ddmStorageLinkId = dataFactory.getCounterNext()>

				<@insertDDMContent
					_ddmStorageLinkId=ddmStorageLinkId
					_ddmStructureId=_ddmStructureId
					_ddmStructureVersionId=_ddmStructureVersionId
					_entry=dlFileEntryModel
				/>

				<@insertMBDiscussion
					_classNameId=dataFactory.DLFileEntryClassNameId
					_classPK=dlFileEntryModel.fileEntryId
					_groupId=dlFileEntryModel.groupId
					_maxCommentCount=0
					_mbRootMessageId=dataFactory.getCounterNext()
					_mbThreadId=dataFactory.getCounterNext()
				/>

				${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(dlFileEntryModel))}

				<#local dlFileEntryMetadataModel = dataFactory.newDLFileEntryMetadataModel(ddmStorageLinkId, _ddmStructureId, dlFileVersionModel)>

				${dataFactory.toInsertSQL(dlFileEntryMetadataModel)}

				${dataFactory.toInsertSQL(dataFactory.newDDMStructureLinkModel(dlFileEntryMetadataModel))}

				${csvFileWriter.write("documentLibrary", dlFileEntryModel.uuid + "," + dlFolderModel.folderId + "," + dlFileEntryModel.name + "," + dlFileEntryModel.fileEntryId + "\n")}
			</#list>

			<@insertDLFolder
				_ddmStructureId=_ddmStructureId
				_ddmStructureVersionId=_ddmStructureVersionId
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

	<#local resourcePermissionModels = dataFactory.newResourcePermissionModels(_groupModel, dataFactory.ownerRoleModel)>

	<#list resourcePermissionModels as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>

	<#local layoutSetModels = dataFactory.newLayoutSetModels(_groupModel)>

	<#list layoutSetModels as layoutSetModel>
		${dataFactory.toInsertSQL(layoutSetModel)}
	</#list>
</#macro>

<#macro insertLayout
	_layoutModel
>
	${dataFactory.toInsertSQL(_layoutModel)}

	${dataFactory.toInsertSQL(dataFactory.newLayoutFriendlyURLModel(_layoutModel))}
</#macro>

<#macro insertMBDiscussion
	_classNameId
	_classPK
	_groupId
	_maxCommentCount
	_mbRootMessageId
	_mbThreadId
>
	<#local mbThreadModel = dataFactory.newMBThreadModel(_mbThreadId, _groupId, _mbRootMessageId, companyModel, sampleUserModel)>

	${dataFactory.toInsertSQL(mbThreadModel)}

	<#local mbRootMessageModel = dataFactory.newMBMessageModel(mbThreadModel, _classNameId, _classPK, 0)>

	<@insertMBMessage _mbMessageModel=mbRootMessageModel />

	<#local mbMessageModels = dataFactory.newMBMessageModels(mbThreadModel, _classNameId, _classPK, _maxCommentCount)>

	<#list mbMessageModels as mbMessageModel>
		<@insertMBMessage _mbMessageModel=mbMessageModel />

		${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel))}
	</#list>

	${dataFactory.toInsertSQL(dataFactory.newMBDiscussionModel(_groupId, _classNameId, _classPK, _mbThreadId, companyModel, sampleUserModel))}
</#macro>

<#macro insertMBMessage
	_mbMessageModel
>
	${dataFactory.toInsertSQL(_mbMessageModel)}

	<@insertAssetEntry _entry=_mbMessageModel />
</#macro>

<#macro insertUser
	_userModel
	_groupIds = []
	_roleIds = []
>
	${dataFactory.toInsertSQL(_userModel)}

	<#local resourcePermissionModels = dataFactory.newResourcePermissionModels(_userModel, dataFactory.ownerRoleModel)>

	<#list resourcePermissionModels as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>

	${dataFactory.toInsertSQL(dataFactory.newContactModel(_userModel, companyModel))}

	<#list _roleIds as roleId>
		${dataFactory.toInsertSQL("Users_Roles", 0, roleId, _userModel.userId)}
	</#list>

	<#list _groupIds as groupId>
		${dataFactory.toInsertSQL("Users_Groups", 0, groupId, _userModel.userId)}
	</#list>
</#macro>