<#list dataFactory.assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.assetCategoryModels as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetCategoryModel)}
</#list>

<#list dataFactory.assetTagModels as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(assetTagModel)}
</#list>

<#list dataFactory.assetTagStatsModels as assetTagStatsModel>
	${dataFactory.toInsertSQL(assetTagStatsModel)}
</#list>