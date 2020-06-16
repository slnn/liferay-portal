<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]

	userModels = dataFactory.newUserModels()
/>

${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel())}

<#list userModels as userModel>
	<#assign
		userGroupModel = dataFactory.newGroupModel(userModel)

		layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")

		cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel)
	/>

	<#list cTCollectionModels as cTCollectionModel>
		${dataFactory.toInsertSQL(cTCollectionModel)}

		<#assign journalFolderModels = cTDataFactory.newJournalFolderModels(cTCollectionModel, groupId) />

		<#list journalFolderModels as journalFolderModel>
			${dataFactory.toInsertSQL(journalFolderModel)}
			${dataFactory.toInsertSQL(cTDataFactory.newAssetEntryModel(journalFolderModel, cTCollectionModel))}
		</#list>
	</#list>

	${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

	<@insertLayout _layoutModel=layoutModel />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>