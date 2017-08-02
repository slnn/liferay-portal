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

import com.liferay.counter.kernel.model.Counter;
import com.liferay.counter.kernel.model.CounterModel;
import com.liferay.counter.model.impl.CounterModelImpl;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.util.SimpleCounter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class DataFactory extends BaseDataFactory {

	public DataFactory(InitContext initContext) throws Exception {
		super(initContext);

		_initContext = initContext;

		_userDataFactory = new UserDataFactory(initContext);

		_journalDataFactory = new JournalDataFactory(
			initContext, _userDataFactory);

		_assetDataFactory = new AssetDataFactory(initContext, _userDataFactory);

		_blogDataFactory = new BlogDataFactory(initContext);
		_dDLDataFactory = new DDLDataFactory(initContext);
		_dLDataFactory = new DLDataFactory(initContext, _userDataFactory);
		_layoutDataFactory = new LayoutDataFactory(initContext);
		_messageBoardDataFactory = new MessageBoardDataFactory(initContext);
		_portletPreferenceDataFactory = new PortletPreferenceDataFactory(
			initContext);

		_portletPreferenceDataFactory.setAssetDataFactory(_assetDataFactory);

		_releaseDataFactory = new ReleaseDataFactory(initContext);
		_resourcePermissionDataFactory = new ResourcePermissionDataFactory(
			initContext);

		_resourcePermissionDataFactory.setUserDataFactory(_userDataFactory);

		_socialActivityDataFactory = new SocialActivityDataFactory(initContext);
		_subscriptionDataFactory = new SubscriptionDataFactory(initContext);
		_wikiDataFactory = new WikiDataFactory(initContext);

		_assetDataFactory.setJournalDataFactory(_journalDataFactory);
		_dDLDataFactory.setUserDataFactory(_userDataFactory);
		_messageBoardDataFactory.setUserDataFactory(_userDataFactory);

		_dataFactories.put("assetDataFactory", _assetDataFactory);
		_dataFactories.put("blogDataFactory", _blogDataFactory);
		_dataFactories.put("dDLDataFactory", _dDLDataFactory);
		_dataFactories.put("dLDataFactory", _dLDataFactory);
		_dataFactories.put("journalDataFactory", _journalDataFactory);
		_dataFactories.put("layoutDataFactory", _layoutDataFactory);
		_dataFactories.put("messageBoardDataFactory", _messageBoardDataFactory);
		_dataFactories.put(
			"portletPreferenceDataFactory", _portletPreferenceDataFactory);
		_dataFactories.put("releaseDataFactory", _releaseDataFactory);
		_dataFactories.put(
			"resourcePermissionDataFactory", _resourcePermissionDataFactory);
		_dataFactories.put(
			"socialActivityDataFactory", _socialActivityDataFactory);
		_dataFactories.put("subscriptionDataFactory", _subscriptionDataFactory);
		_dataFactories.put("userDataFactory", _userDataFactory);
		_dataFactories.put("wikiDataFactory", _wikiDataFactory);
	}

	public long getCounterNext() {
		SimpleCounter counter = _initContext.getCounter();

		return counter.get();
	}

	public Map<String, Object> getDataFactories() {
		return _dataFactories;
	}

	public List<Integer> getSequence(int size) {
		List<Integer> sequence = new ArrayList<>(size);

		for (int i = 1; i <= size; i++) {
			sequence.add(i);
		}

		return sequence;
	}

	public List<CounterModel> newCounterModels() {
		SimpleCounter counter = _initContext.getCounter();
		SimpleCounter socialActivityCounter =
			_socialActivityDataFactory.getSocialActivityCounter();
		SimpleCounter resourcePermissionCounter = 
			_resourcePermissionDataFactory.getResourcePermissionCounter();

		List<CounterModel> counterModels = new ArrayList<>();

		// Counter

		CounterModel counterModel = new CounterModelImpl();

		counterModel.setName(Counter.class.getName());
		counterModel.setCurrentId(counter.get());

		counterModels.add(counterModel);

		// ResourcePermission

		counterModel = new CounterModelImpl();

		counterModel.setName(ResourcePermission.class.getName());
		counterModel.setCurrentId(resourcePermissionCounter.get());

		counterModels.add(counterModel);

		// SocialActivity

		counterModel = new CounterModelImpl();

		counterModel.setName(SocialActivity.class.getName());
		counterModel.setCurrentId(socialActivityCounter.get());

		counterModels.add(counterModel);

		return counterModels;
	}

	public IntegerWrapper newInteger() {
		return new IntegerWrapper();
	}

	private final AssetDataFactory _assetDataFactory;
	private final BlogDataFactory _blogDataFactory;
	private final Map<String, Object> _dataFactories = new HashMap<>();
	private final DDLDataFactory _dDLDataFactory;
	private final DLDataFactory _dLDataFactory;
	private final InitContext _initContext;
	private final JournalDataFactory _journalDataFactory;
	private final LayoutDataFactory _layoutDataFactory;
	private final MessageBoardDataFactory _messageBoardDataFactory;
	private final PortletPreferenceDataFactory _portletPreferenceDataFactory;
	private final ReleaseDataFactory _releaseDataFactory;
	private final ResourcePermissionDataFactory _resourcePermissionDataFactory;
	private final SocialActivityDataFactory _socialActivityDataFactory;
	private final SubscriptionDataFactory _subscriptionDataFactory;
	private final UserDataFactory _userDataFactory;
	private final WikiDataFactory _wikiDataFactory;

}