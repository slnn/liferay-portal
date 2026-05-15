<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ProductionReadinessDisplayContext productionReadinessDisplayContext = new ProductionReadinessDisplayContext(renderRequest);

String status = ParamUtil.getString(request, "status");
%>

<div class="container-fluid container-fluid-max-xl mt-3">
	<div class="row">
		<div class="col-md-4">
			<div class="card">
				<div class="card-body">
					<div class="card-title text-success">Passed</div>
					<h3 class="card-text"><%= productionReadinessDisplayContext.getPassedCount() %></h3>
				</div>
			</div>
		</div>
		<div class="col-md-4">
			<div class="card">
				<div class="card-body">
					<div class="card-title text-danger">Failed</div>
					<h3 class="card-text"><%= productionReadinessDisplayContext.getFailedCount() %></h3>
				</div>
			</div>
		</div>
		<div class="col-md-4">
			<div class="card">
				<div class="card-body">
					<div class="card-title text-secondary">Ignored</div>
					<h3 class="card-text"><%= productionReadinessDisplayContext.getIgnoredCount() %></h3>
				</div>
			</div>
		</div>
	</div>

	<div class="nav-filter-bar mt-4 mb-4">
		<portlet:renderURL var="allURL">
			<portlet:param name="mvcRenderCommandName" value="/server_admin/view" />
			<portlet:param name="tabs1" value="production-readiness" />
			<portlet:param name="status" value="" />
		</portlet:renderURL>

		<portlet:renderURL var="passedURL">
			<portlet:param name="mvcRenderCommandName" value="/server_admin/view" />
			<portlet:param name="tabs1" value="production-readiness" />
			<portlet:param name="status" value="passed" />
		</portlet:renderURL>

		<portlet:renderURL var="failedURL">
			<portlet:param name="mvcRenderCommandName" value="/server_admin/view" />
			<portlet:param name="tabs1" value="production-readiness" />
			<portlet:param name="status" value="failed" />
		</portlet:renderURL>

		<portlet:renderURL var="ignoredURL">
			<portlet:param name="mvcRenderCommandName" value="/server_admin/view" />
			<portlet:param name="tabs1" value="production-readiness" />
			<portlet:param name="status" value="ignored" />
		</portlet:renderURL>

		<ul class="nav nav-underline">
			<li class="nav-item">
				<a class="nav-link <%= status.isEmpty() ? "active" : "" %>" href="<%= allURL %>">All</a>
			</li>
			<li class="nav-item">
				<a class="nav-link <%= status.equals("passed") ? "active" : "" %>" href="<%= passedURL %>">Passed</a>
			</li>
			<li class="nav-item">
				<a class="nav-link <%= status.equals("failed") ? "active" : "" %>" href="<%= failedURL %>">Failed</a>
			</li>
			<li class="nav-item">
				<a class="nav-link <%= status.equals("ignored") ? "active" : "" %>" href="<%= ignoredURL %>">Ignored</a>
			</li>
		</ul>
	</div>

	<table class="table table-autofit">
		<thead>
			<tr>
				<th class="table-cell-expand">Property</th>
				<th>Current</th>
				<th>Recommended</th>
				<th>Status</th>
				<th>Severity</th>
				<th class="table-cell-expand">Message</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="<%= productionReadinessDisplayContext.getRuleResults() %>" var="ruleResult">
				<tr>
					<td class="table-cell-expand">
						<strong><liferay-ui:message key="${ruleResult.rule.key}" /></strong>
						<br/>
						<small class="text-muted">${ruleResult.rule.category}</small>
					</td>
					<td>
						<span class="text-truncate" title="${ruleResult.result.currentValue}">${ruleResult.result.currentValue}</span>
					</td>
					<td>
						<span class="text-truncate" title="${ruleResult.result.recommendedValue}">${ruleResult.result.recommendedValue}</span>
					</td>
					<td>
						<clay:label
							displayType="${ruleResult.result.status == 'PASS' ? 'success' : 'danger'}"
							label="${ruleResult.result.status}"
						/>
					</td>
					<td>
						${ruleResult.result.severity}
					</td>
					<td class="table-cell-expand">
						<c:forEach items="${ruleResult.result.messageParameters}" var="arg">
							${arg}<br/>
						</c:forEach>
					</td>
					<td>
						<portlet:actionURL name="/server_admin/toggle_production_readiness_ignore" var="toggleIgnoreURL">
							<portlet:param name="ruleKey" value="${ruleResult.rule.key}" />
							<portlet:param name="ignore" value="${!ruleResult.ignored}" />
							<portlet:param name="redirect" value="${currentURL}" />
						</portlet:actionURL>

						<clay:button
							displayType="secondary"
							href="${toggleIgnoreURL}"
							label='${ruleResult.ignored ? "Unignore" : "Ignore"}'
						/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
