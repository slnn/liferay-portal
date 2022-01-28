<#list dataFactory.getSequence(dataFactory.maxSegmentsEntryCount) as index>
	${dataFactory.toInsertSQL(dataFactory.newSegmentsEntry(guestGroupId, index))}
</#list>