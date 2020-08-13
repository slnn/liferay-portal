<#assign cTCollectionModels = cTDataFactory.newCTCollectionModels(userModel) />

${insertSQLBuilder.toInsertSQL(cTDataFactory.newCTPreferencesModel(cTCollectionModels))}

<#list cTCollectionModels as cTCollectionModel>
	${insertSQLBuilder.toInsertSQL(cTCollectionModel)}

	<#assign
			journalFolderModels = journalDataFactory.newJournalFolderModels(cTCollectionModel, groupId)
			folderAsstEntryModels = assetDataFactory.newAssetEntryModels(journalFolderModels)
			journalArticleResourceModels = journalDataFactory.newJournalArticleResourceModels(groupId, journalFolderModels)
			journalArticleModels = journalDataFactory.newJournalArticleModels(journalArticleResourceModels, journalFolderModels)
			journalArticleLocalizationModels = journalDataFactory.newJournalArticleLocalizationModels(journalArticleModels, journalFolderModels)
			dDMTemplateLinkModels = journalDataFactory.newDDMTemplateLinkModels(journalArticleModels, ddmTemplateModel.templateId)
			dDMStorageLinkModels = journalDataFactory.newDDMStorageLinkModels(journalArticleModels, ddmStructureModel.structureId)
			socialActivityModels = socialActivityDataFactory.newSocialActivityModels(journalArticleModels)
			articleAssetEntryModels = assetDataFactory.newAssetEntryModels(journalArticleModels, journalArticleLocalizationModels)
			layoutModels = layoutDataFactory.newLayoutModels(groupId, cTCollectionModel)
			layoutFriendlyURLModels = layoutDataFactory.newLayoutFriendlyURLModels(layoutModels)
			layoutAssetEntryModels = assetDataFactory.newLayoutAssetEntryModels(layoutModels)
			journalPagePortletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModels)
			journalArticlePortletPreferencesModels = portletPreferenceDataFactory.newJournalArticlePortletPreferencesModels(layoutModels, journalArticleResourceModels)
			journalContentSearchModels = journalDataFactory.newJournalContentSearchModels(journalArticleModels, layoutModels)
		/>

		<#list journalFolderModels as journalFolderModel>
			${insertSQLBuilder.toInsertSQL(journalFolderModel)}
		</#list>

		<#list folderAsstEntryModels as folderAsstEntryModel>
			${insertSQLBuilder.toInsertSQL(folderAsstEntryModel)}
		</#list>

		<#list journalArticleResourceModels as journalArticleResourceModel>
			${insertSQLBuilder.toInsertSQL(journalArticleResourceModel)}
		</#list>

		<#list journalArticleModels as journalArticleModel>
			${insertSQLBuilder.toInsertSQL(journalArticleModel)}
		</#list>

		<#list journalArticleLocalizationModels as journalArticleLocalizationModel>
			${insertSQLBuilder.toInsertSQL(journalArticleLocalizationModel)}
		</#list>

		<#list dDMTemplateLinkModels as dDMTemplateLinkModel>
			${insertSQLBuilder.toInsertSQL(dDMTemplateLinkModel)}
		</#list>

		<#list dDMStorageLinkModels as dDMStorageLinkModel>
			${insertSQLBuilder.toInsertSQL(dDMStorageLinkModel)}
		</#list>

		<#list socialActivityModels as socialActivityModel>
			${insertSQLBuilder.toInsertSQL(socialActivityModel)}
		</#list>

		<#list articleAssetEntryModels as articleAssetEntryModel>
			${insertSQLBuilder.toInsertSQL(articleAssetEntryModel)}
		</#list>

		<#list layoutModels as layoutModel>
			${insertSQLBuilder.toInsertSQL(layoutModel)}
		</#list>

		<#list layoutFriendlyURLModels as layoutFriendlyURLModel>
			${insertSQLBuilder.toInsertSQL(layoutFriendlyURLModel)}
		</#list>

		<#list layoutAssetEntryModels as layoutAssetEntryModel>
			${insertSQLBuilder.toInsertSQL(layoutAssetEntryModel)}
		</#list>

		<#list journalPagePortletPreferencesModels as journalPagePortletPreferencesModel>
			${insertSQLBuilder.toInsertSQL(journalPagePortletPreferencesModel)}
		</#list>

		<#list journalArticlePortletPreferencesModels as journalArticlePortletPreferencesModel>
			${insertSQLBuilder.toInsertSQL(journalArticlePortletPreferencesModel)}
		</#list>

		<#list journalContentSearchModels as journalContentSearchModel>
			${insertSQLBuilder.toInsertSQL(journalContentSearchModel)}
		</#list>
</#list>