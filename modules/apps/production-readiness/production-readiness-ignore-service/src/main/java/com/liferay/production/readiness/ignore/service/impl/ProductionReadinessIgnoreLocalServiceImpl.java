/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;
import com.liferay.production.readiness.ignore.service.base.ProductionReadinessIgnoreLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore",
	service = AopService.class
)
public class ProductionReadinessIgnoreLocalServiceImpl
	extends ProductionReadinessIgnoreLocalServiceBaseImpl {

	public ProductionReadinessIgnore addProductionReadinessIgnore(
			long companyId, long userId, String ruleKey, String reason)
		throws PortalException {

		_checkPermission();

		ProductionReadinessIgnore productionReadinessIgnore =
			productionReadinessIgnorePersistence.fetchByC_R(companyId, ruleKey);

		if (productionReadinessIgnore != null) {
			return productionReadinessIgnore;
		}

		long productionReadinessIgnoreId = counterLocalService.increment();

		productionReadinessIgnore = productionReadinessIgnorePersistence.create(
			productionReadinessIgnoreId);

		productionReadinessIgnore.setCompanyId(companyId);
		productionReadinessIgnore.setUserId(userId);

		User user = userLocalService.getUser(userId);

		productionReadinessIgnore.setUserName(user.getFullName());
		productionReadinessIgnore.setCreateDate(new Date());
		productionReadinessIgnore.setRuleKey(ruleKey);
		productionReadinessIgnore.setReason(reason);

		return productionReadinessIgnorePersistence.update(
			productionReadinessIgnore);
	}

	public void deleteProductionReadinessIgnore(long companyId, String ruleKey)
		throws PortalException {

		_checkPermission();

		ProductionReadinessIgnore productionReadinessIgnore =
			productionReadinessIgnorePersistence.fetchByC_R(companyId, ruleKey);

		if (productionReadinessIgnore != null) {
			productionReadinessIgnorePersistence.remove(
				productionReadinessIgnore);
		}
	}

	public ProductionReadinessIgnore fetchProductionReadinessIgnore(
		long companyId, String ruleKey) {

		return productionReadinessIgnorePersistence.fetchByC_R(
			companyId, ruleKey);
	}

	public List<String> getIgnoredRuleKeys(long companyId)
		throws PortalException {

		_checkPermission();

		List<ProductionReadinessIgnore> productionReadinessIgnores =
			productionReadinessIgnorePersistence.findByCompanyId(companyId);

		List<String> ruleKeys = new ArrayList<>(
			productionReadinessIgnores.size());

		for (ProductionReadinessIgnore productionReadinessIgnore :
				productionReadinessIgnores) {

			ruleKeys.add(productionReadinessIgnore.getRuleKey());
		}

		return ruleKeys;
	}

	private void _checkPermission() throws PortalException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			throw new PrincipalException.MustBeAuthenticated("Guest");
		}

		if (!permissionChecker.isCompanyAdmin()) {
			throw new PrincipalException.MustBeCompanyAdmin(permissionChecker);
		}
	}

}
