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

package com.liferay.portal.change.tracking;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

/**
 * @author Preston Crary
 */
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CTAdapter<T extends BaseModel<T>, C extends BaseModel<C>> {

	@Transactional(enabled = false)
	public C createContextModel(T model, long ctCollectionId);

	public T fetchByPrimaryKey(long primaryKey);

	public C fetchContextModel(long primaryKey, long ctCollectionId);

	public List<C> fetchContextModels(long[] primaryKeys, long ctCollectionId);

	public List<T> findByCTCollectionId(long ctCollectionId);

	@Transactional(enabled = false)
	public Class<T> getModelClass();

	@Transactional(enabled = false)
	public long getModelCTCollectionId(T model);

	@Transactional(enabled = false)
	public long getModelPrimaryKey(C ctContextModel);

	@Transactional(enabled = false)
	public long getPrimaryKey(T model);

	@Transactional(enabled = false)
	public String getPrimaryKeyColumnName();

	@Transactional(enabled = false)
	public void populateContextModel(T model, C ctContextModel);

	@Transactional(enabled = false)
	public void populateModel(T model, C ctContextModel);

	public void removeContext(long primaryKey, long ctCollectionId);

	public void removeContexts(T model);

	@Transactional(enabled = false)
	public void setContextModelCTCollectionId(
		C ctContextModel, long ctCollectionId);

	@Transactional(enabled = false)
	public void setModelCTCollectionId(T model, long ctCollectionId);

	public void updateContextModel(C ctContextModel);

	public void updateModel(T model);

}