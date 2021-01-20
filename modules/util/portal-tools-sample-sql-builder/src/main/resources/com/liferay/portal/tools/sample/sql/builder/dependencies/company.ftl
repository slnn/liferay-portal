<#list dataFactory.newCompanyModels() as companyModel>
	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

	${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel(companyModel))}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#assign companyModel = companyModel />

	<#include "sample_user.ftl">

	<#include "roles.ftl">

	<#include "groups.ftl">

	<#include "asset.ftl">
</#list>