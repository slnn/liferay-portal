<#list dataFactory.companyModels as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setWebId(companyModel.webId)}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#include "roles.ftl">

	<#include "users.ftl">

	<#include "default_user.ftl">

	<#assign guestGroupId = dataFactory.getGuestGroupId() />

	<#list dataFactory.newGroupLayoutModels(guestGroupId) as groupLayoutModel>
		<@insertLayout _layoutModel=groupLayoutModel />
	</#list>

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">
</#list>