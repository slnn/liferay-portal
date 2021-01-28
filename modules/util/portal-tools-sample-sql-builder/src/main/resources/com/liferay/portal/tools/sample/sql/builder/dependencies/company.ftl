<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel())}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel())}

${csvFileWriter.write("company", companyModel.companyId + "\n")}

<#-- Sample user -->

<#assign sampleUserModel = dataFactory.newSampleUserModel() />

<#include "roles.ftl">

<#include "groups.ftl">