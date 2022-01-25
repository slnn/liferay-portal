<#-- Default user -->

${dataFactory.setDefaultUserId()}

<#-- Sample user -->

<#assign
	sampleUserModel = dataFactory.newSampleUserModel()

	userGroupModel = dataFactory.newGroupModel(sampleUserModel)
/>

<@insertLayout _layoutModel=dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "") />

<@insertGroup _groupModel=userGroupModel />

<@insertUser
	_groupIds=dataFactory.getSequence(dataFactory.maxGroupCount)
	_roleIds=[dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]
	_userModel=sampleUserModel
/>