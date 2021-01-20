<#assign defaultAssetVocabularyModel = dataFactory.newDefaultAssetVocabularyModel(companyModel, defaultUserModel, globalGroupModel) />

${dataFactory.toInsertSQL(defaultAssetVocabularyModel)}

<#list dataFactory.newResourcePermissionModels(defaultAssetVocabularyModel, defaultUserModel) as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}
</#list>