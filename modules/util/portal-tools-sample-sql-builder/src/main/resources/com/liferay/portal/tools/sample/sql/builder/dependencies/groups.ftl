<#assign guestGroupModel = userDataFactory.newGuestGroupModel() />

<#include "default_user.ftl">

<@insertGroup _groupModel=userDataFactory.newGlobalGroupModel() />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=userDataFactory.newUserPersonalSiteGroupModel() />

<#list userDataFactory.newGroupModels() as groupModel>
	<#assign groupId = groupModel.groupId />

	<#include "asset_publisher.ftl">

	<#include "blogs.ftl">

	<#include "ddl.ftl">

	<#include "journal_article.ftl">

	<#include "fragment.ftl">

	<#include "mb.ftl">

	<#include "users.ftl">

	<#include "wiki.ftl">

	<@insertDLFolder
		_ddmStructureId=dLDataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupId=groupId
		_parentDLFolderId=0
	/>

	<@insertContentLayout _layoutModels=layoutDataFactory.newHomePageLayoutModels(groupId) />

	<#assign publicLayoutModels = layoutDataFactory.newPublicLayoutModels(groupId) />

	<#list publicLayoutModels as publicLayoutModel>
		<@insertLayout _layoutModel=publicLayoutModel />
	</#list>

	<@insertGroup _groupModel=groupModel />

	${csvFileWriter.write("repository", groupId + ", " + groupModel.name + "\n")}
</#list>

<@insertContentLayout _layoutModels=layoutDataFactory.newHomePageLayoutModels(guestGroupModel.groupId) />