${dataFactory.toInsertSQL(dlDataFactory.newDLFileEntryTypeModel())}

<#assign defaultDLDDMStructureModel = dlDataFactory.newDefaultDLDDMStructureModel(classNameDataFactory.getClassNameId("com.liferay.document.library.kernel.model.DLFileEntry")) />

<@insertDDMStructure
	_ddmStructureLayoutModel=dlDataFactory.newDefaultDLDDMStructureLayoutModel()
	_ddmStructureModel=defaultDLDDMStructureModel
	_ddmStructureVersionModel=dlDataFactory.newDefaultDLDDMStructureVersionModel(defaultDLDDMStructureModel)
/>