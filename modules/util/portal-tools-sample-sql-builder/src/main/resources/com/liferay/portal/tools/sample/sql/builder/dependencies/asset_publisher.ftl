<#assign
	assetVocabularyModels = dataFactory.newAssetVocabularyModels(guestGroupId)
	pageCounts = dataFactory.getSequence(dataFactory.maxAssetPublisherPageCount)
/>

<#list assetVocabularyModels as assetVocabularyModel>
	${dataFactory.toInsertSQL(assetVocabularyModel)}
</#list>

<#list dataFactory.newAssetCategoryModels(guestGroupId, assetVocabularyModels) as assetCategoryModel>
	${dataFactory.toInsertSQL(assetCategoryModel)}
</#list>

<#list dataFactory.newAssetTagModels(guestGroupId) as assetTagModel>
	${dataFactory.toInsertSQL(assetTagModel)}
</#list>

<#list pageCounts as pageCount>
	<#assign
		portletId = dataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = dataFactory.newLayoutModel(guestGroupId, guestGroupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", companyModel.webId + "," + layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#assign assetPublisherPortletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, guestGroupId, portletId, pageCount) />

	${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}

	<#assign assetPublisherPortletPreferencesModels = dataFactory.newAssetPublisherPortletPreferenceValueModels(assetPublisherPortletPreferencesModel, guestGroupId, pageCount) />

	<#list assetPublisherPortletPreferencesModels as assetPublisherPortletPreferencesModel>
		${dataFactory.toInsertSQL(assetPublisherPortletPreferencesModel)}
	</#list>
</#list>