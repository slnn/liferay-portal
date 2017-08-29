<#assign ddmStructureModel = dataFactory.defaultJournalDDMStructureModel />

<@insertDDMStructure
	_ddmStructureLayoutModel=dataFactory.defaultJournalDDMStructureLayoutModel
	_ddmStructureModel=ddmStructureModel
	_ddmStructureVersionModel=dataFactory.defaultJournalDDMStructureVersionModel
/>

<#assign ddmTemplateModel = dataFactory.defaultJournalDDMTemplateModel />

${dataFactory.toInsertSQL(ddmTemplateModel)}

${resourcePermissionDataFactory.generateResourcePermissionSQL(ddmTemplateModel)}

<#assign
	journalArticlePageCounts = dataFactory.getSequence(initPropertiesContext.maxJournalArticlePageCount)

	resourcePermissionModels = resourcePermissionDataFactory.newResourcePermissionModels("com.liferay.journal", groupId)
/>

<#list resourcePermissionModels as resourcePermissionModel>
	${dataFactory.toInsertSQL(resourcePermissionModel)}

	${resourcePermissionDataFactory.generateResourcePermissionSQL(resourcePermissionModel)}
</#list>

<#list journalArticlePageCounts as journalArticlePageCount>
	<#assign
		portletIdPrefix = "com_liferay_journal_content_web_portlet_JournalContentPortlet_INSTANCE_TEST_" + journalArticlePageCount + "_"

		layoutModel = dataFactory.newLayoutModel(groupId, groupId + "_journal_article_" + journalArticlePageCount, "", dataFactory.getJournalArticleLayoutColumn(portletIdPrefix))
	/>

	${initRuntimeContext.getCSVWriter("layout").write(layoutModel.friendlyURL + "\n")}

	<@insertLayout _layoutModel=layoutModel />

	<#assign portletPreferencesModels = dataFactory.newJournalPortletPreferencesModels(layoutModel.plid) />

	<#list portletPreferencesModels as portletPreferencesModel>
		${dataFactory.toInsertSQL(portletPreferencesModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(portletPreferencesModel)}
	</#list>

	<#assign journalArticleCounts = dataFactory.getSequence(initPropertiesContext.maxJournalArticleCount) />

	<#list journalArticleCounts as journalArticleCount>
		<#assign journalArticleResourceModel = dataFactory.newJournalArticleResourceModel(groupId) />

		${dataFactory.toInsertSQL(journalArticleResourceModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleResourceModel)}

		<#assign versionCounts = dataFactory.getSequence(initPropertiesContext.maxJournalArticleVersionCount) />

		<#list versionCounts as versionCount>
			<#assign journalArticleModel = dataFactory.newJournalArticleModel(journalArticleResourceModel, journalArticleCount, versionCount) />

			${dataFactory.toInsertSQL(journalArticleModel)}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleModel)}

			<#assign journalArticleLocalizationModel = dataFactory.newJournalArticleLocalizationModel(journalArticleModel, journalArticleCount, versionCount) />

			${dataFactory.toInsertSQL(journalArticleLocalizationModel)}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(journalArticleLocalizationModel)}

			${dataFactory.toInsertSQL(dataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newDDMTemplateLinkModel(journalArticleModel, ddmTemplateModel.templateId))}

			${dataFactory.toInsertSQL(dataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newDDMStorageLinkModel(journalArticleModel, ddmStructureModel.structureId))}

			${dataFactory.toInsertSQL(dataFactory.newSocialActivityModel(journalArticleModel))}

			${resourcePermissionDataFactory.generateResourcePermissionSQL(dataFactory.newSocialActivityModel(journalArticleModel))}

			<#if versionCount = initPropertiesContext.maxJournalArticleVersionCount>
				<@insertAssetEntry
					_categoryAndTag=true
					_entry=dataFactory.newObjectValuePair(journalArticleModel, journalArticleLocalizationModel)
				/>
			</#if>
		</#list>

		<@insertMBDiscussion
			_classNameId=dataFactory.journalArticleClassNameId
			_classPK=journalArticleResourceModel.resourcePrimKey
			_groupId=groupId
			_maxCommentCount=0
			_mbRootMessageId=dataFactory.getCounterNext()
			_mbThreadId=dataFactory.getCounterNext()
		/>

		<#assign portletPreferencesModel = dataFactory.newPortletPreferencesModel(layoutModel.plid, portletIdPrefix + journalArticleCount, journalArticleResourceModel) />

		${dataFactory.toInsertSQL(portletPreferencesModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(portletPreferencesModel)}

		<#assign journalContentSearchModel = dataFactory.newJournalContentSearchModel(journalArticleModel, layoutModel.plid) />

		${dataFactory.toInsertSQL(journalContentSearchModel)}

		${resourcePermissionDataFactory.generateResourcePermissionSQL(journalContentSearchModel)}
	</#list>
</#list>