<#list dataFactory.newSegmentsEntries(guestGroupModel.groupId, companyModel, sampleUserModel) as segmentEntry>
	${dataFactory.toInsertSQL(segmentEntry)}

	<#list dataFactory.newResourcePermissionModels(segmentEntry, dataFactory.guestRoleModel) as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>

	${csvFileWriter.write("segments", segmentEntry.segmentsEntryId + ", "+ segmentEntry.name + "\n")}
</#list>