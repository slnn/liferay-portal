/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.display.context;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import java.util.Collection;
import org.osgi.framework.BundleContext;

/**
 * @author Iván Zaera
 */
public class BaseDisplayContextProvider<T extends DisplayContextFactory>
		implements DisplayContextProvider {

	public BaseDisplayContextProvider(Class<T> displayContextFactoryClass) {
		// We track by class name as the key, or a specific property
		_serviceTrackerMap = ServiceTrackerMapFactory.open(
				_bundleContext, displayContextFactoryClass, null);
	}

	public void destroy() {
		_serviceTrackerMap.close();
	}

	public Iterable<T> getDisplayContextFactories() {
		// ServiceTrackerMap.values() returns a Collection of the services
		return _serviceTrackerMap.values();
	}

	private final BundleContext _bundleContext = SystemBundleUtil.getBundleContext();
	private final ServiceTrackerMap<String, T> _serviceTrackerMap;
}