<#assign
	assetClassNameIds = classNameDataFactory.AssetClassNameIds
/>

<#list assetDataFactory.getAssetVocabularyModels(assetDataFactory.newDefaultAssetVocabularyModel()) as assetVocabularyModel>
	${insertSQLBuilder.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetDataFactory.assetCategoryModels as assetCategoryModel>
	${insertSQLBuilder.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetDataFactory.assetTagModels as assetTagModel>
	${insertSQLBuilder.toInsertSQL(assetTagModel)}
</#list>