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

package com.liferay.comment.editor.configuration.internal;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ambrin Chaudhary
 */
@Component(
	property = {"editor.config.key=commentEditor", "service.ranking:Integer=0"},
	service = EditorConfigContributor.class
)
public class CommentEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> attributes, ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory urlFactory) {

		collector.collect(
			"allowedContent",
			() -> PropsValues.DISCUSSION_COMMENTS_ALLOWED_CONTENT);
		collector.collect("toolbars", () -> JSONFactoryUtil.createJSONObject());

		if (PropsValues.DISCUSSION_COMMENTS_FORMAT.equals("bbcode")) {
			collector.collect(
				"extraPlugins",
				(String extraPlugins) -> {
					if (Validator.isNull(extraPlugins)) {
						extraPlugins = "bbcode";
					}
					else if (!extraPlugins.contains("bbcode")) {
						extraPlugins = extraPlugins + ",bbcode";
					}

					return extraPlugins;
				});
		}
	}

}