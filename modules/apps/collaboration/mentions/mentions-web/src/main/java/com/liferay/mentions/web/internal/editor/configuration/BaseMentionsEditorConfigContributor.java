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

package com.liferay.mentions.web.internal.editor.configuration;

import com.liferay.mentions.matcher.MentionsMatcherUtil;
import com.liferay.mentions.web.constants.MentionsPortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigElementContributorCollector;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.PortletURL;

/**
 * @author Sergio González
 */
public class BaseMentionsEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void collectEditorConfigElementContributors(
		EditorConfigElementContributorCollector collector,
		Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		collector.collect(
			"autocomplete",
			() -> {
				JSONObject autoCompleteConfigJSONObject =
					JSONFactoryUtil.createJSONObject();

				autoCompleteConfigJSONObject.put(
					"requestTemplate", "query={query}");

				JSONArray triggerJSONArray = JSONFactoryUtil.createJSONArray();

				JSONObject triggerJSONObject =
					JSONFactoryUtil.createJSONObject();

				triggerJSONObject.put(
					"regExp",
					"(?:\\strigger|^trigger)(" +
						MentionsMatcherUtil.getScreenNameRegularExpression() +
							")");
				triggerJSONObject.put(
					"resultFilters",
					"function(query, results) {return results;}");
				triggerJSONObject.put("resultTextLocator", "screenName");
				triggerJSONObject.put("term", "@");
				triggerJSONObject.put("tplReplace", "{mention}");

				StringBundler sb = new StringBundler(6);

				sb.append("<div class=\"nameplate\"><div class=\"");
				sb.append("nameplate-field\"><div class=\"user-icon\">");
				sb.append("<img class=\"img-circle\" src=\"{portraitURL}\" ");
				sb.append("height=\"32px\" width=\"32px\"></img></div></div>");
				sb.append("<div class=\"nameplate-content\"><h4>{fullName} ");
				sb.append("<small>@{screenName}</small></h4></div></div>");

				triggerJSONObject.put("tplResults", sb.toString());

				PortletURL autoCompleteUserURL =
					requestBackedPortletURLFactory.createResourceURL(
						MentionsPortletKeys.MENTIONS);

				String source =
					autoCompleteUserURL.toString() + "&" +
						PortalUtil.getPortletNamespace(
							MentionsPortletKeys.MENTIONS);

				triggerJSONObject.put("source", source);

				triggerJSONArray.put(triggerJSONObject);

				autoCompleteConfigJSONObject.put("trigger", triggerJSONArray);

				return autoCompleteConfigJSONObject;
			});
		collector.collect(
			"extraPlugins",
			(String extraPlugins) -> {
				if (Validator.isNotNull(extraPlugins)) {
					extraPlugins += ",autocomplete";
				}
				else {
					extraPlugins =
						"autocomplete,ae_placeholder,ae_selectionregion," +
							"ae_uicore";
				}

				return extraPlugins;
			});
	}

}