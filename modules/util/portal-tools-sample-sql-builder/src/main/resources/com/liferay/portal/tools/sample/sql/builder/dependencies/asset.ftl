<#assign
	assetClassNameIds = classNameDataFactory.assetClassNameIds
	assetVocabularyModelsArray = assetDataFactory.newAssetVocabularyModelsArray()
	assetCategoryModelsMaps = assetDataFactory.newAssetCategoryModelsMaps(assetVocabularyModelsArray, assetClassNameIds)
	assetTagModelsMaps = assetDataFactory.newAssetTagModelsMaps(assetClassNameIds)
/>

<#list assetDataFactory.newAssetVocabularyModels(assetDataFactory.newDefaultAssetVocabularyModel(), assetVocabularyModelsArray) as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.newAssetCategoryModels(assetCategoryModelsMaps) as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.newAssetTagModels(assetTagModelsMaps) as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>