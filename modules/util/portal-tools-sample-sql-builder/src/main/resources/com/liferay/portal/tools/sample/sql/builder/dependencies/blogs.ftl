<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

<#list blogsEntryModels as blogsEntryModel>
	${blogDataFactory.toInsertSQL(blogsEntryModel)}
	${resourcePermissionDataFactory.generateResourcePermissionSQL(blogsEntryModel)}
	${blogDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel))}

	<@insertAssetEntry
		_categoryAndTag=true
		_entry=blogsEntryModel
	/>

	<#assign
		mbThreadId = counterDataFactory.getCounterNext()
		mbRootMessageId = counterDataFactory.getCounterNext()
	/>

	<@insertMBDiscussion
		_classNameId=blogDataFactory.blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=initContext.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=mbThreadId
	/>

	${blogDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel))}

	${blogDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel))}

	${initContext.getCSVWriter("blog").write(blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbThreadId + "," + mbRootMessageId + "\n")}
</#list>