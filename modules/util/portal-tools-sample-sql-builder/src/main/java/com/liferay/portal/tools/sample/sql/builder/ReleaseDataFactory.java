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

import com.liferay.portal.kernel.model.ReleaseModel;
import com.liferay.portal.model.impl.ReleaseModelImpl;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lily Chi
 */
public class ReleaseDataFactory extends BaseDataFactory {
	
	public ReleaseDataFactory(InitContext initContext) throws Exception {
		super(initContext);
	}

	public List<ReleaseModel> newReleaseModels() {
 		List<ReleaseModel> releases = new ArrayList<>(6);
 
 		releases.add(newReleaseModel("com.liferay.blogs.service", "1.1.0"));
 		releases.add(
 			newReleaseModel("com.liferay.dynamic.data.lists.service", "1.0.1"));
 		releases.add(
 			newReleaseModel(
 				"com.liferay.dynamic.data.mapping.service", "1.0.3"));
 		releases.add(
 			newReleaseModel("com.liferay.friendly.url.service", "1.0.0"));
 		releases.add(newReleaseModel("com.liferay.journal.service", "1.1.0"));
 		releases.add(newReleaseModel("com.liferay.wiki.service", "1.0.0"));
 
 		return releases;
 	}

	protected ReleaseModelImpl newReleaseModel(
 		String servletContextName, String schemaVersion) {
 
 		ReleaseModelImpl releaseModel = new ReleaseModelImpl();

		SimpleCounter counter = initContext.getCounter();
 
 		releaseModel.setReleaseId(counter.get());
 		releaseModel.setCreateDate(new Date());
 		releaseModel.setModifiedDate(new Date());
 		releaseModel.setServletContextName(servletContextName);
 		releaseModel.setSchemaVersion(schemaVersion);
 		releaseModel.setBuildDate(new Date());
 		releaseModel.setVerified(true);
 
 		return releaseModel;
 	}
 
	
}