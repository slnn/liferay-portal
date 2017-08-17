<#assign blogsEntryModels = dataFactory.newBlogsEntryModels(groupId) />

<#list blogsEntryModels as blogsEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(blogsEntryModel)}

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newFriendlyURLEntryModel(blogsEntryModel))}

	<@insertAssetEntry
		_categoryAndTag=true
		_entry=blogsEntryModel
	/>

	<#assign
		mbThreadId = dataFactory.getCounterNext()
		mbRootMessageId = dataFactory.getCounterNext()
	/>

	<@insertMBDiscussion
		_classNameId=dataFactory.blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=initPropertiesContext.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=mbThreadId
	/>

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newSubscriptionModel(blogsEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newSocialActivityModel(blogsEntryModel))}

	${initRuntimeContext.getCSVWriter("blog").write(blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbThreadId + "," + mbRootMessageId + "\n")}
</#list>