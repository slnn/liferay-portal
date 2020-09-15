<#assign cTCollectionModels = dataFactory.newCTCollectionModels(userModel) />

${dataFactory.toInsertSQL(dataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${dataFactory.toInsertSQL(cTCollectionModel)}

	<#assign
			journalFolderModels = dataFactory.newJournalFolderModels(cTCollectionModel, groupId)
			folderAsstEntryModels = dataFactory.newAssetEntryModels(journalFolderModels)
			journalArticleResourceModels = dataFactory.newJournalArticleResourceModels(groupId, journalFolderModels)
			journalArticleModels = dataFactory.newJournalArticleModels(journalArticleResourceModels, journalFolderModels)
			journalArticleLocalizationModels = dataFactory.newJournalArticleLocalizationModels(journalArticleModels, journalFolderModels)
			dDMTemplateLinkModels = dataFactory.newDDMTemplateLinkModels(journalArticleModels, defaultJournalDDMTemplateModel.templateId)
			dDMStorageLinkModels = dataFactory.newDDMStorageLinkModels(journalArticleModels, defaultJournalDDMStructureModel.structureId)
			socialActivityModels = dataFactory.newSocialActivityModels(journalArticleModels)
			articleAssetEntryModels = dataFactory.newAssetEntryModels(journalArticleModels, journalArticleLocalizationModels)
			layoutModels = dataFactory.newLayoutModels(groupId, cTCollectionModel)
			layoutFriendlyURLModels = dataFactory.newLayoutFriendlyURLModels(layoutModels)
			layoutAssetEntryModels = dataFactory.newLayoutAssetEntryModels(layoutModels)
			journalPagePortletPreferencesModels = dataFactory.newJournalPortletPreferencesModels(layoutModels)
			journalArticlePortletPreferencesModels = dataFactory.newJournalArticlePortletPreferencesModels(layoutModels, journalArticleResourceModels)
			journalContentSearchModels = dataFactory.newJournalContentSearchModels(journalArticleModels, layoutModels)
			cTEntryModels = dataFactory.newCTEntryModels(cTCollectionModel)
		/>

		<#list journalFolderModels as journalFolderModel>
			${dataFactory.toInsertSQL(journalFolderModel)}
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
			${csvFileWriter.write("cTLayout", userModel.screenName + "," + layoutModel.friendlyURL + "," + layoutModel.ctCollectionId + "\n")}
		</#list>

		<#list layoutFriendlyURLModels as layoutFriendlyURLModel>
			${dataFactory.toInsertSQL(layoutFriendlyURLModel)}
		</#list>

		<#list layoutAssetEntryModels as layoutAssetEntryModel>
			${dataFactory.toInsertSQL(layoutAssetEntryModel)}
		</#list>

		<#list journalPagePortletPreferencesModels as journalPagePortletPreferencesModel>
			${dataFactory.toInsertSQL(journalPagePortletPreferencesModel)}
		</#list>

		<#list journalArticlePortletPreferencesModels as journalArticlePortletPreferencesModel>
			${dataFactory.toInsertSQL(journalArticlePortletPreferencesModel)}
		</#list>

		<#list journalContentSearchModels as journalContentSearchModel>
			${dataFactory.toInsertSQL(journalContentSearchModel)}
		</#list>

		<#list cTEntryModels as cTEntryModel>
			${dataFactory.toInsertSQL(cTEntryModel)}
		</#list>
</#list>