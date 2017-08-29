<#list initRuntimeContext.classNameModelValues as classNameModelValue>
	${userDataFactory.toInsertSQL(classNameModelValue)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(classNameModelValue)}
</#list>