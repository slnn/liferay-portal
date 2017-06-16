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

package com.liferay.wiki.internal.upgrade.v1_1_0.util;

import java.sql.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * @author	  Preston Crary
 * @generated
 */
public class WikiPageResourceTable {

	public static final String TABLE_NAME = "WikiPageResource";

	public static final Object[][] TABLE_COLUMNS = {
		{"uuid_", Types.VARCHAR},
		{"resourcePrimKey", Types.BIGINT},
		{"groupId", Types.BIGINT},
		{"companyId", Types.BIGINT},
		{"nodeId", Types.BIGINT},
		{"title", Types.VARCHAR},
		{"headPageId", Types.BIGINT}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP = new HashMap<String, Integer>();

static {
TABLE_COLUMNS_MAP.put("uuid_", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("resourcePrimKey", Types.BIGINT);

TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("nodeId", Types.BIGINT);

TABLE_COLUMNS_MAP.put("title", Types.VARCHAR);

TABLE_COLUMNS_MAP.put("headPageId", Types.BIGINT);

}
	public static final String TABLE_SQL_CREATE = "create table WikiPageResource (uuid_ VARCHAR(75) null,resourcePrimKey LONG not null primary key,groupId LONG,companyId LONG,nodeId LONG,title VARCHAR(255) null,headPageId LONG)";

	public static final String TABLE_SQL_DROP = "drop table WikiPageResource";

	public static final String[] TABLE_SQL_ADD_INDEXES = {
		"create unique index IX_21277664 on WikiPageResource (nodeId, title[$COLUMN_LENGTH:255$])",
		"create index IX_13319367 on WikiPageResource (uuid_[$COLUMN_LENGTH:75$], companyId)",
		"create unique index IX_F705C7A9 on WikiPageResource (uuid_[$COLUMN_LENGTH:75$], groupId)"
	};

}