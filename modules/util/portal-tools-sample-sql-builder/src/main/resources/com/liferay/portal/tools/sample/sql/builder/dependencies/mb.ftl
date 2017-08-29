<#assign mbCategoryModels = dataFactory.newMBCategoryModels(groupId) />

<#list mbCategoryModels as mbCategoryModel>
	${dataFactory.toInsertSQL(mbCategoryModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(mbCategoryModel)}

	${dataFactory.toInsertSQL(dataFactory.newMBMailingListModel(mbCategoryModel))}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newMBMailingListModel(mbCategoryModel))}

	<#assign mbThreadModels = dataFactory.newMBThreadModels(mbCategoryModel) />

	<#list mbThreadModels as mbThreadModel>
		${dataFactory.toInsertSQL(mbThreadModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(mbThreadModel)}

		${dataFactory.toInsertSQL(dataFactory.newSubscriptionModel(mbThreadModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newSubscriptionModel(mbThreadModel))}

		<@insertAssetEntry _entry=mbThreadModel />

		${dataFactory.toInsertSQL(dataFactory.newMBThreadFlagModel(mbThreadModel))}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newMBThreadFlagModel(mbThreadModel))}

		<#assign mbMessageModels = dataFactory.newMBMessageModels(mbThreadModel) />

		<#list mbMessageModels as mbMessageModel>
			<@insertMBMessage _mbMessageModel=mbMessageModel />

			${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(mbMessageModel))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newSocialActivityModel(mbMessageModel))}
		</#list>

		${initRuntimeContext.getCSVWriter("messageBoard").write(mbCategoryModel.categoryId + "," + mbThreadModel.threadId + "," + mbThreadModel.rootMessageId + "\n")}
	</#list>
</#list>