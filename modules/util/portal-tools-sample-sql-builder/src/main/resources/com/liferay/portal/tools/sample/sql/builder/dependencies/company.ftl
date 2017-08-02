${dataFactory.toInsertSQL(dataFactory.companyModel)}

${dataFactory.toInsertSQL(dataFactory.accountModel)}

${dataFactory.toInsertSQL(dataFactory.virtualHostModel)}

${initContext.getCSVWriter("company").write(dataFactory.companyModel.companyId + "\n")}