<#assign mbCategoryModels = dataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${resourcePermissionDataFactory.toInsertSQL(mbCategoryModel)}

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newMBMailingListModel(mbCategoryModel))}

	<#assign mbThreadModels = dataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${resourcePermissionDataFactory.toInsertSQL(mbThreadModel)}

		${resourcePermissionDataFactory.toInsertSQL(dataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${resourcePermissionDataFactory.toInsertSQL(dataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = dataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${resourcePermissionDataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${initRuntimeContext.getCSVWriter("messageBoard").write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>