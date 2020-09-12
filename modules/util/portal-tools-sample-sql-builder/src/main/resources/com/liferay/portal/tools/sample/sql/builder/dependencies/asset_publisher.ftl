<#assign
	blogsEntryClassNameId = classNameDataFactory.getClassNameId("com.liferay.blogs.model.BlogsEntry")
	wikiPageClassNameId = classNameDataFactory.getClassNameId("com.liferay.wiki.model.WikiPage")
	pageCounts = counterDataFactory.getSequence(assetDataFactory.maxAssetPublisherPageCount)
/>
<#list pageCounts as pageCount>
	<#assign
		portletId = portletPreferenceDataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	${dataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, assetClassNameIds, assetCategoryModelsMaps, assetTagModelsMaps))}
</#list>