<#assign releaseModels = dataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${dataFactory.toInsertSQL(releaseModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(releaseModel)}
</#list>