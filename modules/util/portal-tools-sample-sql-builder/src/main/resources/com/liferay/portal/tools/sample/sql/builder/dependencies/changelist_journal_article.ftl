<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
		journalFolderModels = cTDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
		folderAsstEntryModels = cTDataFactory.newAssetEntryModel(journalFolderModels)
		journalArticleResourceModels = cTDataFactory.newJournalArticleResourceModels(groupId, journalFolderModels)
		journalArticleModels = cTDataFactory.newJournalArticleModels(journalArticleResourceModels, journalFolderModels)
		journalArticleLocalizationModels = cTDataFactory.newJournalArticleLocalizationModels(journalArticleModels, journalFolderModels)
		dDMTemplateLinkModels = cTDataFactory.newDDMTemplateLinkModels(journalArticleModels, ddmTemplateModel.templateId)
		dDMStorageLinkModels = cTDataFactory.newDDMStorageLinkModels(journalArticleModels, ddmStructureModel.structureId)
		socialActivityModels = cTDataFactory.newSocialActivityModels(journalArticleModels)
		articleAssetEntryModels = cTDataFactory.newAssetEntryModel(journalArticleModels, journalArticleLocalizationModels)
		layoutModels = cTDataFactory.newLayoutModels(groupId, cTCollectionModel)
		cTEntryModels = cTDataFactory.newCTEntryModels(cTCollectionModel)
	/>

	<#list journalFolderModels as journalFolderModel>
		${dataFactory.toInsertSQL(journalFolderModel)}

		<#assign cTJournalArticleCounts = dataFactory.getSequence(dataFactory.maxCTJournalArticleCount) />
	</#list>

	<#list folderAsstEntryModels as folderAsstEntryModel>
		${dataFactory.toInsertSQL(folderAsstEntryModel)}
	</#list>

	<#list journalArticleResourceModels as journalArticleResourceModel>
		${dataFactory.toInsertSQL(journalArticleResourceModel)}
	</#list>

	<#list journalArticleModels as journalArticleModel>
		${dataFactory.toInsertSQL(journalArticleModel)}
	</#list>

	<#list journalArticleLocalizationModels as journalArticleLocalizationModel>
		${dataFactory.toInsertSQL(journalArticleLocalizationModel)}
	</#list>

	<#list dDMTemplateLinkModels as dDMTemplateLinkModel>
		${dataFactory.toInsertSQL(dDMTemplateLinkModel)}
	</#list>

	<#list dDMStorageLinkModels as dDMStorageLinkModel>
		${dataFactory.toInsertSQL(dDMStorageLinkModel)}
	</#list>

	<#list socialActivityModels as socialActivityModel>
		${dataFactory.toInsertSQL(socialActivityModel)}
	</#list>

	<#list articleAssetEntryModels as articleAssetEntryModel>
		${dataFactory.toInsertSQL(articleAssetEntryModel)}
	</#list>

	<#list layoutModels as layoutModel>
		${dataFactory.toInsertSQL(layoutModel)}
	</#list>

	<#list cTEntryModels as cTEntryModel>
		${dataFactory.toInsertSQL(cTEntryModel)}
	</#list>
</#list>

${dataFactory.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}