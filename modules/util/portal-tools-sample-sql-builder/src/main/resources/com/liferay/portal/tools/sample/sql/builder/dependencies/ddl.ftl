<#assign ddlRecordSetCounts = dataFactory.getSequence(initPropertiesContext.maxDDLRecordSetCount) />

<#list ddlRecordSetCounts as ddlRecordSetCount>
	<#if ddlRecordSetCount = 1>
		<#assign
			ddmStructureModel = dataFactory.newDDLDDMStructureModel(groupId)
			ddmStructureVersionModel = dataFactory.newDDMStructureVersionModel(ddmStructureModel)
		/>

		<@insertDDMStructure
			_ddmStructureLayoutModel=dataFactory.newDDLDDMStructureLayoutModel(groupId, ddmStructureVersionModel)
			_ddmStructureModel=ddmStructureModel
			_ddmStructureVersionModel=ddmStructureVersionModel
		/>
	</#if>

	<#assign
		layoutName = "dynamic_data_list_display_" + ddlRecordSetCount
		portletId = "com_liferay_dynamic_data_lists_web_portlet_DDLDisplayPortlet_INSTANCE_TEST" + ddlRecordSetCount

		layoutModel = dataFactory.newLayoutModel(groupId, layoutName, "", portletId)
	/>

	<@insertLayout _layoutModel=layoutModel />

	<#assign ddlRecordSetModel = dataFactory.newDDLRecordSetModel(ddmStructureModel, ddlRecordSetCount) />

	${resourcePermissionDataFactory.toInsertSQL(ddlRecordSetModel)}

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newDDMStructureLinkModel(ddlRecordSetModel))}

	<#assign ddlRecordCounts = dataFactory.getSequence(initPropertiesContext.maxDDLRecordCount) />

	<#list ddlRecordCounts as ddlRecordCount>
		<#assign ddlRecordModel = dataFactory.newDDLRecordModel(ddlRecordSetModel) />

		${resourcePermissionDataFactory.toInsertSQL(ddlRecordModel)}

		${resourcePermissionDataFactory.toInsertSQL(dataFactory.newDDLRecordVersionModel(ddlRecordModel))}

		<@insertDDMContent
			_currentIndex=ddlRecordCount
			_ddmStorageLinkId=dataFactory.getCounterNext()
			_ddmStructureId=ddmStructureModel.structureId
			_entry=ddlRecordModel
		/>

		${initRuntimeContext.getCSVWriter("dynamicDataList").write(ddlRecordModel.groupId + "," + layoutName + "," + portletId + "," + ddlRecordSetModel.recordSetId + "," + ddlRecordModel.recordId + "\n")}
	</#list>

	${resourcePermissionDataFactory.toInsertSQL(dataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, ddlRecordSetModel))}

	<#assign portletPreferencesModels = dataFactory.newDDLPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${resourcePermissionDataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>
</#list>