<#assign ddmStructureModel = journalDataFactory.defaultJournalDDMStructureModel />

<@insertDDMStructure
	_ddmStructureLayoutModel=journalDataFactory.defaultJournalDDMStructureLayoutModel
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=journalDataFactory.defaultJournalDDMStructureVersionModel
/>

<#assign ddmTemplateModel = journalDataFactory.defaultJournalDDMTemplateModel />

${journalDataFactory.toInsertSQL(ddmTemplateModel)}

<#assign
	journalArticlePageCounts = dataFactory.getSequence(initContext.maxJournalArticlePageCount)

	resourcePermissionModels = dataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${journalDataFactory.toInsertSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = layoutDataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", journalDataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${initContext.getCSVWriter("layout").write(layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = portletPreferenceDataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${journalDataFactory.toInsertSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = dataFactory.getSequence(initContext.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = journalDataFactory.newJournalArticleResourceModel(groupId) />

		${dataFactory.toInsertSQL(journalArticleResourceModel)}

		<#assign versionCounts = dataFactory.getSequence(initContext.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = journalDataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${journalDataFactory.toInsertSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = journalDataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${journalDataFactory.toInsertSQL(journalArticleLocalizationModel)}

			${journalDataFactory.toInsertSQL(dDLDataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${journalDataFactory.toInsertSQL(dDLDataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${journalDataFactory.toInsertSQL(socialActivityDataFactory.newSocialActivityModel(journalArticleModel))}

			<#if versionCount = initContext.maxJournalArticleVersionCount>
				<@insertAssetEntry
					_categoryAndTag=true
					_entry=journalDataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
				/>
			</#if>
		</#list>

		<@insertMBDiscussion
			_classNameId=journalDataFactory.journalArticleClassNameId
			_classPK=journalArticleResourceModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=0
			_mbRootMessageId=dataFactory.getCounterNext()
			_mbThreadId=dataFactory.getCounterNext()
		/>

		${journalDataFactory.toInsertSQL(portletPreferenceDataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel))}

		${journalDataFactory.toInsertSQL(journalDataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.plid))}
	</#list>
</#list>