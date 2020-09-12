<#list userDataFactory.newRoleModels(classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Role")) as roleModel>
	${resourcePermissionDataFactory.toInsertSQL(roleModel)}
</#list>