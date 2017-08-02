<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

<#list blogsEntryModels as blogsEntryModel>
	${blogDataFactory.toInsertSQL(blogsEntryModel)}

	${blogDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel))}

	<@insertAssetEntry
		_categoryAndTag=true
		_entry=blogsEntryModel
	/>

	<#assign
		mbThreadId = dataFactory.getCounterNext()
		mbRootMessageId = dataFactory.getCounterNext()
	/>

	<@insertMBDiscussion
		_classNameId=blogDataFactory.blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=initContext.maxBlogsEntryCommentCount
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=mbThreadId
	/>

	${blogDataFactory.toInsertSQL(dataFactory.newSubscriptionModel(blogsEntryModel))}

	${blogDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel))}

	${initContext.getCSVWriter("blog").write(blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbThreadId + "," + mbRootMessageId + "\n")}
</#list>