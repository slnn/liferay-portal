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

<%@ include file="/html/common/themes/init.jsp" %>

<%-- Raw Text --%>

<%
StringBundler bodyTopSB = OutputTag.getData(request, WebKeys.PAGE_BODY_TOP);
%>

<c:if test="<%= bodyTopSB != null %>">

	<%
	bodyTopSB.writeTo(out);
	%>

</c:if>

<%@ include file="/html/common/themes/top_messages.jsp" %>

<liferay-util:include page="/html/common/themes/body_top-ext.jsp" />

<c:if test="<%= themeDisplay.isSignedIn() && user.isSetupComplete() %>">
	<%
	String productMenuState = SessionClicks.get(request, "com.liferay.control.menu.web_productMenuState", "closed");
	%>

	<div class="<%= productMenuState %> lfr-product-menu-panel sidenav-fixed sidenav-menu-slider" id="sidenavSliderId">
		<div class="product-menu sidebar sidenav-menu">
			<liferay-portlet:runtime
				portletProviderClassName="com.liferay.portlet.admin.util.PortalProductMenuApplicationType$ProductMenu"
				portletProviderAction="<%= PortletProvider.Action.VIEW %>"
			/>
		</div>
	</div>

	<liferay-portlet:runtime
		portletProviderClassName="com.liferay.portlet.admin.util.PortalControlMenuApplicationType$ControlMenu"
		portletProviderAction="<%= PortletProvider.Action.VIEW %>"
	/>
</c:if>