<#assign
	blogsEntryClassNameId = classNameDataFactory.getClassNameId("com.liferay.blogs.model.BlogsEntry")
	journalArticleClassNameId = classNameDataFactory.getClassNameId("com.liferay.journal.model.JournalArticle")
	wikiPageClassNameId = classNameDataFactory.getClassNameId("com.liferay.wiki.model.WikiPage")
	pageCounts = counterDataFactory.getSequence(assetDataFactory.maxAssetPublisherPageCount)
/>

<#list pageCounts as pageCount>
	<#assign
		portletId = portletPreferenceDataFactory.getPortletId("com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_")

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_asset_publisher_" + pageCount, "", portletId)
	/>

	${csvFileWriter.write("assetPublisher", layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newAssetPublisherPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${insertSQLBuilder.toInsertSQL(portletPreferencesModel)}
	</#list>

	${insertSQLBuilder.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, groupId, portletId, pageCount, blogsEntryClassNameId, journalArticleClassNameId, wikiPageClassNameId, assetCategoryModelsMaps))}
</#list>