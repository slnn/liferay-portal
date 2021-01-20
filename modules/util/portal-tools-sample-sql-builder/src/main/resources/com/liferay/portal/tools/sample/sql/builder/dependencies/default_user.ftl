<#-- Default user -->

<#assign defaultUserModel = dataFactory.newDefaultUserModel(companyModel) />

<@insertUser _userModel=defaultUserModel />

<#-- Guest user -->

<#assign guestUserModel = dataFactory.newGuestUserModel(companyModel) />

<@insertGroup _groupModel=dataFactory.newGroupModel(guestUserModel, companyModel, sampleUserModel) />

<#assign
	groupIds = [guestGroupModel.groupId]
	roleIds = [dataFactory.administratorRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=guestUserModel
/>

<#assign
	userGroupModel = dataFactory.newGroupModel(sampleUserModel, companyModel, sampleUserModel)

	layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "", companyModel, sampleUserModel)
/>

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=userGroupModel />

<#assign
	groupIds = dataFactory.getSequence(dataFactory.maxGroupCount)
	roleIds = [dataFactory.administratorRoleModel.roleId, dataFactory.powerUserRoleModel.roleId, dataFactory.userRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=sampleUserModel
/>

<#list groupIds as groupId>
	${dataFactory.toInsertSQL(dataFactory.newBlogsStatsUserModel(groupId, companyModel, sampleUserModel))}

	${dataFactory.toInsertSQL(dataFactory.newMBStatsUserModel(groupId, sampleUserModel))}
</#list>