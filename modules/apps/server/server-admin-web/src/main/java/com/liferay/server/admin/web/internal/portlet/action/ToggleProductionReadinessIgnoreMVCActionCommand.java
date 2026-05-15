/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.production.readiness.ignore.service.ProductionReadinessIgnoreLocalServiceUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lily Chi
 */
@Component(
	property = {
		"javax.portlet.name=com_liferay_server_admin_web_portlet_ServerAdminPortlet",
		"mvc.command.name=/server_admin/toggle_production_readiness_ignore"
	},
	service = MVCActionCommand.class
)
public class ToggleProductionReadinessIgnoreMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String ruleKey = ParamUtil.getString(actionRequest, "ruleKey");
		boolean ignore = ParamUtil.getBoolean(actionRequest, "ignore");

		if (ignore) {
			ProductionReadinessIgnoreLocalServiceUtil.addProductionReadinessIgnore(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(), ruleKey,
				"Ignored from Server Administration");
		}
		else {
			ProductionReadinessIgnoreLocalServiceUtil.deleteProductionReadinessIgnore(
				themeDisplay.getCompanyId(), ruleKey);
		}
	}

}
