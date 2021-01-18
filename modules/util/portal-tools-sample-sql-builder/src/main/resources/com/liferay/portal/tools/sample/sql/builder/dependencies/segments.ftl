<#list dataFactory.newSegmentsEntries(guestGroupModel.groupId, companyModel) as segmentEntry>
	${dataFactory.toInsertSQL(segmentEntry)}

	${csvFileWriter.write("segments", segmentEntry.segmentsEntryId + ", "+ segmentEntry.name + "\n")}
</#list>