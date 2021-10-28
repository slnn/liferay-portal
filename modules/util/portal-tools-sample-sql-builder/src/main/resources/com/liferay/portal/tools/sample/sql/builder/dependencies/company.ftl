${csvFileWriter.write("company", dataFactory.defaultCompanyId + "\n")}

<#list dataFactory.companyModels as companyModel>
	${dataFactory.setCompanyId(companyModel.companyId)}

	${dataFactory.setWebId(companyModel.webId)}

	${csvFileWriter.write("company", companyModel.companyId + "\n")}

	<#include "roles.ftl">

	<#include "users.ftl">

	<#include "default_user.ftl">

	<#include "ddm.ftl">

	<#assign guestGroupId = dataFactory.getGuestGroupId() />

	<#list dataFactory.newGroupLayoutModels(guestGroupId) as groupLayoutModel>
		<@insertLayout _layoutModel=groupLayoutModel />
	</#list>

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "journal_article.ftl">

	<#include "mb.ftl">

	<#include "wiki.ftl">

	<@insertDLFolder
		_ddmStructureId=dataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=guestGroupId
		_parentDLFolderId=0
	/>
</#list>
