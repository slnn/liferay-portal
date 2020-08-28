<#-- Default user -->

<@insertUser _userModel=userDataFactory.newDefaultUserModel() />

<#-- Guest user -->

<#assign guestUserModel = userDataFactory.newGuestUserModel() />

<@insertGroup _groupModel=userDataFactory.newGroupModel(guestUserModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.User")) />

<#assign
	groupIds = [guestGroupModel.groupId]
	roleIds = [userDataFactory.administratorRoleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=guestUserModel
/>

<#-- Sample user -->

<#assign
	sampleUserModel = userDataFactory.newSampleUserModel()

	userGroupModel = userDataFactory.newGroupModel(sampleUserModel, classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.User"))

	layoutModel = layoutDataFactory.newLayoutModel(userGroupModel.groupId, "home", "", "")
/>

<@insertLayout _layoutModel=layoutModel />

<@insertGroup _groupModel=userGroupModel />

<#assign
	groupIds = counterDataFactory.getSequence(userDataFactory.maxGroupCount)
	roleIds = [userDataFactory.administratorRoleId, userDataFactory.powerUserRoleId, userDataFactory.userRoleId]
/>

<@insertUser
	_groupIds=groupIds
	_roleIds=roleIds
	_userModel=sampleUserModel
/>

<#list groupIds as groupId>
	${insertSQLBuilder.toInsertSQL(blogDataFactory.newBlogsStatsUserModel(groupId))}

	${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBStatsUserModel(groupId))}
</#list>