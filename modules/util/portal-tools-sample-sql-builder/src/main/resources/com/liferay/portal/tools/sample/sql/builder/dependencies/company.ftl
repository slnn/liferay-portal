<#list dataFactory.companyModels as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setWebId(companyModel.webId)}

	${csvFileWriter.write("company", companyModel.webId + "," + companyModel.companyId + "\n")}

	<#include "roles.ftl">

	<#include "users.ftl">

	<#include "default_user.ftl">

	<#assign guestGroupId = dataFactory.getGuestGroupId() />

	<#include "blogs.ftl">
</#list>