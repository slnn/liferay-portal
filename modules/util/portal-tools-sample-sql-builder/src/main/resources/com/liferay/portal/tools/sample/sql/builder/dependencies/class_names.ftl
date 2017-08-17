<#list initRuntimeContext.classNameModelValues as classNameModelValue>
	${resourcePermissionDataFactory.toInsertSQL(classNameModelValue)}
</#list>