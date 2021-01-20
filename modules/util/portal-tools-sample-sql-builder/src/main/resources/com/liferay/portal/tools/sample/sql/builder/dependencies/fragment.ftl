<#assign fragmentCollectionModel = dataFactory.newFragmentCollectionModel(groupId, companyModel, sampleUserModel) />

${dataFactory.toInsertSQL(fragmentCollectionModel)}

<#assign fragmentEntryModel = dataFactory.newFragmentEntryModel(groupId, fragmentCollectionModel) />

${dataFactory.toInsertSQL(fragmentEntryModel)}

<#assign contentLayoutModels = dataFactory.newContentLayoutModels(groupId, companyModel, sampleUserModel) />

<#list contentLayoutModels as contentLayoutModel>
	<@insertContentLayout
		_fragmentEntryModel=fragmentEntryModel
		_layoutModel=contentLayoutModel
	/>

	${csvFileWriter.write("fragment", contentLayoutModel.friendlyURL + "\n")}
</#list>