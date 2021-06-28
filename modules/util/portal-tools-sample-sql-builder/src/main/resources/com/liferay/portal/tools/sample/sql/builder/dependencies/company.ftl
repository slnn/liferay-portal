<#list dataFactory.newResourceActionModels() as resourceActionModel>
	${dataFactory.toInsertSQL(resourceActionModel)}
</#list>

<#list dataFactory.newDDMTemplateModels() as ddmTemplateModel>
	${dataFactory.toInsertSQL(ddmTemplateModel)}
</#list>

<#list dataFactory.newCompanyModels() as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	<#assign virtualHostModel = dataFactory.newVirtualHostModel(companyModel) />

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

	${dataFactory.toInsertSQL(virtualHostModel)}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#list dataFactory.newPortletModels(companyModel) as portletModel>
		${dataFactory.toInsertSQL(portletModel)}
	</#list>

	<#include "roles.ftl">

	<#include "groups.ftl">

	<#list dataFactory.newResourcePermissionModels() as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>
</#list>