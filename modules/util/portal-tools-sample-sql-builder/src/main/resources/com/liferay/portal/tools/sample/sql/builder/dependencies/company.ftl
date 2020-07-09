<#assign companyModel = userDataFactory.newCompanyModel() />

${resourcePermissionDataFactory.toInsertSQL(companyModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newAccountModel())}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.newVirtualHostModel())}

${userDataFactory.getCSVWriter("company").write(companyModel.companyId + "\n")}