<#assign
	assetClassNameIds = classNameDataFactory.AssetClassNameIds
	assetVocabularyModelsArray = assetDataFactory.newAssetVocabularyModelsArray()
	assetCategoryModelsMaps = assetDataFactory.newAssetCategoryModelsMaps(assetVocabularyModelsArray, assetClassNameIds)
	assetTagModelsMaps = assetDataFactory.newAssetTagModelsMaps(assetClassNameIds)
/>

<#list assetDataFactory.getAssetVocabularyModels(assetDataFactory.newDefaultAssetVocabularyModel(), assetVocabularyModelsArray) as assetVocabularyModel>
	${insertSQLBuilder.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.getAssetCategoryModels(assetCategoryModelsMaps) as assetCategoryModel>
	${insertSQLBuilder.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${insertSQLBuilder.toInsertSQL(assetTagModel)}
</#list>