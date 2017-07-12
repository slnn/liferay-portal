${initContext.toInsertSQL(userDataFactory.companyModel)}

${initContext.toInsertSQL(userDataFactory.accountModel)}

${initContext.toInsertSQL(userDataFactory.virtualHostModel)}

${initContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}
