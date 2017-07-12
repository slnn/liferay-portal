<#list userDataFactory.roleModels as roleModel>
	${initContext.toInsertSQL(roleModel)}
</#list>