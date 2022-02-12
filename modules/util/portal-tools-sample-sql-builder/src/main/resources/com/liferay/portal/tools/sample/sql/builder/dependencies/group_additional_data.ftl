<#include "asset_publisher.ftl">

<#include "blogs.ftl">

<#include "ddl.ftl">

<#include "journal_article.ftl">

<#include "fragment.ftl">

<#include "mb.ftl">

<#include "users.ftl">

<#include "wiki.ftl">

<@insertDLFolder
	_ddmStructureId=dataFactory.defaultDLDDMStructureId
	_dlFolderDepth=1
	_groupId=groupId
	_parentDLFolderId=0
/>

<#list dataFactory.newGroupLayoutModels(groupId) as groupLayoutModel>
	<@insertLayout _layoutModel=groupLayoutModel />
</#list>