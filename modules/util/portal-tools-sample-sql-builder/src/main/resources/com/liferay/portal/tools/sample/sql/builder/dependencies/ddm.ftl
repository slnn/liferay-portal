<#assign
	journalArticleClassNameId = classNameDataFactory.getClassNameId("com.liferay.journal.model.JournalArticle")
	ddmStructureClassNameId = classNameDataFactory.getClassNameId("com.liferay.dynamic.data.mapping.model.DDMStructure")
	defaultJournalDDMStructureModel = journalDataFactory.newDefaultJournalDDMStructureModel(journalArticleClassNameId)
/>

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.newDefaultJournalDDMStructureLayoutModel()
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=journalDataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

<#assign defaultJournalDDMTemplateModel = journalDataFactory.newDefaultJournalDDMTemplateModel(journalArticleClassNameId, ddmStructureClassNameId) />

${insertSQLBuilder.toInsertSQL(defaultJournalDDMTemplateModel, classNameDataFactory.getClassName(defaultJournalDDMTemplateModel))}

${insertSQLBuilder.toInsertSQL(journalDataFactory.newDefaultJournalDDMTemplateVersionModel(ddmStructureClassNameId))}