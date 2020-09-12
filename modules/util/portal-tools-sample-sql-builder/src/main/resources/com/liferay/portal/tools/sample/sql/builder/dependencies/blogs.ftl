<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

<#list blogsEntryModels as blogsEntryModel>
	${dataFactory.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel, blogsEntryClassNameId) />

	${dataFactory.toInsertSQL(friendlyURLEntryModel)}

	${dataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${dataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${dataFactory.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel, classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion_com.liferay.blogs.model.BlogsEntry")))}

	<@insertAssetEntry
		_assetCategoryModelsMaps=assetCategoryModelsMaps
		_assetTagModelsMaps=assetTagModelsMaps
		_categoryAndTag=true
		_classNameIds=[blogsEntryClassNameId]
		_entry=blogsEntryModel
	/>

	<#assign mbRootMessageId = dataFactory.getCounterNext() />

	<@insertMBDiscussion
		_classNameId=blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=blogDataFactory.maxBlogsEntryCommentCount
		_mbDiscussionAssetCategoryModelsMaps=assetCategoryModelsMaps
		_mbDiscussionAssetTagModelsMaps=assetTagModelsMaps
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=dataFactory.getCounterNext()
	/>

	${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(blogsEntryModel, blogsEntryClassNameId))}

	${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(blogsEntryModel, blogsEntryClassNameId))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>