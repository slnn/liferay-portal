<#assign blogsEntryModels = blogDataFactory.newBlogsEntryModels(groupId) />

<#list blogsEntryModels as blogsEntryModel>
	${resourcePermissionDataFactory.toInsertSQL(blogsEntryModel)}

	<#assign friendlyURLEntryModel = blogDataFactory.newFriendlyURLEntryModel(blogsEntryModel, blogsEntryClassNameId) />

	${resourcePermissionDataFactory.toInsertSQL(friendlyURLEntryModel)}

	${resourcePermissionDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryLocalizationModel(friendlyURLEntryModel, blogsEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(blogDataFactory.newFriendlyURLEntryMapping(friendlyURLEntryModel))}

	${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(blogsEntryModel, classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion_com.liferay.blogs.model.BlogsEntry")))}

	<@insertAssetEntry
		_assetCategoryModelsMaps=assetCategoryModelsMaps
		_assetTagModelsMaps=assetTagModelsMaps
		_categoryAndTag=true
		_classNameIds=[blogsEntryClassNameId]
		_entry=blogsEntryModel
	/>

	<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

	<@insertMBDiscussion
		_classNameId=blogsEntryClassNameId
		_classPK=blogsEntryModel.entryId
		_groupId=groupId
		_maxCommentCount=blogDataFactory.maxBlogsEntryCommentCount
		_mbDiscussionAssetCategoryModelsMaps=assetCategoryModelsMaps
		_mbDiscussionAssetTagModelsMaps=assetTagModelsMaps
		_mbRootMessageId=mbRootMessageId
		_mbThreadId=counterDataFactory.getCounterNext()
	/>

	${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(blogsEntryModel, blogsEntryClassNameId))}

	${resourcePermissionDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(blogsEntryModel, blogsEntryClassNameId))}

	${csvFileWriter.write("blog", blogsEntryModel.entryId + "," + blogsEntryModel.urlTitle + "," + mbRootMessageId + "\n")}
</#list>