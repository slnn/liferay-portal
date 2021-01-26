<#list dataFactory.newRoleModels(companyModel, sampleUserModel) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}

	<#list dataFactory.newResourcePermissionModels(roleModel, dataFactory.ownerRoleModel) as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>
</#list>