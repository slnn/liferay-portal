<#-- Default user -->

${dataFactory.setDefaultUserId()}

<#-- Sample user -->

<#assign
	sampleUserModel = dataFactory.newSampleUserModel()

	userGroupModel = dataFactory.newGroupModel(sampleUserModel)

	layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
/>

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=userGroupModel />

<#assign
	groupIds = dataFactory.getSampleUserGroupIds()
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.userRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=sampleUserModel
/>

<#list groupIds as groupId>
	${dataFactory.toInsertSQL(dataFactory.newBlogsStatsUserModel(groupId))}
</#list>