<#assign releaseModels = releaseDataFactory.newReleaseModels() />

<#list releaseModels as releaseModel>
	${initContext.toInsertSQL(releaseModel)}
</#list>