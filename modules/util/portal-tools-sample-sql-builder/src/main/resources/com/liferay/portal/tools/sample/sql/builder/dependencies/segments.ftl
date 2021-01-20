<#list dataFactory.newSegmentsEntries(guestGroupModel.groupId, companyModel, sampleUserModel) as segmentEntry>
	${dataFactory.toInsertSQL(segmentEntry)}

	${csvFileWriter.write("segments", segmentEntry.segmentsEntryId + ", "+ segmentEntry.name + "\n")}
</#list>