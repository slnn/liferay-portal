<#list userDataFactory.newRoleModels(classNameDataFactory.getClassNameId("com.liferay.portal.kernel.model.Role")) as roleModel>
	${dataFactory.toInsertSQL(roleModel)}
</#list>