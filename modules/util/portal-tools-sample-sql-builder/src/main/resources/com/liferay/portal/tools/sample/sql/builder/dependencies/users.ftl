<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupId, guestGroupId)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.userRoleModel.roleId]
/>

<#list dataFactory.newUserModels() as userModel>
	<#assign userGroupModel = dataFactory.newGroupModel(userModel) />

	${csvFileWriter.write("user", companyModelList[1] + "," + userModel.screenName + "\n")}

	<@insertLayout _layoutModel=dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>