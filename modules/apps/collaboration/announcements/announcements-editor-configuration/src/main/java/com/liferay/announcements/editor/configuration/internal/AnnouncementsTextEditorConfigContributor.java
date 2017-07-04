/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.announcements.editor.configuration.internal;

import com.liferay.announcements.web.constants.AnnouncementsPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"editor.config.key=titleEditor",
		"javax.portlet.name=" + AnnouncementsPortletKeys.ALERTS,
		"javax.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
		"javax.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN
	},
	service = EditorConfigContributor.class
)
public class AnnouncementsTextEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		collector.collect("allowedContent", () -> "p");
		collector.collect("disallowedContent", () -> "br");
		collector.collect(
			"extraPlugins",
			() -> "ae_placeholder,ae_selectionregion,ae_uicore");
		collector.collect("toolbars", () -> JSONFactoryUtil.createJSONObject());
	}

}