<#assign
	globalGroupModel = dataFactory.newGlobalGroupModel()
	guestGroupModel = dataFactory.newGuestGroupModel()
/>

<#include "guest_user.ftl">

<#include "commerce_groups.ftl">

<@insertGroup _groupModel=globalGroupModel />

<@insertGroup _groupModel=guestGroupModel />

<@insertGroup _groupModel=dataFactory.newUserPersonalSiteGroupModel() />

<#include "asset.ftl">

<#include "ddm.ftl">

<#include "segments.ftl">

<#list dataFactory.newGroupModels() as groupModel>
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
		_ddmStructureId=dataFactory.defaultDLDDMStructureId
		_dlFolderDepth=1
		_groupModel=groupModel
		_parentDLFolderId=0
	/>

	<#assign homePageContentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, "welcome") />

	<#list dataFactory.newPortletPreferencesModels(homePageContentLayoutModels) as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<@insertContentPageLayout
		_fragmentEntryLinkModels=dataFactory.newFragmentEntryLinkModels(homePageContentLayoutModels)
		_layoutModels=homePageContentLayoutModels
		_templateFileName="default-homepage-layout-definition.json"
	/>

	<#list dataFactory.newGroupLayoutModels(groupId) as groupLayoutModel>
		<@insertLayout _layoutModel=groupLayoutModel />

		<#list dataFactory.newPortletPreferencesModels(groupLayoutModel) as portletPreferencesModel>
				${dataFactory.toInsertSQL(portletPreferencesModel)}
				<#if portletPreferencesModel.portletId = "com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet_INSTANCE_templateSearch">
					<#list dataFactory.newPortletPreferenceValueModels(portletPreferencesModel) as portletPreferenceValueModel>
						${dataFactory.toInsertSQL(portletPreferenceValueModel)}
					</#list>
				</#if>
		</#list>
	</#list>

	<#list dataFactory.newResourcePermissionModels(groupId) as resourcePermissionModel>
		${dataFactory.toInsertSQL(resourcePermissionModel)}
	</#list>

	<@insertGroup _groupModel=groupModel />

	${csvFileWriter.write("repository", virtualHostModel.hostname + "," + groupModel.friendlyURL + "," + groupId + ", " + groupModel.name + "\n")}
</#list>

<#assign defaultSiteHomePageContentLayoutModels = dataFactory.newContentPageLayoutModels(guestGroupModel.groupId, "welcome") />

<#list dataFactory.newPortletPreferencesModels(defaultSiteHomePageContentLayoutModels) as portletPreferencesModel>
	${dataFactory.toInsertSQL(portletPreferencesModel)}
</#list>

<@insertContentPageLayout
	_fragmentEntryLinkModels=dataFactory.newFragmentEntryLinkModels(defaultSiteHomePageContentLayoutModels)
	_layoutModels=defaultSiteHomePageContentLayoutModels
	_templateFileName="default-homepage-layout-definition.json"
/>

<#assign
	searchLayoutModel = dataFactory.newSearchLayoutModel(guestGroupModel.groupId, true)
	layoutPrototypeModel = dataFactory.newLayoutPrototypeModel(defaultAdminUserModel.userId)
	searchTemplateGroupModel = dataFactory.newSearchTemplateGroupModel(layoutPrototypeModel.layoutPrototypeId, defaultAdminUserModel.userId)
	searchGroupLayoutModel = dataFactory.newSearchGroupLayoutModel(searchTemplateGroupModel.groupId, searchLayoutModel)
/>

<@insertLayout _layoutModel=searchLayoutModel />

<#list dataFactory.newPortletPreferencesModels(searchLayoutModel) as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
		<#if portletPreferencesModel.portletId = "com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet_INSTANCE_templateSearch">
			  <#list dataFactory.newPortletPreferenceValueModels(portletPreferencesModel) as portletPreferenceValueModel>
			  	  ${dataFactory.toInsertSQL(portletPreferenceValueModel)}
			  </#list>
		</#if>
</#list>

<@insertLayout _layoutModel=searchGroupLayoutModel />

${dataFactory.toInsertSQL(layoutPrototypeModel)}

<@insertGroup _groupModel=searchTemplateGroupModel />