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

package com.liferay.site.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(immediate = true, service = RecentGroupManager.class)
public class RecentGroupManager {

	public void addRecentGroup(
		HttpServletRequest httpServletRequest, Group group) {

		addRecentGroup(httpServletRequest, group.getGroupId());
	}

	public void addRecentGroup(
		HttpServletRequest httpServletRequest, long groupId) {

		long liveGroupId = _getLiveGroupId(groupId);

		if (liveGroupId <= 0) {
			return;
		}

		Group liveGroup = _groupLocalService.fetchGroup(liveGroupId);

		if (liveGroup.isLayoutPrototype() || liveGroup.isLayoutSetPrototype()) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			String value = SessionClicks.get(
				httpServletRequest, _KEY_RECENT_GROUPS, null);

			List<Long> groupIds = ListUtil.fromArray(
				ArrayUtil.toLongArray(
					com.liferay.portal.kernel.util.StringUtil.split(
						value, 0L)));

			groupIds.remove(liveGroupId);

			groupIds.add(0, liveGroupId);

			groupIds = ListUtil.subList(
				groupIds, 0, PropsValues.RECENT_GROUPS_MAX_ELEMENTS);

			SessionClicks.put(
				httpServletRequest, _KEY_RECENT_GROUPS,
				com.liferay.portal.kernel.util.StringUtil.merge(groupIds));

			return;
		}

		_portalPreferenceValueLocalService.updatePreferenceValue(
			themeDisplay.getUserId(), PortletKeys.PREFS_OWNER_TYPE_USER,
			SessionClicks.class.getName(), _KEY_RECENT_GROUPS, 0,
			value -> {
				String liveGroupIdString = String.valueOf(liveGroupId);

				List<String> groupIds = StringUtil.split(value);

				if (groupIds.isEmpty()) {
					return liveGroupIdString;
				}

				groupIds.remove(liveGroupIdString);

				StringBundler sb = new StringBundler((2 * groupIds.size()) + 1);

				sb.append(liveGroupIdString);

				for (int i = 0;
					 (i < groupIds.size()) &&
					 (i < PropsValues.RECENT_GROUPS_MAX_ELEMENTS); i++) {

					sb.append(StringPool.COMMA);
					sb.append(groupIds.get(i));
				}

				return sb.toString();
			});
	}

	public List<Group> getRecentGroups(HttpServletRequest httpServletRequest) {
		String value = SessionClicks.get(
			httpServletRequest, _KEY_RECENT_GROUPS, null);

		try {
			PortletRequest portletRequest =
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST);

			return getRecentGroups(value, portletRequest);
		}
		catch (Exception exception) {
			_log.error("Unable to get recent groups", exception);
		}

		return Collections.emptyList();
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             #getRecentGroups(String, PortletRequest)}
	 */
	@Deprecated
	protected List<Group> getRecentGroups(String value) {
		List<String> groupIds = StringUtil.split(value);

		if (groupIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<Group> groups = new ArrayList<>(groupIds.size());

		for (String groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(groupId));

			if (!_groupLocalService.isLiveGroupActive(group)) {
				continue;
			}

			groups.add(group);
		}

		return groups;
	}

	protected List<Group> getRecentGroups(
			String value, PortletRequest portletRequest)
		throws Exception {

		List<String> groupIds = StringUtil.split(value);

		if (groupIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<Group> groups = new ArrayList<>(groupIds.size());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(
				_portal.getUser(portletRequest));

		for (String groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(
				GetterUtil.getLong(groupId));

			if (!_groupLocalService.isLiveGroupActive(group)) {
				continue;
			}

			if (!group.isCompany()) {
				Layout layout = _layoutLocalService.fetchFirstLayout(
					group.getGroupId(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

				if (layout == null) {
					layout = _layoutLocalService.fetchFirstLayout(
						group.getGroupId(), true,
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

					if ((layout == null) ||
						!LayoutPermissionUtil.contains(
							permissionChecker, layout, true, ActionKeys.VIEW)) {

						continue;
					}
				}
			}

			String groupURL = _groupURLProvider.getGroupURL(
				group, portletRequest);

			if (Validator.isNull(groupURL)) {
				continue;
			}

			groups.add(group);
		}

		return groups;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	private long _getLiveGroupId(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return 0;
		}

		if (!group.isStagedRemotely() && group.isStagingGroup()) {
			return group.getLiveGroupId();
		}

		return groupId;
	}

	private static final String _KEY_RECENT_GROUPS =
		"com.liferay.site.util_recentGroups";

	private static final Log _log = LogFactoryUtil.getLog(
		RecentGroupManager.class);

	private GroupLocalService _groupLocalService;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

}