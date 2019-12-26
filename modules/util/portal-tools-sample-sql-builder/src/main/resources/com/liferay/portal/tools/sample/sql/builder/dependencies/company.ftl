<#assign companyModel = userDataFactory.newCompanyModel() />

${dataFactory.toInsertSQL(companyModel)}

${dataFactory.toInsertSQL(userDataFactory.newAccountModel())}

${dataFactory.toInsertSQL(userDataFactory.newVirtualHostModel())}

${dataFactory.getCSVWriter("company").write(companyModel.companyId + "\n")}