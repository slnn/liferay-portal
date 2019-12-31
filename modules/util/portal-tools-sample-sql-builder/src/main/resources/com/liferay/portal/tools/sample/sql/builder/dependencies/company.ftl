<#assign companyModel = userDataFactory.newCompanyModel() />

${resourcePermissionDataFactory.toInsertSQL(companyModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newAccountModel())}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newVirtualHostModel())}

${dataFactory.getCSVWriter("company").write(companyModel.companyId + "\n")}