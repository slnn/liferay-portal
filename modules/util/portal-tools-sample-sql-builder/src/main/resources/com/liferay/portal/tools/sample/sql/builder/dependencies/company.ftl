${resourcePermissionDataFactory.toInsertSQL(userDataFactory.companyModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.accountModel)}

${resourcePermissionDataFactory.toInsertSQL(userDataFactory.virtualHostModel)}

${initRuntimeContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}