<#list dataFactory.newRoleModels(companyModel, sampleUserModel) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>