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

package com.liferay.wiki.internal.upgrade.v1_1_0;

import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.wiki.internal.upgrade.v1_1_0.util.WikiPageResourceTable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Preston Crary
 */
public class UpgradeWikiPageResource extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		alter(
			WikiPageResourceTable.class,
			new AlterTableAddColumn("headPageId LONG"));

		String selectSQL = SQLTransformer.transform(
			"select pageId, resourcePrimKey from WikiPage where head = " +
				"[$TRUE$] order by resourcePrimKey ASC, version DESC");

		long previousResourcePrimKey = -1;

		try (PreparedStatement ps1 = connection.prepareStatement(selectSQL);
			ResultSet rs = ps1.executeQuery();
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update WikiPageResource set headPageId = ? where " +
						"resourcePrimKey = ?")) {

			while (rs.next()) {
				long resourcePrimKey = rs.getLong(2);

				if (previousResourcePrimKey != resourcePrimKey) {
					ps2.setLong(1, rs.getLong(1));
					ps2.setLong(2, resourcePrimKey);

					ps2.addBatch();
				}

				previousResourcePrimKey = resourcePrimKey;
			}

			ps2.executeBatch();
		}
	}

}