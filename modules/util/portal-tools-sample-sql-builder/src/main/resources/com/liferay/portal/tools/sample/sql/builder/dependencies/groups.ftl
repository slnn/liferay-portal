<#assign guestGroupModel = dataFactory.newGuestGroupModel(companyModel) />

<#include "default_user.ftl">

<#include "segments.ftl">

<@insertLayout _layoutModel=dataFactory.newLayoutModel(guestGroupModel.groupId, "welcome", "com_liferay_login_web_portlet_LoginPortlet,", "com_liferay_hello_world_web_portlet_HelloWorldPortlet,", companyModel) />

<@insertGroup _groupModel=dataFactory.newGlobalGroupModel(companyModel) />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=dataFactory.newUserPersonalSiteGroupModel(companyModel) />

<#list dataFactory.newGroupModels(companyModel) as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "ddl.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<@insertDLFolder
		_ddmStructureId=defaultDLDDMStructureModel.structureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<#assign groupLayoutModels = dataFactory.newGroupLayoutModels(groupId, companyModel) />

	<#list groupLayoutModels as groupLayoutModel>
		<@insertLayout _layoutModel=groupLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${csvFileWriter.write("repository", groupId + ", " + groupModel.name + "\n")}
</#list>