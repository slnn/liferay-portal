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

package com.liferay.commerce.product.internal.helper;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.commerce.account.model.CommerceAccountGroupRel;
import com.liferay.commerce.account.service.CommerceAccountGroupRelLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.configuration.CProductVersionConfiguration;
import com.liferay.commerce.product.exception.CPDefinitionIgnoreSKUCombinationsException;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPDisplayLayout;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.model.impl.CPDefinitionModelImpl;
import com.liferay.commerce.product.service.CPInstanceOptionValueRelLocalService;
import com.liferay.commerce.product.service.CProductLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalService;
import com.liferay.commerce.product.service.persistence.CPAttachmentFileEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLinkPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionSpecificationOptionValuePersistence;
import com.liferay.commerce.product.service.persistence.CPDisplayLayoutPersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeServicesTracker;
import com.liferay.commerce.product.util.CPVersionContributor;
import com.liferay.commerce.product.util.CPVersionContributorRegistryUtil;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.settings.SystemSettingsLocator;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.uuid.PortalUUID;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lily Chi
 */
@Component(enabled = false, service = CPDefinitionLocalServiceHelper.class)
public class CPDefinitionLocalServiceHelper {

	public CPDefinition copyCPDefinition(long cpDefinitionId)
		throws PortalException {

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		return copyCPDefinition(
			cpDefinitionId, cpDefinition.getGroupId(),
			WorkflowConstants.STATUS_DRAFT);
	}

	public CPDefinition copyCPDefinition(
			long cpDefinitionId, long groupId, int status)
		throws PortalException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		User user = _userLocalService.getUser(serviceContext.getUserId());

		CPDefinition originalCPDefinition =
			_cpDefinitionPersistence.findByPrimaryKey(cpDefinitionId);

		CPDefinition newCPDefinition =
			(CPDefinition)originalCPDefinition.clone();

		newCPDefinition.setUuid(_portalUUID.generate());

		long newCPDefinitionId = _counterLocalService.increment();

		newCPDefinition.setCPDefinitionId(newCPDefinitionId);

		newCPDefinition.setGroupId(groupId);
		newCPDefinition.setUserId(user.getUserId());
		newCPDefinition.setUserName(user.getFullName());

		CProduct originalCProduct = originalCPDefinition.getCProduct();

		if (isVersionable(originalCProduct.getPublishedCPDefinitionId()) &&
			((status != originalCPDefinition.getStatus()) ||
			 (status == WorkflowConstants.STATUS_APPROVED))) {

			CProduct cProduct = _cProductPersistence.findByPrimaryKey(
				originalCPDefinition.getCProductId());

			cProduct.setLatestVersion(cProduct.getLatestVersion() + 1);

			cProduct = _cProductPersistence.update(cProduct);

			newCPDefinition.setVersion(cProduct.getLatestVersion());

			if (status == WorkflowConstants.STATUS_APPROVED) {
				CPDefinition publishedCPDefinition =
					_cpDefinitionPersistence.findByPrimaryKey(
						originalCProduct.getPublishedCPDefinitionId());

				publishedCPDefinition.setPublished(false);

				publishedCPDefinition = _cpDefinitionPersistence.update(
					publishedCPDefinition);

				long publishedCPDefinitionId =
					newCPDefinition.getCPDefinitionId();

				CProduct publishedCProduct =
					_cProductPersistence.findByPrimaryKey(
						publishedCPDefinition.getCProductId());

				long originalPublishedCPDefinitionId =
					publishedCProduct.getPublishedCPDefinitionId();

				if (originalPublishedCPDefinitionId !=
						publishedCPDefinitionId) {

					publishedCProduct.setPublishedCPDefinitionId(
						publishedCPDefinitionId);

					_cProductPersistence.update(cProduct);

					_cpDefinitionIndexHelper.reindexCPDefinition(
						originalPublishedCPDefinitionId);
					_cpDefinitionIndexHelper.reindexCPDefinition(
						publishedCPDefinitionId);
				}

				long cProductId = publishedCPDefinition.getCProductId();

				TransactionCommitCallbackUtil.registerCallback(
					() -> {
						maintainVersionThreshold(cProductId);

						return null;
					});
			}
		}
		else {
			CProduct newCProduct = (CProduct)originalCProduct.clone();

			newCProduct.setUuid(_portalUUID.generate());

			long cProductId = _counterLocalService.increment();

			newCProduct.setExternalReferenceCode(String.valueOf(cProductId));
			newCProduct.setCProductId(cProductId);

			newCProduct.setUserId(user.getUserId());
			newCProduct.setUserName(user.getFullName());
			newCProduct.setPublishedCPDefinitionId(newCPDefinitionId);

			newCPDefinition.setCProductId(newCProduct.getCProductId());

			_cProductPersistence.update(newCProduct);
		}

		newCPDefinition.setStatus(status);

		newCPDefinition = _cpDefinitionPersistence.update(newCPDefinition);

		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			cpDefinitionClassNameId, cpDefinitionId);

		if (assetEntry != null) {
			AssetEntry newAssetEntry = (AssetEntry)assetEntry.clone();

			newAssetEntry.setEntryId(_counterLocalService.increment());
			newAssetEntry.setClassPK(newCPDefinitionId);

			_assetEntryLocalService.addAssetEntry(newAssetEntry);
		}

		List<CPDefinitionLocalization> cpDefinitionLocalizations =
			_cpDefinitionLocalizationPersistence.findByCPDefinitionId(
				cpDefinitionId);

		for (CPDefinitionLocalization cpDefinitionLocalization :
				cpDefinitionLocalizations) {

			CPDefinitionLocalization newCPDefinitionLocalization =
				(CPDefinitionLocalization)cpDefinitionLocalization.clone();

			newCPDefinitionLocalization.setCpDefinitionLocalizationId(
				_counterLocalService.increment());
			newCPDefinitionLocalization.setCPDefinitionId(newCPDefinitionId);

			if (originalCPDefinition.getCProductId() !=
					newCPDefinition.getCProductId()) {

				newCPDefinitionLocalization.setName(
					LanguageUtil.format(
						LocaleUtil.fromLanguageId(
							newCPDefinitionLocalization.getLanguageId()),
						"copy-of-x", newCPDefinitionLocalization.getName()));
			}

			_cpDefinitionLocalizationPersistence.update(
				newCPDefinitionLocalization);
		}

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryPersistence.findByC_C(
				cpDefinitionClassNameId, cpDefinitionId);

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			CPAttachmentFileEntry newCPAttachmentFileEntry =
				(CPAttachmentFileEntry)cpAttachmentFileEntry.clone();

			newCPAttachmentFileEntry.setUuid(_portalUUID.generate());

			long cpAttachmentFileEntryId = _counterLocalService.increment();

			newCPAttachmentFileEntry.setExternalReferenceCode(
				String.valueOf(cpAttachmentFileEntryId));
			newCPAttachmentFileEntry.setCPAttachmentFileEntryId(
				cpAttachmentFileEntryId);

			newCPAttachmentFileEntry.setClassPK(newCPDefinitionId);

			_cpAttachmentFileEntryPersistence.update(newCPAttachmentFileEntry);
		}

		List<CPDefinitionLink> cpDefinitionLinks =
			_cpDefinitionLinkPersistence.findByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			CPDefinitionLink newCPDefinitionLink =
				(CPDefinitionLink)cpDefinitionLink.clone();

			newCPDefinitionLink.setUuid(_portalUUID.generate());
			newCPDefinitionLink.setCPDefinitionLinkId(
				_counterLocalService.increment());
			newCPDefinitionLink.setCPDefinitionId(newCPDefinitionId);

			_cpDefinitionLinkPersistence.update(newCPDefinitionLink);
		}

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelPersistence.findByCPDefinitionId(
				cpDefinitionId);

		List<CPDefinitionOptionRel> newCPDefinitionOptionRels = new ArrayList<>(
			cpDefinitionOptionRels.size());

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			CPDefinitionOptionRel newCPDefinitionOptionRel =
				(CPDefinitionOptionRel)cpDefinitionOptionRel.clone();

			newCPDefinitionOptionRel.setUuid(_portalUUID.generate());

			long newCPDefinitionOptionRelId = _counterLocalService.increment();

			newCPDefinitionOptionRel.setCPDefinitionOptionRelId(
				newCPDefinitionOptionRelId);

			newCPDefinitionOptionRel.setCPDefinitionId(newCPDefinitionId);

			newCPDefinitionOptionRel = _cpDefinitionOptionRelPersistence.update(
				newCPDefinitionOptionRel);

			newCPDefinitionOptionRels.add(newCPDefinitionOptionRel);

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_cpDefinitionOptionValueRelPersistence.
					findByCPDefinitionOptionRelId(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionValueRels) {

				CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
					(CPDefinitionOptionValueRel)
						cpDefinitionOptionValueRel.clone();

				newCPDefinitionOptionValueRel.setUuid(_portalUUID.generate());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionValueRelId(
					_counterLocalService.increment());
				newCPDefinitionOptionValueRel.setCPDefinitionOptionRelId(
					newCPDefinitionOptionRelId);

				_cpDefinitionOptionValueRelPersistence.update(
					newCPDefinitionOptionValueRel);
			}

			_reindexCPDefinitionOptionValueRels(newCPDefinitionOptionRel);
		}

		_reindexCPDefinitionOptionRels(newCPDefinition);

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValuePersistence.
					findByCPDefinitionId(cpDefinitionId);

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			CPDefinitionSpecificationOptionValue
				newCPDefinitionSpecificationOptionValue =
					(CPDefinitionSpecificationOptionValue)
						cpDefinitionSpecificationOptionValue.clone();

			newCPDefinitionSpecificationOptionValue.setUuid(
				_portalUUID.generate());
			newCPDefinitionSpecificationOptionValue.
				setCPDefinitionSpecificationOptionValueId(
					_counterLocalService.increment());
			newCPDefinitionSpecificationOptionValue.setCPDefinitionId(
				newCPDefinitionId);

			_cpDefinitionSpecificationOptionValuePersistence.update(
				newCPDefinitionSpecificationOptionValue);
		}

		List<CPDisplayLayout> cpDisplayLayouts =
			_cpDisplayLayoutPersistence.findByC_C(
				cpDefinitionClassNameId, cpDefinitionId);

		for (CPDisplayLayout cpDisplayLayout : cpDisplayLayouts) {
			CPDisplayLayout newCPDisplayLayout =
				(CPDisplayLayout)cpDisplayLayout.clone();

			newCPDisplayLayout.setUuid(_portalUUID.generate());
			newCPDisplayLayout.setCPDisplayLayoutId(
				_counterLocalService.increment());
			newCPDisplayLayout.setClassPK(newCPDefinitionId);

			_cpDisplayLayoutPersistence.update(newCPDisplayLayout);
		}

		List<CPInstance> cpInstances =
			_cpInstancePersistence.findByCPDefinitionId(cpDefinitionId);

		for (CPInstance cpInstance : cpInstances) {
			CPInstance newCPInstance = (CPInstance)cpInstance.clone();

			newCPInstance.setUuid(_portalUUID.generate());

			long cpInstanceId = _counterLocalService.increment();

			newCPInstance.setExternalReferenceCode(
				String.valueOf(cpInstanceId));
			newCPInstance.setCPInstanceId(cpInstanceId);

			newCPInstance.setCPDefinitionId(newCPDefinitionId);
			newCPInstance.setCPInstanceUuid(_portalUUID.generate());

			List<CPInstanceOptionValueRel> cpInstanceOptionValueRels =
				_cpInstanceOptionValueRelPersistence.findByCPInstanceId(
					cpInstance.getCPInstanceId());

			for (CPInstanceOptionValueRel cpInstanceOptionValueRel :
					cpInstanceOptionValueRels) {

				CPInstanceOptionValueRel newCPInstanceOptionValueRel =
					(CPInstanceOptionValueRel)cpInstanceOptionValueRel.clone();

				newCPInstanceOptionValueRel.setUuid(_portalUUID.generate());
				newCPInstanceOptionValueRel.setCPInstanceOptionValueRelId(
					_counterLocalService.increment());
				newCPInstanceOptionValueRel.setCPInstanceId(
					newCPInstance.getCPInstanceId());

				CPDefinitionOptionRel cpDefinitionOptionRel =
					_cpDefinitionOptionRelPersistence.findByPrimaryKey(
						cpInstanceOptionValueRel.getCPDefinitionOptionRelId());

				Stream<CPDefinitionOptionRel> cpDefinitionOptionRelStream =
					newCPDefinitionOptionRels.stream();

				Optional<CPDefinitionOptionRel> cpDefinitionOptionRelOptional =
					cpDefinitionOptionRelStream.filter(
						curCPDefinitionOptionRel ->
							cpDefinitionOptionRel.getCPOptionId() ==
								curCPDefinitionOptionRel.getCPOptionId()
					).findFirst();

				if (cpDefinitionOptionRelOptional.isPresent()) {
					CPDefinitionOptionRel newCPDefinitionOptionRel =
						cpDefinitionOptionRelOptional.get();

					long cpDefinitionOptionRelId =
						newCPDefinitionOptionRel.getCPDefinitionOptionRelId();

					newCPInstanceOptionValueRel.setCPDefinitionOptionRelId(
						cpDefinitionOptionRelId);

					List<CPDefinitionOptionValueRel>
						cpDefinitionOptionValueRels =
							cpDefinitionOptionRel.
								getCPDefinitionOptionValueRels();

					Stream<CPDefinitionOptionValueRel>
						cpDefinitionOptionValueRelsStream =
							cpDefinitionOptionValueRels.stream();

					Optional<CPDefinitionOptionValueRel>
						cpDefinitionOptionValueRelOptional =
							cpDefinitionOptionValueRelsStream.filter(
								curCPDefinitionOptionValueRel ->
									cpDefinitionOptionRelId ==
										curCPDefinitionOptionValueRel.
											getCPDefinitionOptionRelId()
							).findFirst();

					if (cpDefinitionOptionValueRelOptional.isPresent()) {
						CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
							cpDefinitionOptionValueRelOptional.get();

						newCPInstanceOptionValueRel.
							setCPInstanceOptionValueRelId(
								cpDefinitionOptionValueRel.
									getCPDefinitionOptionValueRelId());
					}
				}

				_cpInstanceOptionValueRelLocalService.
					updateCPInstanceOptionValueRel(newCPInstanceOptionValueRel);
			}

			_updateCommercePriceEntry(
				newCPInstance, CommercePriceListConstants.TYPE_PRICE_LIST,
				newCPInstance.getPrice(), serviceContext);
			_updateCommercePriceEntry(
				newCPInstance, CommercePriceListConstants.TYPE_PROMOTION,
				newCPInstance.getPromoPrice(), serviceContext);

			_cpInstancePersistence.update(newCPInstance);
		}

		for (CommerceChannelRel commerceChannelRel :
				_commerceChannelRelLocalService.getCommerceChannelRels(
					originalCPDefinition.getModelClassName(),
					originalCPDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			_commerceChannelRelLocalService.addCommerceChannelRel(
				newCPDefinition.getModelClassName(), newCPDefinitionId,
				commerceChannelRel.getCommerceChannelId(), serviceContext);
		}

		for (CommerceAccountGroupRel commerceAccountGroupRel :
				_commerceAccountGroupRelLocalService.
					getCommerceAccountGroupRels(
						originalCPDefinition.getModelClassName(),
						originalCPDefinition.getCPDefinitionId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			_commerceAccountGroupRelLocalService.addCommerceAccountGroupRel(
				newCPDefinition.getModelClassName(), newCPDefinitionId,
				commerceAccountGroupRel.getCommerceAccountGroupId(),
				serviceContext);
		}

		List<CPVersionContributor> cpVersionContributors =
			CPVersionContributorRegistryUtil.getCPVersionContributors();

		for (CPVersionContributor cpVersionContributor :
				cpVersionContributors) {

			cpVersionContributor.onUpdate(cpDefinitionId, newCPDefinitionId);
		}

		return newCPDefinition;
	}

	public CPDefinition deleteCPDefinition(CPDefinition cpDefinition)
		throws PortalException {

		// Commerce product

		int cpDefinitionsCount = _cpDefinitionPersistence.countByC_S(
			cpDefinition.getCProductId(), WorkflowConstants.STATUS_ANY);

		if (!isVersioningEnabled() ||
			(isVersioningEnabled() && (cpDefinitionsCount == 1))) {

			_cProductLocalService.deleteCProduct(cpDefinition.getCProductId());
		}

		// Commerce product definition specification option values

		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues =
				_cpDefinitionSpecificationOptionValuePersistence.
					findByCPDefinitionId(
						cpDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS, null);

		for (CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue :
					cpDefinitionSpecificationOptionValues) {

			if (isVersionable(
					cpDefinitionSpecificationOptionValue.getCPDefinitionId())) {

				try {
					CPDefinition newCPDefinition = copyCPDefinition(
						cpDefinitionSpecificationOptionValue.
							getCPDefinitionId());

					cpDefinitionSpecificationOptionValue =
						_cpDefinitionSpecificationOptionValuePersistence.
							findByC_CSOVI(
								newCPDefinition.getCPDefinitionId(),
								cpDefinitionSpecificationOptionValue.
									getCPDefinitionSpecificationOptionValueId());
				}
				catch (PortalException portalException) {
					throw new SystemException(portalException);
				}
			}

			// Commerce product definition specification option value

			_cpDefinitionSpecificationOptionValuePersistence.remove(
				cpDefinitionSpecificationOptionValue);

			// Expando

			_expandoRowLocalService.deleteRows(
				cpDefinitionSpecificationOptionValue.
					getCPDefinitionSpecificationOptionValueId());

			_cpDefinitionIndexHelper.reindexCPDefinition(
				cpDefinitionSpecificationOptionValue.getCPDefinitionId());
		}

		// Commerce product definition

		_cpDefinitionIndexHelper.reindexCPDefinition(
			cpDefinition.getCPDefinitionId());

		// Commerce product instances

		List<CPInstance> cpInstances =
			_cpInstancePersistence.findByCPDefinitionId(
				cpDefinition.getCPDefinitionId());

		for (CPInstance cpInstance : cpInstances) {
			if (isVersionable(cpInstance.getCPDefinitionId())) {
				CPDefinition newCPDefinition = copyCPDefinition(
					cpInstance.getCPDefinitionId());

				cpInstance = _cpInstancePersistence.findByC_C(
					newCPDefinition.getCPDefinitionId(),
					cpInstance.getCPInstanceUuid());
			}

			// Commerce product instance

			_cpInstancePersistence.remove(cpInstance);

			_cpInstanceOptionValueRelPersistence.removeByCPInstanceId(
				cpInstance.getCPInstanceId());

			_cpDefinitionOptionValueRelLocalServiceHelper.
				resetCPInstanceCPDefinitionOptionValueRels(
					cpInstance.getCPInstanceUuid());

			// Expando

			_expandoRowLocalService.deleteRows(cpInstance.getCPInstanceId());

			// Workflow

			_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
				cpInstance.getCompanyId(), cpInstance.getGroupId(),
				CPInstance.class.getName(), cpInstance.getCPInstanceId());

			_cpDefinitionIndexHelper.reindexCPDefinition(
				cpInstance.getCPDefinitionId());
		}

		// Commerce product definition option rels

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			_cpDefinitionOptionRelPersistence.findByCPDefinitionId(
				cpDefinition.getCPDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		for (CPDefinitionOptionRel cpDefinitionOptionRel :
				cpDefinitionOptionRels) {

			if (isVersionable(cpDefinitionOptionRel.getCPDefinitionId())) {
				CPDefinition newCPDefinition = copyCPDefinition(
					cpDefinitionOptionRel.getCPDefinitionId());

				cpDefinitionOptionRel =
					_cpDefinitionOptionRelPersistence.findByC_C(
						newCPDefinition.getCPDefinitionId(),
						cpDefinitionOptionRel.getCPOptionId());
			}

			// Commerce product definition option value rels

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_cpDefinitionOptionValueRelPersistence.
					findByCPDefinitionOptionRelId(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (CPDefinitionOptionValueRel cpDefinitionOptionValueRel :
					cpDefinitionOptionValueRels) {

				_cpDefinitionOptionValueRelPersistence.remove(
					cpDefinitionOptionValueRel);

				_expandoRowLocalService.deleteRows(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId());
			}

			// Commerce product definition option rel

			_cpDefinitionOptionRelPersistence.remove(cpDefinitionOptionRel);

			// Expando

			_expandoRowLocalService.deleteRows(
				cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			// Commerce product instances

			List<CPInstance> cpInstanceList =
				_cpInstancePersistence.findByCPDefinitionId(
					cpDefinitionOptionRel.getCPDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			long userId = PrincipalThreadLocal.getUserId();

			for (CPInstance cpInstanceItem : cpInstanceList) {
				if (cpInstanceItem.isInactive() ||
					!_cpInstanceOptionValueRelLocalService.
						hasCPInstanceCPDefinitionOptionRel(
							cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
							cpInstanceItem.getCPInstanceId())) {

					continue;
				}

				if (userId <= 0) {
					userId = cpInstanceItem.getUserId();
				}

				User user = _userLocalService.getUser(userId);
				Date date = new Date();

				CPInstance cpInstance = _cpInstancePersistence.findByPrimaryKey(
					cpInstanceItem.getCPInstanceId());

				cpInstance.setStatus(WorkflowConstants.STATUS_INACTIVE);
				cpInstance.setStatusByUserId(user.getUserId());
				cpInstance.setStatusByUserName(user.getFullName());
				cpInstance.setStatusDate(date);

				_cpInstancePersistence.update(cpInstance);
			}

			updateCPDefinitionIgnoreSKUCombinations(
				cpDefinitionOptionRel.getCPDefinitionId(),
				new ServiceContext());

			// Commerce product definition

			_cpDefinitionIndexHelper.reindexCPDefinition(
				cpDefinitionOptionRel.getCPDefinitionId());
		}

		// Commerce product definition attachment file entries

		List<CPAttachmentFileEntry> cpAttachmentFileEntries =
			_cpAttachmentFileEntryPersistence.findByC_C(
				_classNameLocalService.getClassNameId(
					CPDefinition.class.getName()),
				cpDefinition.getCPDefinitionId());

		for (CPAttachmentFileEntry cpAttachmentFileEntry :
				cpAttachmentFileEntries) {

			long cpDefinitionClassNameId =
				_classNameLocalService.getClassNameId(CPDefinition.class);

			if ((cpAttachmentFileEntry.getClassNameId() ==
					cpDefinitionClassNameId) &&
				isVersionable(cpAttachmentFileEntry.getClassPK())) {

				CPDefinition newCPDefinition = copyCPDefinition(
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

			// Commerce product attachment file entry

			_cpAttachmentFileEntryPersistence.remove(cpAttachmentFileEntry);

			// Expando

			_expandoRowLocalService.deleteRows(
				cpAttachmentFileEntry.getCPAttachmentFileEntryId());

			_cpDefinitionIndexHelper.reindex(
				cpAttachmentFileEntry.getClassNameId(),
				cpAttachmentFileEntry.getClassPK());
		}

		// Commerce product definition links

		List<CPDefinitionLink> cpDefinitionLinks =
			_cpDefinitionLinkPersistence.findByCPDefinitionId(
				cpDefinition.getCPDefinitionId());

		for (CPDefinitionLink cpDefinitionLink : cpDefinitionLinks) {
			if (isVersionable(cpDefinitionLink.getCPDefinitionId())) {
				try {
					CPDefinition newCPDefinition = copyCPDefinition(
						cpDefinitionLink.getCPDefinitionId());

					cpDefinitionLink = _cpDefinitionLinkPersistence.findByC_C_T(
						newCPDefinition.getCPDefinitionId(),
						cpDefinitionLink.getCProductId(),
						cpDefinitionLink.getType());
				}
				catch (PortalException portalException) {
					throw new SystemException(portalException);
				}
			}

			// Commerce product definition link

			_cpDefinitionLinkPersistence.remove(cpDefinitionLink);

			// Expando

			_expandoRowLocalService.deleteRows(
				cpDefinitionLink.getCPDefinitionLinkId());

			CProduct cProduct = _cProductPersistence.findByPrimaryKey(
				cpDefinitionLink.getCProductId());

			_cpDefinitionIndexHelper.reindexCPDefinition(
				cProduct.getPublishedCPDefinitionId());

			_cpDefinitionIndexHelper.reindexCPDefinition(
				cpDefinitionLink.getCPDefinitionId());
		}

		// Commerce product type

		CPType cpType = _cpTypeServicesTracker.getCPType(
			cpDefinition.getProductTypeName());

		if (cpType != null) {
			cpType.deleteCPDefinition(cpDefinition.getCPDefinitionId());
		}

		// Commerce product friendly URL entries

		Group companyGroup = _groupLocalService.getCompanyGroup(
			cpDefinition.getCompanyId());

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			companyGroup.getGroupId(), CProduct.class,
			cpDefinition.getCProductId());

		// Commerce product display layouts

		List<CPDisplayLayout> cpDisplayLayouts =
			_cpDisplayLayoutPersistence.findByC_C(
				_classNameLocalService.getClassNameId(CPDefinition.class),
				cpDefinition.getCPDefinitionId());

		for (CPDisplayLayout cpDisplayLayout : cpDisplayLayouts) {
			_cpDisplayLayoutPersistence.remove(cpDisplayLayout);
		}

		// Commerce product version contributors

		List<CPVersionContributor> cpVersionContributors =
			CPVersionContributorRegistryUtil.getCPVersionContributors();

		for (CPVersionContributor cpVersionContributor :
				cpVersionContributors) {

			cpVersionContributor.onDelete(cpDefinition.getCPDefinitionId());
		}

		// Commerce product definition

		_cpDefinitionPersistence.remove(cpDefinition);

		// Asset

		_assetEntryLocalService.deleteEntry(
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		// Expando

		_expandoRowLocalService.deleteRows(cpDefinition.getCPDefinitionId());

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			cpDefinition.getCompanyId(), cpDefinition.getGroupId(),
			CPDefinition.class.getName(), cpDefinition.getCPDefinitionId());

		return cpDefinition;
	}

	public boolean isPublishedCPDefinition(CPDefinition cpDefinition) {
		CProduct cProduct = _cProductPersistence.fetchByPrimaryKey(
			cpDefinition.getCProductId());

		if ((cProduct != null) &&
			(cProduct.getPublishedCPDefinitionId() ==
				cpDefinition.getCPDefinitionId())) {

			return true;
		}

		return false;
	}

	public boolean isPublishedCPDefinition(long cpDefinitionId) {
		CPDefinition cpDefinition = _cpDefinitionPersistence.fetchByPrimaryKey(
			cpDefinitionId);

		if (cpDefinition == null) {
			return false;
		}

		return isPublishedCPDefinition(cpDefinition);
	}

	public boolean isVersionable(long cpDefinitionId) {
		if (!isVersioningEnabled()) {
			return false;
		}

		return isPublishedCPDefinition(cpDefinitionId);
	}

	public boolean isVersionable(
		long cpDefinitionId, HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return isVersionable(cpDefinitionId);
		}

		boolean versionable = GetterUtil.getBoolean(
			httpServletRequest.getAttribute("versionable#" + cpDefinitionId),
			true);

		if (versionable) {
			return isVersionable(cpDefinitionId);
		}

		return false;
	}

	public boolean isVersioningEnabled() {
		try {
			CProductVersionConfiguration cProductVersionConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CProductVersionConfiguration.class,
					new SystemSettingsLocator(
						CProductVersionConfiguration.class.getName()));

			if (cProductVersionConfiguration.enabled()) {
				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	public void maintainVersionThreshold(long cProductId)
		throws PortalException {

		int threshold = 0;

		try {
			CProductVersionConfiguration cProductVersionConfiguration =
				ConfigurationProviderUtil.getConfiguration(
					CProductVersionConfiguration.class,
					new SystemSettingsLocator(
						CProductVersionConfiguration.class.getName()));

			threshold = cProductVersionConfiguration.versionThreshold();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return;
		}

		OrderByComparator<CPDefinition> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				CPDefinitionModelImpl.TABLE_NAME, Field.VERSION, false);

		List<CPDefinition> deletableCPDefinitions =
			_cpDefinitionPersistence.findByC_S(
				cProductId, WorkflowConstants.STATUS_APPROVED, threshold,
				threshold + Short.MAX_VALUE, orderByComparator);

		for (CPDefinition cpDefinition : deletableCPDefinitions) {
			deleteCPDefinition(cpDefinition);
		}
	}

	public CPDefinition updateCPDefinitionIgnoreSKUCombinations(
			long cpDefinitionId, boolean ignoreSKUCombinations,
			ServiceContext serviceContext)
		throws PortalException {

		_checkCPInstances(
			serviceContext.getUserId(), cpDefinitionId, ignoreSKUCombinations);

		CPDefinition cpDefinition = _cpDefinitionPersistence.findByPrimaryKey(
			cpDefinitionId);

		cpDefinition.setIgnoreSKUCombinations(ignoreSKUCombinations);

		return _cpDefinitionPersistence.update(cpDefinition);
	}

	public void updateCPDefinitionIgnoreSKUCombinations(
			long cpDefintionId, ServiceContext serviceContext)
		throws PortalException {

		if (_hasCPDefinitionSKUContributorCPDefinitionOptionRel(
				cpDefintionId)) {

			updateCPDefinitionIgnoreSKUCombinations(
				cpDefintionId, false, serviceContext);

			return;
		}

		updateCPDefinitionIgnoreSKUCombinations(
			cpDefintionId, true, serviceContext);
	}

	private void _checkCPInstances(
			long userId, long cpDefinitionId, boolean ignoreSKUCombinations)
		throws PortalException {

		if (ignoreSKUCombinations) {
			int cpInstancesCount = _cpInstancePersistence.countByC_ST(
				cpDefinitionId, WorkflowConstants.STATUS_APPROVED);

			if (cpInstancesCount <= 1) {
				return;
			}

			throw new CPDefinitionIgnoreSKUCombinationsException();
		}

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelPersistence.countByC_SC(cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount == 0) {
			return;
		}

		List<CPInstance> cpInstances = _cpInstancePersistence.findByC_ST(
			cpDefinitionId, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		for (CPInstance cpInstance : cpInstances) {
			if (!_cpInstanceOptionValueRelLocalService.
					hasCPInstanceOptionValueRel(cpInstance.getCPInstanceId())) {

				User user = _userLocalService.getUser(userId);
				Date date = new Date();

				CPInstance cpInstanceFromDB =
					_cpInstancePersistence.findByPrimaryKey(
						cpInstance.getCPInstanceId());

				cpInstanceFromDB.setStatus(WorkflowConstants.STATUS_INACTIVE);
				cpInstanceFromDB.setStatusByUserId(user.getUserId());
				cpInstanceFromDB.setStatusByUserName(user.getFullName());
				cpInstanceFromDB.setStatusDate(date);

				_cpInstancePersistence.update(cpInstanceFromDB);
			}
		}
	}

	private boolean _hasCPDefinitionSKUContributorCPDefinitionOptionRel(
		long cpDefinitionId) {

		int cpDefinitionOptionRelsCount =
			_cpDefinitionOptionRelPersistence.countByC_SC(cpDefinitionId, true);

		if (cpDefinitionOptionRelsCount > 0) {
			return true;
		}

		return false;
	}

	private void _reindexCPDefinitionOptionRels(CPDefinition cpDefinition)
		throws PortalException {

		Indexer<CPDefinitionOptionRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(CPDefinitionOptionRel.class);

		indexer.reindex(cpDefinition.getCPDefinitionOptionRels());
	}

	private void _reindexCPDefinitionOptionValueRels(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws PortalException {

		Indexer<CPDefinitionOptionValueRel> indexer =
			IndexerRegistryUtil.nullSafeGetIndexer(
				CPDefinitionOptionValueRel.class);

		indexer.reindex(cpDefinitionOptionRel.getCPDefinitionOptionValueRels());
	}

	private void _updateCommercePriceEntry(
			CPInstance cpInstance, String type, BigDecimal price,
			ServiceContext serviceContext)
		throws PortalException {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				getCatalogBaseCommercePriceListByType(
					cpInstance.getGroupId(), type);

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.fetchCommercePriceEntry(
				commercePriceList.getCommercePriceListId(),
				cpInstance.getCPInstanceUuid());

		if (commercePriceEntry == null) {
			CPDefinition cpDefinition = cpInstance.getCPDefinition();

			_commercePriceEntryLocalService.addCommercePriceEntry(
				cpDefinition.getCProductId(), cpInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), price, null,
				serviceContext);
		}
		else {
			_commercePriceEntryLocalService.updateCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), price, null,
				serviceContext);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionLocalServiceHelper.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceAccountGroupRelLocalService
		_commerceAccountGroupRelLocalService;

	@Reference
	private CommerceChannelRelLocalService _commerceChannelRelLocalService;

	@Reference
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Reference
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private CPAttachmentFileEntryPersistence _cpAttachmentFileEntryPersistence;

	@Reference
	private CPDefinitionIndexHelper _cpDefinitionIndexHelper;

	@Reference
	private CPDefinitionLinkPersistence _cpDefinitionLinkPersistence;

	@Reference
	private CPDefinitionLocalizationPersistence
		_cpDefinitionLocalizationPersistence;

	@Reference
	private CPDefinitionOptionRelPersistence _cpDefinitionOptionRelPersistence;

	@Reference
	private CPDefinitionOptionValueRelLocalServiceHelper
		_cpDefinitionOptionValueRelLocalServiceHelper;

	@Reference
	private CPDefinitionOptionValueRelPersistence
		_cpDefinitionOptionValueRelPersistence;

	@Reference
	private CPDefinitionPersistence _cpDefinitionPersistence;

	@Reference
	private CPDefinitionSpecificationOptionValuePersistence
		_cpDefinitionSpecificationOptionValuePersistence;

	@Reference
	private CPDisplayLayoutPersistence _cpDisplayLayoutPersistence;

	@Reference
	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

	@Reference
	private CPInstanceOptionValueRelPersistence
		_cpInstanceOptionValueRelPersistence;

	@Reference
	private CPInstancePersistence _cpInstancePersistence;

	@Reference
	private CProductLocalService _cProductLocalService;

	@Reference
	private CProductPersistence _cProductPersistence;

	@Reference
	private CPTypeServicesTracker _cpTypeServicesTracker;

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private PortalUUID _portalUUID;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

}