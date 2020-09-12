<#assign wikiNodeModels = wikiDataFactory.newWikiNodeModels(groupId) />

<#list wikiNodeModels as wikiNodeModel>
	${resourcePermissionDataFactory.toInsertSQL(wikiNodeModel)}

	<#assign wikiPageModels = wikiDataFactory.newWikiPageModels(wikiNodeModel) />

	<#list wikiPageModels as wikiPageModel>
		${resourcePermissionDataFactory.toInsertSQL(wikiPageModel)}

		${resourcePermissionDataFactory.toInsertSQL(assetDataFactory.newMBDiscussionAssetEntryModel(wikiPageModel, classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBDiscussion_com.liferay.wiki.model.WikiPage")))}

		${resourcePermissionDataFactory.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(wikiPageModel, wikiPageClassNameId))}

		${resourcePermissionDataFactory.toInsertSQL(wikiDataFactory.newWikiPageResourceModel(wikiPageModel))}

		<@insertAssetEntry
			_assetCategoryModelsMaps=assetCategoryModelsMaps
			_assetTagModelsMaps=assetTagModelsMaps
			_categoryAndTag=true
			_classNameIds=[wikiPageClassNameId]
			_entry=wikiPageModel
		/>

		<#assign mbRootMessageId = counterDataFactory.getCounterNext() />

		<@insertMBDiscussion
			_classNameId=wikiPageClassNameId
			_classPK=wikiPageModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=wikiDataFactory.maxWikiPageCommentCount
			_mbDiscussionAssetCategoryModelsMaps=assetCategoryModelsMaps
			_mbDiscussionAssetTagModelsMaps=assetTagModelsMaps
			_mbRootMessageId=mbRootMessageId
			_mbThreadId=counterDataFactory.getCounterNext()
		/>

		${csvFileWriter.write("wiki", wikiNodeModel.nodeId + "," + wikiNodeModel.name + "," + wikiPageModel.resourcePrimKey + "," + wikiPageModel.title + "," + mbRootMessageId + "\n")}
	</#list>
</#list>