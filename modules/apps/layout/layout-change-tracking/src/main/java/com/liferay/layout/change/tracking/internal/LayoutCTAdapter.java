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

package com.liferay.layout.change.tracking.internal;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.change.tracking.CTAdapter;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutCT;
import com.liferay.portal.kernel.service.persistence.LayoutCTPK;
import com.liferay.portal.kernel.service.persistence.LayoutCTPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(immediate = true, service = AopService.class)
public class LayoutCTAdapter
	implements AopService, CTAdapter<Layout, LayoutCT> {

	@Override
	public LayoutCT createContextModel(Layout layout, long ctCollectionId) {
		return _layoutCTPersistence.create(
			new LayoutCTPK(layout.getPlid(), ctCollectionId));
	}

	@Override
	public Layout fetchByPrimaryKey(long primaryKey) {
		return _layoutPersistence.fetchByPrimaryKey(primaryKey);
	}

	@Override
	public LayoutCT fetchContextModel(long primaryKey, long ctCollectionId) {
		return _layoutCTPersistence.fetchByPrimaryKey(
			new LayoutCTPK(primaryKey, ctCollectionId));
	}

	@Override
	public List<LayoutCT> fetchContextModels(
		long[] primaryKeys, long ctCollectionId) {

		return _layoutCTPersistence.findByP_CT(primaryKeys, ctCollectionId);
	}

	@Override
	public List<Layout> findByCTCollectionId(long ctCollectionId) {
		return _layoutPersistence.findByCTCollectionId(ctCollectionId);
	}

	@Override
	public Class<Layout> getModelClass() {
		return Layout.class;
	}

	@Override
	public long getModelCTCollectionId(Layout layout) {
		return layout.getCtCollectionId();
	}

	@Override
	public long getModelPrimaryKey(LayoutCT layoutCT) {
		return layoutCT.getPlid();
	}

	@Override
	public long getPrimaryKey(Layout layout) {
		return layout.getPlid();
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return "plid";
	}

	@Override
	public void populateContextModel(Layout layout, LayoutCT ctContextModel) {
		ctContextModel.setTypeSettings(layout.getTypeSettings());
	}

	@Override
	public void populateModel(Layout model, LayoutCT layoutCT) {
		model.setTypeSettings(layoutCT.getTypeSettings());
	}

	@Override
	public void removeContext(long primaryKey, long ctCollectionId) {
		LayoutCT layoutCT = _layoutCTPersistence.fetchByPrimaryKey(
			new LayoutCTPK(primaryKey, ctCollectionId));

		if (layoutCT != null) {
			_layoutCTPersistence.remove(layoutCT);
		}
	}

	@Override
	public void removeContexts(Layout layout) {
		_layoutCTPersistence.removeByPlid(layout.getPlid());
	}

	@Override
	public void setContextModelCTCollectionId(
		LayoutCT layoutCT, long ctCollectionId) {

		layoutCT.setCtCollectionId(ctCollectionId);
	}

	@Override
	public void setModelCTCollectionId(Layout layout, long ctCollectionId) {
		layout.setCtCollectionId(ctCollectionId);
	}

	@Override
	public void updateContextModel(LayoutCT layoutCT) {
		_layoutCTPersistence.update(layoutCT);
	}

	@Override
	public void updateModel(Layout layout) {
		_layoutPersistence.update(layout);
	}

	@Reference
	private LayoutCTPersistence _layoutCTPersistence;

	@Reference
	private LayoutPersistence _layoutPersistence;

}