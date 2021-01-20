<#assign
	groupIds = dataFactory.getNewUserGroupIds(groupModel.groupId, guestGroupModel)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]

	userModels = dataFactory.newUserModels(companyModel)
/>

<#list userModels as userModel>
	<#assign userGroupModel = dataFactory.newGroupModel(userModel, companyModel, sampleUserModel) />

	<@insertLayout _layoutModel=dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "", companyModel, sampleUserModel) />

	<@insertGroup _groupModel=userGroupModel />

	<@insertUser
		_groupIds=groupIds
		_roleIds=roleIds
		_userModel=userModel
	/>
</#list>