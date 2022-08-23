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

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDisplayLayoutLocalService;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryPersistence;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(enabled = false, immediate = true, service = ModelListener.class)
public class AssetCategoryModelListener
	extends BaseModelListener<AssetCategory> {

	@Override
	public void onAfterCreate(AssetCategory assetCategory)
		throws ModelListenerException {

		try {
			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(),
				_getUniqueUrlTitles(assetCategory), new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	@Override
	public void onBeforeRemove(AssetCategory assetCategory) {
		try {
			List<CPAttachmentFileEntry> cpAttachmentFileEntries =
				_cpAttachmentFileEntryPersistence.findByC_C(
					_classNameLocalService.getClassNameId(
						AssetCategory.class.getName()),
					assetCategory.getCategoryId());

			for (CPAttachmentFileEntry cpAttachmentFileEntry :
					cpAttachmentFileEntries) {

				_cpAttachmentFileEntryLocalService.deleteCPAttachmentFileEntry(
					_copyCPDefinitionAndPrepareCPAttachmentFileEntry(
						cpAttachmentFileEntry));
			}

			_cpDisplayLayoutLocalService.deleteCPDisplayLayouts(
				AssetCategory.class, assetCategory.getCategoryId());

			_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
				assetCategory.getGroupId(), AssetCategory.class,
				assetCategory.getCategoryId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private CPAttachmentFileEntry
			_copyCPDefinitionAndPrepareCPAttachmentFileEntry(
				CPAttachmentFileEntry cpAttachmentFileEntry)
		throws PortalException {

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		if ((cpAttachmentFileEntry.getClassNameId() ==
				cpDefinitionClassNameId) &&
			_cpDefinitionLocalService.isVersionable(
				cpAttachmentFileEntry.getClassPK())) {

			CPDefinition newCPDefinition =
				_cpDefinitionLocalService.copyCPDefinition(
					cpAttachmentFileEntry.getClassPK());

			if (cpAttachmentFileEntry.isCDNEnabled()) {
				cpAttachmentFileEntry =
					_cpAttachmentFileEntryPersistence.findByC_C_C_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getCDNURL(), null);
			}
			else {
				cpAttachmentFileEntry =
					_cpAttachmentFileEntryPersistence.findByC_C_F_First(
						cpDefinitionClassNameId,
						newCPDefinition.getCPDefinitionId(),
						cpAttachmentFileEntry.getFileEntryId(), null);
			}
		}

		return cpAttachmentFileEntry;
	}

	private Map<String, String> _getUniqueUrlTitles(AssetCategory assetCategory)
		throws PortalException {

		Map<String, String> urlTitleMap = new HashMap<>();

		Map<Locale, String> titleMap = assetCategory.getTitleMap();

		for (Map.Entry<Locale, String> titleEntry : titleMap.entrySet()) {
			String urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
				assetCategory.getGroupId(),
				_portal.getClassNameId(AssetCategory.class),
				assetCategory.getCategoryId(), titleEntry.getValue());

			urlTitleMap.put(
				LocaleUtil.toLanguageId(titleEntry.getKey()), urlTitle);
		}

		return urlTitleMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryModelListener.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private CPAttachmentFileEntryPersistence _cpAttachmentFileEntryPersistence;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPDisplayLayoutLocalService _cpDisplayLayoutLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Portal _portal;

}