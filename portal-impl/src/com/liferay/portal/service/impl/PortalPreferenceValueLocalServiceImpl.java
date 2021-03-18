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

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.SQLStateAcceptor;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValuePersistence;
import com.liferay.portal.kernel.spring.aop.Property;
import com.liferay.portal.kernel.spring.aop.Retry;
import com.liferay.portal.service.base.PortalPreferenceValueLocalServiceBaseImpl;
import com.liferay.portlet.PortalPreferenceKey;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.internal.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Preston Crary
 */
public class PortalPreferenceValueLocalServiceImpl
	extends PortalPreferenceValueLocalServiceBaseImpl {

	public static Map<PortalPreferenceKey, String[]> getPreferenceMap(
		PortalPreferenceValuePersistence portalPreferenceValuePersistence,
		long portalPreferencesId, boolean useFinderCache) {

		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValuesMap = getPortalPreferenceValuesMap(
				portalPreferenceValuePersistence, portalPreferencesId,
				useFinderCache);

		Map<PortalPreferenceKey, String[]> preferenceMap = new HashMap<>();

		for (Map.Entry<PortalPreferenceKey, List<PortalPreferenceValue>> entry :
				portalPreferenceValuesMap.entrySet()) {

			List<PortalPreferenceValue> portalPreferenceValues =
				entry.getValue();

			String[] values = new String[portalPreferenceValues.size()];

			for (int i = 0; i < portalPreferenceValues.size(); i++) {
				PortalPreferenceValue portalPreferenceValue =
					portalPreferenceValues.get(i);

				values[i] = portalPreferenceValue.getValue();
			}

			preferenceMap.put(entry.getKey(), values);
		}

		return preferenceMap;
	}

	@Override
	public com.liferay.portal.kernel.portlet.PortalPreferences
		getPortalPreferences(
			PortalPreferences portalPreferences, boolean signedIn) {

		Map<PortalPreferenceKey, String[]> preferenceMap = getPreferenceMap(
			portalPreferenceValuePersistence,
			portalPreferences.getPortalPreferencesId(), true);

		return new PortalPreferencesImpl(
			portalPreferences.getOwnerId(), portalPreferences.getOwnerType(),
			preferenceMap, signedIn);
	}

	@Override
	public String getPreferenceValue(
		long ownerId, int ownerType, String namespace, String key, int index,
		String defaultValue) {

		PortalPreferences portalPreferences =
			portalPreferencesPersistence.fetchByO_O(ownerId, ownerType);

		if (portalPreferences == null) {
			return defaultValue;
		}

		PortalPreferenceValue portalPreferenceValue =
			portalPreferenceValuePersistence.fetchByP_I_K_N(
				portalPreferences.getPortalPreferencesId(), index, key,
				namespace);

		if (portalPreferenceValue == null) {
			return defaultValue;
		}

		String value = PreferenceUtil.getActualValue(
			portalPreferenceValue.getValue());

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	@Override
	@Retry(
		acceptor = SQLStateAcceptor.class,
		properties = {
			@Property(
				name = SQLStateAcceptor.SQLSTATE,
				value = SQLStateAcceptor.SQLSTATE_INTEGRITY_CONSTRAINT_VIOLATION + "," + SQLStateAcceptor.SQLSTATE_TRANSACTION_ROLLBACK
			)
		}
	)
	public void updatePreferenceValue(
		long ownerId, int ownerType, String namespace, String key, int index,
		Function<String, String> valueFunction) {

		PortalPreferences portalPreferences =
			portalPreferencesPersistence.fetchByO_O(ownerId, ownerType);

		if (portalPreferences == null) {
			long portalPreferencesId = counterLocalService.increment();

			portalPreferences = portalPreferencesPersistence.create(
				portalPreferencesId);

			portalPreferences.setOwnerId(ownerId);
			portalPreferences.setOwnerType(ownerType);

			portalPreferences = portalPreferencesPersistence.update(
				portalPreferences);
		}

		PortalPreferenceValue portalPreferenceValue =
			portalPreferenceValuePersistence.fetchByP_I_K_N(
				portalPreferences.getPortalPreferencesId(), index, key,
				namespace);

		String originalValue = null;

		if (portalPreferenceValue != null) {
			originalValue = portalPreferenceValue.getValue();
		}

		String newValue = valueFunction.apply(
			PreferenceUtil.getActualValue(originalValue));

		if (newValue == null) {
			if (portalPreferenceValue != null) {
				portalPreferenceValuePersistence.remove(portalPreferenceValue);
			}

			return;
		}

		newValue = PreferenceUtil.getXMLSafeValue(newValue);

		if (Objects.equals(originalValue, newValue)) {
			return;
		}

		if (portalPreferenceValue == null) {
			long portalPreferenceValueId = counterLocalService.increment(
				PortalPreferenceValue.class.getName());

			portalPreferenceValue = portalPreferenceValuePersistence.create(
				portalPreferenceValueId);

			portalPreferenceValue.setPortalPreferencesId(
				portalPreferences.getPortalPreferencesId());
			portalPreferenceValue.setIndex(index);
			portalPreferenceValue.setKey(key);
			portalPreferenceValue.setNamespace(namespace);
		}

		portalPreferenceValue.setValue(newValue);

		portalPreferenceValuePersistence.update(portalPreferenceValue);
	}

	protected static Map<PortalPreferenceKey, List<PortalPreferenceValue>>
		getPortalPreferenceValuesMap(
			PortalPreferenceValuePersistence portalPreferenceValuePersistence,
			long portalPreferencesId, boolean useFinderCache) {

		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValuesMap = new HashMap<>();

		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValuePersistence.findByPortalPreferencesId(
					portalPreferencesId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null, useFinderCache)) {

			List<PortalPreferenceValue> portalPreferenceValues =
				portalPreferenceValuesMap.computeIfAbsent(
					new PortalPreferenceKey(
						portalPreferenceValue.getNamespace(),
						portalPreferenceValue.getKey()),
					key -> new ArrayList<>(1));

			portalPreferenceValues.add(portalPreferenceValue);
		}

		return portalPreferenceValuesMap;
	}

}