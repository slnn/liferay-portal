<#list assetDataFactory.assetVocabularyModels as assetVocabularyModel>
	${initContext.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	${initContext.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${initContext.toInsertSQL(assetTagModel)}
</#list>

<#list assetDataFactory.assetTagStatsModels as assetTagStatsModel>
	${initContext.toInsertSQL(assetTagStatsModel)}
</#list>