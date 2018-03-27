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

package com.liferay.dynamic.insert.taglib.internal.util;

import com.liferay.dynamic.insert.taglib.DynamicInsert;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringBundler;

import javax.servlet.jsp.PageContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Matthew Tambara
 */
@Component(immediate = true)
public class DynamicInsertUtil {

	public static StringBundler insert(
		String name, PageContext pageContext, StringBundler sb) {

		for (DynamicInsert dynamicInsert : _getDynamicInserts(name)) {
			sb = dynamicInsert.insert(sb, pageContext);
		}

		return sb;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private static DynamicInsert[] _getDynamicInserts(String name) {
		BundleContext bundleContext = _bundleContext;

		DynamicInsert[] dynamicInserts = new DynamicInsert[0];

		try {
			StringBundler sb = new StringBundler(5);

			sb.append("(&(objectClass=");
			sb.append(DynamicInsert.class.getName());
			sb.append(")(name=");
			sb.append(name);
			sb.append("))");

			ServiceTracker<DynamicInsert, DynamicInsert> serviceTracker =
				new ServiceTracker<>(
					bundleContext, bundleContext.createFilter(sb.toString()),
					null);

			serviceTracker.open();

			dynamicInserts = serviceTracker.getServices(dynamicInserts);

			ArrayUtil.reverse(dynamicInserts);
		}
		catch (InvalidSyntaxException ise) {
			_log.error("Invalid service filter {name=" + name + "}", ise);
		}

		return dynamicInserts;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicInsertUtil.class);

	private static BundleContext _bundleContext;

}