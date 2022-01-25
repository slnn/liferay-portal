<#assign defaultJournalDDMStructureModel = dataFactory.newDefaultJournalDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

${dataFactory.toInsertSQL(dataFactory.newDefaultJournalDDMTemplateVersionModel())}

<#assign defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>