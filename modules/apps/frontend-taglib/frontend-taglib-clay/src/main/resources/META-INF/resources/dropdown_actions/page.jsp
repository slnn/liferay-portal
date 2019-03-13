<%--
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
--%>

<%@ include file="/dropdown_actions/init.jsp" %>

<%
String dropdownItems = (String)request.getAttribute("clay:dropdown-actions:dropdownItems");

String id = StringUtil.randomId();
%>

<div class="dropdown dropdown-action" id="<%= id %>">
	<button aria-expanded="false" aria-haspopup="true" class="btn btn-unstyled dropdown-toggle" title="Actions" type="button">
		<svg aria-hidden="true" class="lexicon-icon lexicon-icon-ellipsis-v">
			<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#ellipsis-v" />
		</svg>
	</button>
</div>

<aui:script require="frontend-taglib-clay$clay-dropdown@2.9.0/lib/ClayActionsDropdown as ClayActionsDropdown">
	new ClayActionsDropdown.default(
		{
			element: '#<%= id %>',
			items: JSON.parse('<%= dropdownItems %>'),
			spritemap: '<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg'
		}
	);
</aui:script>