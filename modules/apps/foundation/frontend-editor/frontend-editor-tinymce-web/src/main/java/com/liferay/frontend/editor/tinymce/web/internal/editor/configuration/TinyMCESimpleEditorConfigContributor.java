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

package com.liferay.frontend.editor.tinymce.web.internal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrin Chaudhary
 */
@Component(
	property = {"editor.name=tinymce_simple"},
	service = EditorConfigContributor.class
)
public class TinyMCESimpleEditorConfigContributor
	extends BaseTinyMCEEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		super.collectEditorConfigElementContributors(
			collector, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		collector.collect(
			"plugins",
			() -> {
				String plugins = "contextmenu preview print";

				if (isShowSource(inputEditorTaglibAttributes)) {
					plugins += " code";
				}

				return plugins;
			});
		collector.collect(
			"toolbar",
			() -> {
				String toolbar =
					"bold italic underline | alignleft aligncenter " +
						"alignright alignjustify | ";

				if (isShowSource(inputEditorTaglibAttributes)) {
					toolbar += "code ";
				}

				toolbar += "preview print";

				return toolbar;
			});
	}

}