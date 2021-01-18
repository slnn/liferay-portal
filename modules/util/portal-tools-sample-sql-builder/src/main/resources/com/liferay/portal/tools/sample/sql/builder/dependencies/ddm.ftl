<#assign defaultJournalDDMStructureModel = dataFactory.newDefaultJournalDDMStructureModel(companyModel) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.newDefaultJournalDDMStructureLayoutModel(companyModel)
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

<#assign defaultJournalDDMTemplateModel = dataFactory.newDefaultJournalDDMTemplateModel(companyModel) />

${dataFactory.toInsertSQL(defaultJournalDDMTemplateModel)}

${dataFactory.toInsertSQL(dataFactory.newDefaultJournalDDMTemplateVersionModel(companyModel))}