<#assign counterModels = dataFactory.newCounterModels() />

<#list counterModels as counterModel>
	<#if dataFactory.updateCounter(counterModel)>
		update Counter set currentId = ${counterModel.currentId} where name = '${counterModel.name}';
	<#else>
		${dataFactory.toInsertSQL(counterModel)}
	</#if>
</#list>