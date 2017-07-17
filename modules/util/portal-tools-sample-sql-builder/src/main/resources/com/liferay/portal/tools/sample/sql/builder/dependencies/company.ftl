${userDataFactory.toInsertSQL(userDataFactory.companyModel)}

${userDataFactory.toInsertSQL(userDataFactory.accountModel)}

${userDataFactory.toInsertSQL(userDataFactory.virtualHostModel)}

${initContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}