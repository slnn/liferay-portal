${userDataFactory.toInsertSQL(userDataFactory.companyModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.companyModel)}

${userDataFactory.toInsertSQL(userDataFactory.accountModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.accountModel)}

${userDataFactory.toInsertSQL(userDataFactory.virtualHostModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(userDataFactory.virtualHostModel)}

${initRuntimeContext.getCSVWriter("company").write(userDataFactory.companyModel.companyId + "\n")}