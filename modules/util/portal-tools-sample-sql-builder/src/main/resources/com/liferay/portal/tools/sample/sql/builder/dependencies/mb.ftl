<#assign mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${insertSQLBuilder.toInsertSQL(mbCategoryModel)}
	${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${insertSQLBuilder.toInsertSQL(mbThreadModel)}

		${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel, classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBThread")))}

		<@insertAssetEntry
			_assetCategoryModelsMaps=assetCategoryModelsMaps
			_assetTagModelsMaps=assetTagModelsMaps
			_classNameIds=[classNameDataFactory.getClassNameataFactory.getClassNameId("com.liferay.message.boards.model.MBThread")]
			_entry=mbThreadModel
		/>

		${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel, classNameDataFactory.getClassNameId("com.liferay.wiki.model.WikiPage"), classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>