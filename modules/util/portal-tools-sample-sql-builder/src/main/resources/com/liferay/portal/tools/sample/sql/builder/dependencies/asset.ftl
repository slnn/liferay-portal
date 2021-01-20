<#assign defaultAssetVocabularyModel = dataFactory.newDefaultAssetVocabularyModel(companyModel, defaultUserModel) />

${dataFactory.toInsertSQL(defaultAssetVocabularyModel)}

<#list dataFactory.newResourcePermissionModels(defaultAssetVocabularyModel, defaultUserModel) as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>