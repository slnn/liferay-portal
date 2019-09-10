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

package com.liferay.fragment.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorTracker;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class FragmentCollectionContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			FragmentCollectionContributor.class,
			new TestFragmentCollectionContributor());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testRegisterFragmentCollectionContributor() throws Exception {
		_fragmentCollectionContributorTracker.getFragmentCollectionContributor(
			TestFragmentCollectionContributor.TEST_FRAGMENT_COLLECTION_KEY);

		Assert.assertNotNull(
			_fragmentCollectionContributorTracker.
				getFragmentCollectionContributor(
					TestFragmentCollectionContributor.
						TEST_FRAGMENT_COLLECTION_KEY));
	}

	@Inject
	private FragmentCollectionContributorTracker
		_fragmentCollectionContributorTracker;

	private ServiceRegistration<FragmentCollectionContributor>
		_serviceRegistration;

	private static class TestFragmentCollectionContributor
		implements FragmentCollectionContributor {

		public static final String TEST_FRAGMENT_COLLECTION_KEY =
			"test-fragment-collection-contributor";

		@Override
		public String getFragmentCollectionKey() {
			return TEST_FRAGMENT_COLLECTION_KEY;
		}

		@Override
		public List<FragmentEntry> getFragmentEntries(int type) {
			return Collections.emptyList();
		}

		@Override
		public String getName() {
			return "Test Fragment Collection Contributor";
		}

	}

}