<#assign
	mbCategoryModels = messageBoardDataFactory.newMBCategoryModels(groupId)
	mbThreadClassNameId = classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBThread")
/>

<#list mbCategoryModels as mbCategoryModel>
	${insertSQLBuilder.toInsertSQL(mbCategoryModel)}
	${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBMailingListModel(mbCategoryModel, sampleUserModel))}

	${csvFileWriter.write("mbCategory", mbCategoryModel.categoryId + "," + mbCategoryModel.name + "\n")}

	<#assign mbThreadModels = messageBoardDataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${insertSQLBuilder.toInsertSQL(mbThreadModel)}

		${insertSQLBuilder.toInsertSQL(subscriptionDataFactory.newSubscriptionModel(mbThreadModel, mbThreadClassNameId))}

		<@insertAssetEntry
			_assetCategoryModelsMaps=assetCategoryModelsMaps
			_assetTagModelsMaps=assetTagModelsMaps
			_classNameIds=[mbThreadClassNameId]
			_entry=mbThreadModel
		/>

		${insertSQLBuilder.toInsertSQL(messageBoardDataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = messageBoardDataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage
				_mbMessageAssetCategoryModelsMaps=assetCategoryModelsMaps
				_mbMessageAssetTagModelsMaps=assetTagModelsMaps
				_mbMessageModel=mbMessageModel
			/>

			${insertSQLBuilder.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(mbMessageModel, wikiPageClassNameId, classNameDataFactory.getClassNameId("com.liferay.message.boards.model.MBMessage")))}
		</#list>

		${csvFileWriter.write("mbThread", mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>