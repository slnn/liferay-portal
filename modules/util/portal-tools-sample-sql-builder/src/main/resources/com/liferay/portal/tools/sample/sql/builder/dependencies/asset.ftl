<#list assetDataFactory.assetVocabularyModels as assetVocabularyModel>
	${assetDataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	${assetDataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${assetDataFactory.toInsertSQL(assetTagModel)}
</#list>

<#list assetDataFactory.assetTagStatsModels as assetTagStatsModel>
	${assetDataFactory.toInsertSQL(assetTagStatsModel)}
</#list>