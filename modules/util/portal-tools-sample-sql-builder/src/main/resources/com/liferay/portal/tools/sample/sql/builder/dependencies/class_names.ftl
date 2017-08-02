<#list initContext.classNameModelValues as classNameModelValue>
	${dataFactory.toInsertSQL(classNameModelValue)}
</#list>