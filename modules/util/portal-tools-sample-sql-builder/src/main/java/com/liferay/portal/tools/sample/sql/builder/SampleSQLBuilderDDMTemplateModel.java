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
public class SampleSQLBuilderDDMTemplateModel {

	public SampleSQLBuilderDDMTemplateModel(
		String className, String templateKey, String name, String script) {

		_className = className;
		_templateKey = templateKey;
		_name = name;
		_script = script;
	}

	public String getClassName() {
		return _className;
	}

	public String getName() {
		return _name;
	}

	public String getScript() {
		return _script;
	}

	public String getTemplateKey() {
		return _templateKey;
	}

	private final String _className;
	private final String _name;
	private final String _script;
	private final String _templateKey;

}