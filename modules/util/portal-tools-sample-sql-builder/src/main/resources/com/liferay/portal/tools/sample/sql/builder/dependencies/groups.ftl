<#assign
	globalGroupId = dataFactory.getGlobalGroupId()
	guestGroupId = dataFactory.getGuestGroupId()

	commerceCurrencyModel = dataFactory.newCommerceCurrencyModel()
	countryModel = dataFactory.newCountryModel()
	currentCompanyModelList = companyModelList
/>

${dataFactory.toInsertSQL(countryModel)}

<#include "default_user.ftl">

<#include "commerce_groups.ftl">

<#include "ddm.ftl">

<#include "segments.ftl">

<#if dataFactory.defaultSiteAdditionalDataEnable>
	<#assign groupId = guestGroupId />

	<#include "group_additional_data.ftl">
<#else>
	<#list dataFactory.newGroupModels() as groupModel>
		<#assign
			groupId = groupModel.groupId
			homePageContentLayoutModels = dataFactory.newContentPageLayoutModels(groupId, "welcome")
		/>

		<#include "group_additional_data.ftl">

		<@insertContentPageLayout
			_fragmentEntryLinkModels=dataFactory.newFragmentEntryLinkModels(homePageContentLayoutModels)
			_layoutModels=homePageContentLayoutModels
			_templateFileName="default-homepage-layout-definition.json"
		/>

		<@insertGroup _groupModel=groupModel />

		${csvFileWriter.write("repository", groupId + ", " + groupModel.name + "\n")}
	</#list>
</#if>