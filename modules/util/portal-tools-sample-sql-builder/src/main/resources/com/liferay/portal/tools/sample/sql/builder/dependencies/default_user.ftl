<#-- Default user -->

<@insertUser _userModel=dataFactory.newDefaultUserModel(companyModel) />

<#-- Guest user -->

<#assign guestUserModel = dataFactory.newGuestUserModel(companyModel) />

<@insertGroup _groupModel=dataFactory.newGroupModel(guestUserModel, companyModel) />

<#assign
	groupIds = [guestGroupModel.groupId]
	roleIds = [dataFactory.administratorRoleModel.roleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=guestUserModel
/>

<#-- Sample user -->

<#assign
	sampleUserModel = dataFactory.newSampleUserModel(companyModel)

	userGroupModel = dataFactory.newGroupModel(sampleUserModel, companyModel)

	layoutModel = dataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "", companyModel)
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
	${dataFactory.toInsertSQL(dataFactory.newBlogsStatsUserModel(groupId, companyModel))}

	${dataFactory.toInsertSQL(dataFactory.newMBStatsUserModel(groupId))}
</#list>