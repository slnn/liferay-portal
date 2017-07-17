<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${releaseDataFactory.toInsertSQL(releaseModel)}
</#list>