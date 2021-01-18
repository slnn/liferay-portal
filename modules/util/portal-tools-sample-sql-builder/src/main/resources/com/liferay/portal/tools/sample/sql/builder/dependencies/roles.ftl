<#list dataFactory.newRoleModels(companyModel) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>