<#list initContext.classNameModelValues as classNameModelValue>
	${userDataFactory.toInsertSQL(classNameModelValue)}
</#list>