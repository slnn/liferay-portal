<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]
/>

<#list dataFactory.newUserModels() as userModel>
	<#assign
		portalPreferencesModel = dataFactory.newPortalPreferencesModel(userModel.userId)
		userGroupModel = dataFactory.newGroupModel(userModel)
	/>

	${csvFileWriter.write("user", virtualHostModel.hostname + "," + userModel.screenName + "\n")}

	<#list dataFactory.newLayoutModels(userGroupModel.groupId, "home", "", "") as layoutModel>
		<@insertLayout _layoutModel = layoutModel />
	</#list>

	<@insertGroup _groupModel = userGroupModel />

	<@insertUser
		_groupIds = groupIds
		_roleIds = roleIds
		_userModel = userModel
	/>

	${dataFactory.toInsertSQL(portalPreferencesModel)}

	<#list dataFactory.newPortalPreferenceValueModels(portalPreferencesModel.portalPreferencesId) as portalPreferenceValueModel>
			${dataFactory.toInsertSQL(portalPreferenceValueModel)}
	</#list>
</#list>