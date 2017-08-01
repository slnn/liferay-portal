<#list userDataFactory.roleModels as roleModel>
	${userDataFactory.toInsertSQL(roleModel)}
</#list>