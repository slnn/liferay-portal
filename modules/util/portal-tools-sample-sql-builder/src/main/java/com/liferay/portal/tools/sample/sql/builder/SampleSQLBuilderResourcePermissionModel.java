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

package com.liferay.portal.tools.sample.sql.builder;

/**
 * @author Lily Chi
 */
public class SampleSQLBuilderResourcePermissionModel {

	public SampleSQLBuilderResourcePermissionModel(
		String mvccVersion, String ctCollectionId, String resourcePermissionId,
		String companyId, String name, String scope, String primKey,
		String primKeyId, String roleId, String ownerId, String actionIds,
		String viewActionId) {

		_mvccVersion = mvccVersion;
		_ctCollectionId = ctCollectionId;
		_resourcePermissionId = resourcePermissionId;
		_companyId = companyId;
		_name = name;
		_scope = scope;
		_primKey = primKey;
		_primKeyId = primKeyId;
		_roleId = roleId;
		_ownerId = ownerId;
		_actionIds = actionIds;
		_viewActionId = viewActionId;
	}

	public String getActionIds() {
		return _actionIds;
	}

	public String getCompanyId() {
		return _companyId;
	}

	public String getCtCollectionId() {
		return _ctCollectionId;
	}

	public String getMvccVersion() {
		return _mvccVersion;
	}

	public String getName() {
		return _name;
	}

	public String getOwnerId() {
		return _ownerId;
	}

	public String getPrimKey() {
		return _primKey;
	}

	public String getPrimKeyId() {
		return _primKeyId;
	}

	public String getResourcePermissionId() {
		return _resourcePermissionId;
	}

	public String getRoleId() {
		return _roleId;
	}

	public String getScope() {
		return _scope;
	}

	public String getViewActionId() {
		return _viewActionId;
	}

	public void setCompanyId(String companyId) {
		_companyId = companyId;
	}

	public void setResourcePermissionId(String resourcePermissionId) {
		_resourcePermissionId = resourcePermissionId;
	}

	public void setRoleId(String roleId) {
		_roleId = roleId;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(25);

		sb.append("mvccVersion:");
		sb.append(_mvccVersion);
		sb.append(",");
		sb.append("ctCollectionId:");
		sb.append(_ctCollectionId);
		sb.append(",");
		sb.append("resourcePermissionId:");
		sb.append(_resourcePermissionId);
		sb.append(",");
		sb.append("companyId:");
		sb.append(_companyId);
		sb.append(",");
		sb.append("name:");
		sb.append(_name);
		sb.append(",");
		sb.append("scope:");
		sb.append(_scope);
		sb.append(",");
		sb.append("primKey:");
		sb.append(_primKey);
		sb.append(",");
		sb.append("primKeyId:");
		sb.append(_primKeyId);
		sb.append(",");
		sb.append("roleId:");
		sb.append(_roleId);
		sb.append(",");
		sb.append("ownerId:");
		sb.append(_ownerId);
		sb.append(",");
		sb.append("actionIds:");
		sb.append(_actionIds);
		sb.append(",");
		sb.append("viewActionId:");
		sb.append(_viewActionId);

		return sb.toString();
	}

	private final String _actionIds;
	private String _companyId;
	private final String _ctCollectionId;
	private final String _mvccVersion;
	private final String _name;
	private final String _ownerId;
	private final String _primKey;
	private final String _primKeyId;
	private String _resourcePermissionId;
	private String _roleId;
	private final String _scope;
	private final String _viewActionId;

}