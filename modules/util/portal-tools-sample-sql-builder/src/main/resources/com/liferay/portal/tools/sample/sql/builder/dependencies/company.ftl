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
</#list>