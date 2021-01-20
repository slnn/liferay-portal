<#assign guestGroupModel = dataFactory.newGuestGroupModel(companyModel, sampleUserModel) />

<#include "default_user.ftl">

<#include "commerce_product.ftl">

<#include "ddm.ftl">

<#include "segments.ftl">

<@insertLayout _layoutModel=dataFactory.newLayoutModel(guestGroupModel.groupId, "welcome", "com_liferay_login_web_portlet_LoginPortlet,", "com_liferay_hello_world_web_portlet_HelloWorldPortlet,", companyModel, sampleUserModel) />

<@insertGroup _groupModel=dataFactory.newGlobalGroupModel(companyModel, sampleUserModel) />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=dataFactory.newUserPersonalSiteGroupModel(companyModel, defaultUserModel, sampleUserModel) />

<#list dataFactory.newGroupModels(companyModel, sampleUserModel) as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "ddl.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<@insertDLFolder
		_ddmStructureId=defaultDLDDMStructureModel.structureId
		_ddmStructureVersionId=defaultDLDDMStructureVersionModel.structureVersionId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<#assign groupLayoutModels = dataFactory.newGroupLayoutModels(groupId, companyModel, sampleUserModel) />

	<#list groupLayoutModels as groupLayoutModel>
		<@insertLayout _layoutModel=groupLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${csvFileWriter.write("repository", groupId + ", " + groupModel.name + "\n")}
</#list>