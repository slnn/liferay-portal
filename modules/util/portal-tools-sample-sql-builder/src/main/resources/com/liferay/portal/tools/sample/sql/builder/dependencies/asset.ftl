<#assign
	assetClassNameIds = classNameDataFactory.assetClassNameIds
	assetVocabularyModelsArray = assetDataFactory.newAssetVocabularyModelsArray()
	assetCategoryModelsMaps = assetDataFactory.newAssetCategoryModelsMaps(assetVocabularyModelsArray, assetClassNameIds)
	assetTagModelsMaps = assetDataFactory.newAssetTagModelsMaps(assetClassNameIds)
/>

<#list assetDataFactory.newAssetVocabularyModels(assetDataFactory.newDefaultAssetVocabularyModel(), assetVocabularyModelsArray) as assetVocabularyModel>
	${insertSQLBuilder.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.newAssetCategoryModels(assetCategoryModelsMaps) as assetCategoryModel>
	${insertSQLBuilder.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.newAssetTagModels(assetTagModelsMaps) as assetTagModel>
	${insertSQLBuilder.toInsertSQL(assetTagModel)}
</#list>