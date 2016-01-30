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

<%@page import="com.liferay.portal.kernel.portlet.PortletConfigurationLayoutUtil" %><%@
page import="com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType" %>

<portlet:defineObjects />

<%
String tilesPortletContent = GetterUtil.getString(request.getAttribute(WebKeys.PORTLET_CONTENT_JSP));

if (Validator.isBlank(tilesPortletContent)) {
	tilesPortletContent = GetterUtil.getString(TilesAttributeUtil.getTilesAttribute(pageContext, "portlet_content"));
}

TilesAttributeUtil.removeComponentContext(pageContext);

Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

PortletPreferences portletSetup = portletDisplay.getPortletSetup();

RenderResponseImpl renderResponseImpl = (RenderResponseImpl)PortletResponseImpl.getPortletResponseImpl(renderResponse);

String currentURL = PortalUtil.getCurrentURL(request);

Group group = layout.getGroup();

// Portlet title

String portletTitle = PortletConfigurationUtil.getPortletTitle(portletSetup, themeDisplay.getLanguageId());

if (portletDisplay.isAccess() && portletDisplay.isActive() && (portletTitle == null)) {
	portletTitle = renderResponseImpl.getTitle();
}

if (portletTitle == null) {
	portletTitle = PortalUtil.getPortletTitle(portlet, application, locale);
}

portletDisplay.setTitle(portletTitle);

// Portlet description

if (Validator.isNull(portletDisplay.getDescription())) {
	String portletDescription = PortalUtil.getPortletDescription(portlet, application, locale);

	portletDisplay.setDescription(portletDescription);
}

// URL close

String urlClose = themeDisplay.getPathMain() + "/portal/update_layout?p_auth=" + AuthTokenUtil.getToken(request) + "&p_l_id=" + plid + "&p_p_id=" + portletDisplay.getId() + "&p_v_l_s_g_id=" + themeDisplay.getSiteGroupId() + "&doAsUserId=" + HttpUtil.encodeURL(themeDisplay.getDoAsUserId()) + "&" + Constants.CMD + "=" + Constants.DELETE + "&referer=" + HttpUtil.encodeURL(themeDisplay.getPathMain() + "/portal/layout?p_l_id=" + plid + "&doAsUserId=" + themeDisplay.getDoAsUserId()) + "&refresh=1";

if (themeDisplay.isAddSessionIdToURL()) {
	urlClose = PortalUtil.getURLWithSessionId(urlClose, themeDisplay.getSessionId());
}

portletDisplay.setURLClose(urlClose);

// URL configuration

PortletURL urlConfiguration = PortletProviderUtil.getPortletURL(request, PortletConfigurationApplicationType.PortletConfiguration.CLASS_NAME, PortletProvider.Action.VIEW);

urlConfiguration.setWindowState(LiferayWindowState.POP_UP);

if (portlet.getConfigurationActionInstance() != null) {
	urlConfiguration.setParameter("mvcPath", "/edit_configuration.jsp");

	String settingsScope = (String)request.getAttribute(WebKeys.SETTINGS_SCOPE);

	settingsScope = ParamUtil.get(request, "settingsScope", settingsScope);

	if (Validator.isNotNull(settingsScope)) {
		urlConfiguration.setParameter("settingsScope", settingsScope);
	}
}
else if (PortletPermissionUtil.contains(permissionChecker, layout, portletDisplay.getId(), ActionKeys.PERMISSIONS)) {
	urlConfiguration.setParameter("mvcPath", "/edit_permissions.jsp");
}
else {
	urlConfiguration.setParameter("mvcPath", "/edit_sharing.jsp");
}

urlConfiguration.setParameter("redirect", currentURL);
urlConfiguration.setParameter("returnToFullPageURL", currentURL);
urlConfiguration.setParameter("portletConfiguration", Boolean.TRUE.toString());
urlConfiguration.setParameter("portletResource", portletDisplay.getId());
urlConfiguration.setParameter("resourcePrimKey", PortletPermissionUtil.getPrimaryKey(plid, portlet.getPortletId()));

portletDisplay.setURLConfiguration(urlConfiguration.toString());

StringBuilder urlConfigurationJSSB = new StringBuilder(14);

urlConfigurationJSSB.append("Liferay.Portlet.openWindow({bodyCssClass: 'dialog-with-footer',");
urlConfigurationJSSB.append("namespace: '");
urlConfigurationJSSB.append(portletDisplay.getNamespace());
urlConfigurationJSSB.append("', portlet: '#p_p_id_");
urlConfigurationJSSB.append(portletDisplay.getId());
urlConfigurationJSSB.append("_', portletId: '");
urlConfigurationJSSB.append(portletDisplay.getId());

if (PropsValues.PORTLET_CONFIG_SHOW_PORTLET_ID) {
	urlConfigurationJSSB.append("', subTitle: '");
	urlConfigurationJSSB.append(portletDisplay.getId());
}

urlConfigurationJSSB.append("', title: '");
urlConfigurationJSSB.append(UnicodeLanguageUtil.get(request, "configuration"));
urlConfigurationJSSB.append("', uri: '");
urlConfigurationJSSB.append(HtmlUtil.escapeJS(portletDisplay.getURLConfiguration()));
urlConfigurationJSSB.append("'}); return false;");

portletDisplay.setURLConfigurationJS(urlConfigurationJSSB.toString());

// URL edit

PortletURLImpl urlEdit = new PortletURLImpl(request, portletDisplay.getId(), plid, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEdit()) {
	urlEdit.setWindowState(WindowState.NORMAL);
	urlEdit.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit() || portletDisplay.isStateMax()) {
		urlEdit.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEdit.setWindowState(WindowState.NORMAL);
	}

	urlEdit.setPortletMode(PortletMode.EDIT);
}

urlEdit.setEscapeXml(false);

portletDisplay.setURLEdit(urlEdit.toString());

// URL edit defaults

PortletURLImpl urlEditDefaults = new PortletURLImpl(request, portletDisplay.getId(), plid, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEditDefaults()) {
	urlEditDefaults.setWindowState(WindowState.NORMAL);
	urlEditDefaults.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit()) {
		urlEditDefaults.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEditDefaults.setWindowState(WindowState.NORMAL);
	}

	urlEditDefaults.setPortletMode(LiferayPortletMode.EDIT_DEFAULTS);
}

urlEditDefaults.setEscapeXml(false);

portletDisplay.setURLEditDefaults(urlEditDefaults.toString());

// URL edit guest

PortletURLImpl urlEditGuest = new PortletURLImpl(request, portletDisplay.getId(), plid, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeEditGuest()) {
	urlEditGuest.setWindowState(WindowState.NORMAL);
	urlEditGuest.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeEdit()) {
		urlEditGuest.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlEditGuest.setWindowState(WindowState.NORMAL);
	}

	urlEditGuest.setPortletMode(LiferayPortletMode.EDIT_GUEST);
}

urlEditGuest.setEscapeXml(false);

portletDisplay.setURLEditGuest(urlEditGuest.toString());

// URL export / import

PortletURLImpl urlExportImport = new PortletURLImpl(request, PortletKeys.EXPORT_IMPORT, plid, PortletRequest.RENDER_PHASE);

urlExportImport.setWindowState(LiferayWindowState.POP_UP);

urlExportImport.setParameter("mvcRenderCommandName", "exportImport");
urlExportImport.setParameter("redirect", currentURL);
urlExportImport.setParameter("returnToFullPageURL", currentURL);
urlExportImport.setParameter("portletResource", portletDisplay.getId());

urlExportImport.setEscapeXml(false);

portletDisplay.setURLExportImport(urlExportImport.toString() + "&" + PortalUtil.getPortletNamespace(PortletKeys.EXPORT_IMPORT));

// URL help

PortletURLImpl urlHelp = new PortletURLImpl(request, portletDisplay.getId(), plid, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModeHelp()) {
	urlHelp.setWindowState(WindowState.NORMAL);
	urlHelp.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isMaximizeHelp()) {
		urlHelp.setWindowState(WindowState.MAXIMIZED);
	}
	else {
		urlHelp.setWindowState(WindowState.NORMAL);
	}

	urlHelp.setPortletMode(PortletMode.HELP);
}

urlHelp.setEscapeXml(false);

portletDisplay.setURLHelp(urlHelp.toString());

// URL max

String lifecycle = PortletRequest.RENDER_PHASE;

if (!portletDisplay.isRestoreCurrentView()) {
	lifecycle = PortletRequest.ACTION_PHASE;
}

PortletURLImpl urlMax = new PortletURLImpl(request, portletDisplay.getId(), plid, lifecycle);

if (portletDisplay.isStateMax()) {
	urlMax.setWindowState(WindowState.NORMAL);
}
else {
	urlMax.setWindowState(WindowState.MAXIMIZED);
}

urlMax.setWindowStateRestoreCurrentView(true);

urlMax.setEscapeXml(false);

if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
	String portletNamespace = portletDisplay.getNamespace();

	Set<String> publicRenderParameterNames = SetUtil.fromEnumeration(portletConfig.getPublicRenderParameterNames());

	Map<String, String[]> renderParameters = RenderParametersPool.get(request, plid, portletDisplay.getId());

	for (Map.Entry<String, String[]> entry : renderParameters.entrySet()) {
		String key = entry.getKey();

		if (key.startsWith(portletNamespace) || publicRenderParameterNames.contains(key)) {
			if (key.startsWith(portletNamespace)) {
				key = key.substring(portletNamespace.length());
			}

			String[] values = entry.getValue();

			urlMax.setParameter(key, values);
		}
	}
}

portletDisplay.setURLMax(urlMax.toString());

// URL min

String urlMin = themeDisplay.getPathMain() + "/portal/update_layout?p_l_id=" + plid + "&p_p_id=" + portletDisplay.getId() + "&p_p_restore=" + portletDisplay.isStateMin() + "&p_v_l_s_g_id=" + themeDisplay.getSiteGroupId() + "&doAsUserId=" + HttpUtil.encodeURL(themeDisplay.getDoAsUserId()) + "&" + Constants.CMD + "=minimize&referer=" + HttpUtil.encodeURL(themeDisplay.getPathMain() + "/portal/layout?p_auth=" + AuthTokenUtil.getToken(request) + "&p_l_id=" + plid + "&doAsUserId=" + themeDisplay.getDoAsUserId()) + "&refresh=1";

portletDisplay.setURLMin(urlMin);

// URL portlet css

String urlPortletCss = "javascript:;";

portletDisplay.setURLPortletCss(urlPortletCss.toString());

// URL print

PortletURLImpl urlPrint = new PortletURLImpl(request, portletDisplay.getId(), plid, PortletRequest.RENDER_PHASE);

if (portletDisplay.isModePrint()) {
	urlPrint.setWindowState(WindowState.NORMAL);
	urlPrint.setPortletMode(PortletMode.VIEW);
}
else {
	if (portlet.isPopUpPrint()) {
		urlPrint.setWindowState(LiferayWindowState.POP_UP);
	}
	else {
		urlPrint.setWindowState(WindowState.NORMAL);
	}

	urlPrint.setPortletMode(LiferayPortletMode.PRINT);
}

urlPrint.setEscapeXml(false);

portletDisplay.setURLPrint(urlPrint.toString());

// URL refresh

String urlRefresh = "javascript:;";

portletDisplay.setURLRefresh(urlRefresh);

// URL staging

PortletURLImpl urlStaging = new PortletURLImpl(request, PortletKeys.EXPORT_IMPORT, plid, PortletRequest.RENDER_PHASE);

urlStaging.setWindowState(LiferayWindowState.POP_UP);

urlStaging.setParameter("mvcRenderCommandName", "publishPortlet");
urlStaging.setParameter("cmd", Constants.PUBLISH_TO_LIVE);
urlStaging.setParameter("redirect", currentURL);
urlStaging.setParameter("returnToFullPageURL", currentURL);
urlStaging.setParameter("portletResource", portletDisplay.getId());

urlStaging.setEscapeXml(false);

portletDisplay.setURLStaging(urlStaging.toString() + "&" + PortalUtil.getPortletNamespace(PortletKeys.EXPORT_IMPORT));

// URL back

String urlBack = null;

if (portletDisplay.isModeEdit()) {
	urlBack = urlEdit.toString();
}
else if (portletDisplay.isModeEditDefaults()) {
	urlBack = urlEditDefaults.toString();
}
else if (portletDisplay.isModeEditGuest()) {
	urlBack = urlEditGuest.toString();
}
else if (portletDisplay.isModeHelp()) {
	urlBack = urlHelp.toString();
}
else if (portletDisplay.isModePrint()) {
	urlBack = urlPrint.toString();
}
else if (portletDisplay.isStateMax()) {
	if (portletDisplay.getId().startsWith("WSRP_")) {
		urlBack = portletDisplay.getURLBack();
	}
	else {
		urlBack = ParamUtil.getString(request, "returnToFullPageURL");
		urlBack = PortalUtil.escapeRedirect(urlBack);
	}

	if (Validator.isNull(urlBack)) {
		urlBack = urlMax.toString();
	}
}

if (urlBack != null) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(urlBack);
}

if (themeDisplay.isWidget()) {
	portletDisplay.setShowBackIcon(false);
}

if (group.isControlPanel()) {
	portletDisplay.setShowBackIcon(false);
	portletDisplay.setShowConfigurationIcon(false);
	portletDisplay.setShowMaxIcon(false);
	portletDisplay.setShowMinIcon(false);
	portletDisplay.setShowMoveIcon(false);
	portletDisplay.setShowPortletCssIcon(false);

	Layout curLayout = PortletConfigurationLayoutUtil.getLayout(themeDisplay);

	if (!portlet.isPreferencesUniquePerLayout() && (portlet.getConfigurationActionInstance() != null) && PortletPermissionUtil.contains(permissionChecker, themeDisplay.getScopeGroupId(), curLayout, portlet, ActionKeys.CONFIGURATION)) {
		portletDisplay.setShowConfigurationIcon(true);
	}
}

boolean wsrp = ParamUtil.getBoolean(PortalUtil.getOriginalServletRequest(request), "wsrp");
%>

<c:choose>
	<c:when test="<%= wsrp %>">
		<liferay-wsrp-portlet>
			<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
		</liferay-wsrp-portlet>
	</c:when>
	<c:when test="<%= themeDisplay.isFacebook() %>">
		<%@ include file="/html/common/themes/portlet_facebook.jspf" %>
	</c:when>
	<c:when test="<%= themeDisplay.isStateExclusive() %>">
		<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
	</c:when>
	<c:when test="<%= themeDisplay.isStatePopUp() %>">
		<div class="portlet-body">
			<c:if test='<%= !tilesPortletContent.endsWith("/error.jsp") %>'>
				<%@ include file="/html/common/themes/portlet_messages.jspf" %>
			</c:if>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(tilesPortletContent) %>">
					<liferay-util:include page="<%= StrutsUtil.TEXT_HTML_DIR + tilesPortletContent %>" />
				</c:when>
				<c:otherwise>

					<%
					pageContext.getOut().print(renderRequest.getAttribute(WebKeys.PORTLET_CONTENT));
					%>

				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>

		<%
		Boolean renderPortletResource = (Boolean)request.getAttribute(WebKeys.RENDER_PORTLET_RESOURCE);

		boolean runtimePortlet = (renderPortletResource != null) && renderPortletResource.booleanValue();

		boolean freeformPortlet = themeDisplay.isFreeformLayout() && !runtimePortlet && !layoutTypePortlet.hasStateMax();

		String containerStyles = StringPool.BLANK;

		if (freeformPortlet) {
			Properties freeformStyleProps = PropertiesUtil.load(portletSetup.getValue("portlet-freeform-styles", StringPool.BLANK));

			containerStyles = "style=\"height: ".concat(GetterUtil.getString(HtmlUtil.escapeAttribute(freeformStyleProps.getProperty("height")), "300px")).concat("; overflow: auto;\"");
		}
		%>

		<liferay-theme:wrap-portlet page="portlet.jsp">
			<div class="<%= portletDisplay.isStateMin() ? "hide" : "" %> portlet-content-container" <%= containerStyles %>>
				<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
			</div>
		</liferay-theme:wrap-portlet>

		<c:if test="<%= freeformPortlet %>">
			<div class="portlet-resize-container">
				<div class="portlet-resize-handle"></div>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>