<#list dataFactory.companyModelLists as companyModelList>
	${dataFactory.setCompanyId(companyModelList[0])}

	${dataFactory.setWebId(companyModelList[1])}

	${csvFileWriter.write("company", companyModelList[1] + "," + companyModelList[0] + "\n")}

	${dataFactory.initRoleModels()}

	<#include "groups.ftl">
</#list>