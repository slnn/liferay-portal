<#list dataFactory.getSequence(dataFactory.maxVirtualInstanceCount) as virtualInstanceCount>
	<#assign companyModel = dataFactory.newCompanyModel(virtualInstanceCount) />

	${dataFactory.toInsertSQL(companyModel)}

	${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

	${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel(companyModel))}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#-- Sample user -->

	<#assign sampleUserModel = dataFactory.newSampleUserModel() />

	<#include "roles.ftl">

	<#include "groups.ftl">
</#list>