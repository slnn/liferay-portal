<#assign companyModel = dataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(dataFactory.newAccountModel(companyModel))}

${dataFactory.toInsertSQL(dataFactory.newVirtualHostModel(companyModel))}

${csvFileWriter.write("company", companyModel.companyId + "\n")}

<#include "roles.ftl">

<#include "asset.ftl">

<#include "commerce_product.ftl">

<#include "ddm.ftl">

<#include "default_dl_file_type.ftl">

<#include "groups.ftl">