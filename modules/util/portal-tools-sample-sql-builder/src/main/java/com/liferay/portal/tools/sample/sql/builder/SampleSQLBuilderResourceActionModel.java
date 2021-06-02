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
public class SampleSQLBuilderResourceActionModel {

	public SampleSQLBuilderResourceActionModel(
		String mvccVersion, String resourceActionId, String name,
		String actionId, String bitwiseValue) {

		_mvccVersion = mvccVersion;
		_resourceActionId = resourceActionId;
		_name = name;
		_actionId = actionId;
		_bitwiseValue = bitwiseValue;
	}

	public String getActionId() {
		return _actionId;
	}

	public String getBitwiseValue() {
		return _bitwiseValue;
	}

	public String getMvccVersion() {
		return _mvccVersion;
	}

	public String getName() {
		return _name;
	}

	public String getResourceActionId() {
		return _resourceActionId;
	}

	public void setResourceActionId(String resourceActionId) {
		_resourceActionId = resourceActionId;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(14);

		sb.append("mvccVersion:");
		sb.append(_mvccVersion);
		sb.append(",");
		sb.append("resourceActionId:");
		sb.append(_resourceActionId);
		sb.append(",");
		sb.append("name:");
		sb.append(_name);
		sb.append(",");
		sb.append("actionId:");
		sb.append(_actionId);
		sb.append(",");
		sb.append("bitwiseValue:");
		sb.append(_bitwiseValue);

		return sb.toString();
	}

	private final String _actionId;
	private final String _bitwiseValue;
	private final String _mvccVersion;
	private final String _name;
	private String _resourceActionId;

}