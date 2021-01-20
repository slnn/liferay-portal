<#assign
	assetVocabularyModels = dataFactory.newAssetVocabularyModels(groupId, companyModel, sampleUserModel)
	assetCategoryModels = dataFactory.newAssetCategoryModels(groupId, assetVocabularyModels)
	assetTagModels = dataFactory.newAssetTagModels(groupId, companyModel, sampleUserModel)
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list assetCategoryModels as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list assetTagModels as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>

<#include "blogs.ftl">

<#include "journal_article.ftl">

<#include "wiki.ftl">

<#list pageCounts as pageCount>
	<#assign
		portletId = dataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId, companyModel, sampleUserModel)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid, companyModel) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#if pageCount = 1>
		<#assign assetPublisherPortletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, portletId, companyModel) />

		${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}

		<#list dataFactory.newAssetPublisherPortletPreferenceValueModels(assetPublisherPortletPreferencesModel, groupId, pageCount, null) as assetPublisherPortletPreferencesModel>
			${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}
		</#list>
	<#elseif pageCount % 2 = 0>
		<#assign
			nextAssetTagModels = dataFactory.getNextAssetTagModels([journalArticleAssetTagModels, blogAssetTagModels, wikiAssetTagModels])
			assetPublisherPortletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, nextAssetTagModels, companyModel)
		/>

		${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}

		<#list dataFactory.newAssetPublisherPortletPreferenceValueModels(assetPublisherPortletPreferencesModel, groupId, pageCount, nextAssetTagModels) as assetPublisherPortletPreferencesModel>
			${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}
		</#list>
	<#else>
		<#assign
			nextAssetCategoryModels = dataFactory.getNextAssetCategoryModels([journalArticleAssetCategoryModels, blogAssetCategoryModels, wikiAssetCategoryModels])
			assetPublisherPortletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, nextAssetCategoryModels, companyModel)
		/>

		${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}

		<#list dataFactory.newAssetPublisherPortletPreferenceValueModels(assetPublisherPortletPreferencesModel, groupId, pageCount, nextAssetCategoryModels) as assetPublisherPortletPreferencesModel>
			${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}
		</#list>
	</#if>
</#list>