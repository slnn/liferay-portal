<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${initContext.toInsertSQL(mbCategoryModel)}
	${initContext.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel))}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${initContext.toInsertSQL(mbThreadModel)}

		${initContext.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${initContext.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${initContext.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${initContext.getCSVWriter("messageBoard").write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>