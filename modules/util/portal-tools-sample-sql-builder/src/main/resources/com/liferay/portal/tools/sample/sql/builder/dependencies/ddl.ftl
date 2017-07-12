<#assign ddlRecordSetCounts = counterDataFactory.getSequence(initContext.maxDDLRecordSetCount) />

<#list ddlRecordSetCounts as ddlRecordSetCount>
	<#if ddlRecordSetCount = 1>
		<#assign
			ddmStructureModel = dDLDataFactory.newDDLDDMStructureModel(groupId)
			ddmStructureVersionModel = dDLDataFactory.newDDMStructureVersionModel(ddmStructureModel)
		/>

		<@insertDDMStructure
			_ddmStructureLayoutModel=dDLDataFactory.newDDLDDMStructureLayoutModel(groupId, ddmStructureVersionModel)
			_ddmStructureModel=ddmStructureModel
			_ddmStructureVersionModel=ddmStructureVersionModel
		/>
	</#if>

	<#assign
		layoutName = "dynamic_data_list_display_" + ddlRecordSetCount
		portletId = "com_liferay_dynamic_data_lists_web_portlet_DDLDisplayPortlet_INSTANCE_TEST" + ddlRecordSetCount

		layoutModel = layoutDataFactory.newLayoutModel(groupId, layoutName, "", portletId)
	/>

	<@insertLayout _layoutModel=layoutModel/>

	<#assign ddlRecordSetModel = dDLDataFactory.newDDLRecordSetModel(ddmStructureModel, ddlRecordSetCount) />

	${initContext.toInsertSQL(ddlRecordSetModel)}

	${initContext.toInsertSQL(dDLDataFactory.newDDMStructureLinkModel(ddlRecordSetModel))}

	<#assign ddlRecordCounts = counterDataFactory.getSequence(initContext.maxDDLRecordCount) />

	<#list ddlRecordCounts as ddlRecordCount>
		<#assign ddlRecordModel = dDLDataFactory.newDDLRecordModel(ddlRecordSetModel) />

		${initContext.toInsertSQL(ddlRecordModel)}

		${initContext.toInsertSQL(dDLDataFactory.newDDLRecordVersionModel(ddlRecordModel))}

		<@insertDDMContent
			_currentIndex=ddlRecordCount
			_ddmStorageLinkId=counterDataFactory.getCounterNext()
			_ddmStructureId=ddmStructureModel.structureId
			_entry=ddlRecordModel
		/>

		${initContext.getCSVWriter("dynamicDataList").write(ddlRecordModel.groupId + "," + layoutName + "," + portletId + "," + ddlRecordSetModel.recordSetId + "," + ddlRecordModel.recordId + "\n")}
	</#list>

	${initContext.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, ddlRecordSetModel))}

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newDDLPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${initContext.toInsertSQL(portletPreferencesModel)}
	</#list>
</#list>
