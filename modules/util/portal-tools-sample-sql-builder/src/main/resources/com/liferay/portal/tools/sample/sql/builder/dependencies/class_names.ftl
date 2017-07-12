<#list initContext.classNameModelValues as classNameModelValue>
	${initContext.toInsertSQL(classNameModelValue)}
</#list>
