<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId, companyModel) />

${dataFactory.toInsertSQL(fragmentCollectionModel)}

<#assign fragmentEntryModel = dataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

${dataFactory.toInsertSQL(fragmentEntryModel)}

<#assign contentLayoutModels = dataFactory.newContentLayoutModels(groupId, companyModel) />

<#list contentLayoutModels as contentLayoutModel>
	<@insertContentLayout
		_fragmentEntryModel=fragmentEntryModel
		_layoutModel=contentLayoutModel
	/>

	${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
</#list>