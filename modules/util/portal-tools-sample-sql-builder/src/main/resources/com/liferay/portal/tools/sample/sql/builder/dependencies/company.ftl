<#list dataFactory.newResourceActionModels() as resourceActionModel>
	${dataFactory.toInsertSQL(resourceActionModel)}
</#list>

<#list dataFactory.newCompanyModels() as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setAccountId(companyModel.accountId)}

	${dataFactory.setWebId(companyModel.webId)}

	<#assign virtualHostModel = dataFactory.newVirtualHostModel() />

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

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

	<#list dataFactory.newDDMStructureModels() as ddmStructureModel>
		${dataFactory.toInsertSQL(ddmStructureModel)}

		<#assign ddmStructureVersionModel = dataFactory.newDDMStructureVersionModel(ddmStructureModel) />

		${dataFactory.toInsertSQL(ddmStructureVersionModel)}

		${dataFactory.toInsertSQL(dataFactory.newDDMStructureLayoutModel(ddmStructureModel, ddmStructureVersionModel))}
	</#list>

	<#list dataFactory.newDDMTemplateModels() as ddmTemplateModel>
		${dataFactory.toInsertSQL(ddmTemplateModel)}

		${dataFactory.toInsertSQL(dataFactory.newDDMTemplateVersionModel(ddmTemplateModel))}
	</#list>

	<#list dataFactory.newKaleoDefinitionModels() as kaleoDefinitionModel>
		${dataFactory.toInsertSQL(kaleoDefinitionModel)}

		<#assign startKaleoNodeModel = dataFactory.newStartKaleoNodeModel(kaleoDefinitionModel) />

		${dataFactory.toInsertSQL(startKaleoNodeModel)}

		${dataFactory.toInsertSQL(dataFactory.newKaleoDefinitionVersionModel(kaleoDefinitionModel, startKaleoNodeModel))}

		<#list dataFactory.newKaleoNodeModels() as kaleoNodeModel>
			${dataFactory.toInsertSQL(kaleoNodeModel)}
		</#list>
	</#list>
</#list>