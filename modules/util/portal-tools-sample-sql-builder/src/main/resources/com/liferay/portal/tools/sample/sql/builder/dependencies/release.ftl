<#assign releaseModels = dataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${resourcePermissionDataFactory.toInsertSQL(releaseModel)}
</#list>