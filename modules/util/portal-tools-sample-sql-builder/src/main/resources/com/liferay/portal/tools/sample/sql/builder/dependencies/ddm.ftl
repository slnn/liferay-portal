<#assign defaultJournalDDMStructureModel = dataFactory.newDefaultJournalDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureModel=defaultJournalDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultJournalDDMStructureVersionModel(defaultJournalDDMStructureModel)
/>

<#assign defaultDLDDMStructureModel = dataFactory.newDefaultDLDDMStructureModel() />

<@insertDDMStructure
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>