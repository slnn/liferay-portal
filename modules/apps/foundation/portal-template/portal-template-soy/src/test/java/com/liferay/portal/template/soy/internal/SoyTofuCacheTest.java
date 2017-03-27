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

package com.liferay.portal.template.soy.internal;

import com.google.template.soy.tofu.SoyTofu;

import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.template.soy.internal.SoyTofuCache.SoyTemplateResource;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Bruno Basto
 */
public class SoyTofuCacheTest {

	@Before
	public void setUp() throws Exception {
		_soyTestHelper.setUp();
	}

	@After
	public void tearDown() {
		_soyTestHelper.tearDown();
	}

	@Test
	public void testCacheHit() throws Exception {
		List<TemplateResource> templateResources =
			_soyTestHelper.getTemplateResources(
				Arrays.asList("simple.soy", "context.soy"));

		SoyTofu soyTofu = Mockito.mock(SoyTofu.class);

		SoyTofuCache.add(templateResources, soyTofu);

		Assert.assertNotNull(SoyTofuCache.get(templateResources));
	}

	@Test
	public void testCacheMiss() throws Exception {
		List<TemplateResource> templateResources =
			_soyTestHelper.getTemplateResources(
				Arrays.asList("simple.soy", "context.soy"));

		SoyTofu soyTofu = Mockito.mock(SoyTofu.class);

		SoyTofuCache.add(templateResources, soyTofu);

		List<TemplateResource> templateResourcesA =
			_soyTestHelper.getTemplateResources(Arrays.asList("context.soy"));

		Assert.assertNull(SoyTofuCache.get(templateResourcesA));
	}

	@Test
	public void testRemoveAny() throws Exception {
		List<TemplateResource> cachedTemplateResources =
			_soyTestHelper.getTemplateResources(
				Arrays.asList(
					"simple.soy", "context.soy", "multi-context.soy"));

		SoyTofu soyTofu = Mockito.mock(SoyTofu.class);

		SoyTofuCache.add(cachedTemplateResources, soyTofu);

		List<TemplateResource> templateResources =
			_soyTestHelper.getTemplateResources(Arrays.asList("context.soy"));

		SoyTofuCache.removeIfAny(templateResources);

		Assert.assertNull(SoyTofuCache.get(templateResources));
	}

	@Test
	public void testRemoveAnyWithMultipleEntries() throws Exception {
		List<TemplateResource> cachedTemplateResourcesA =
			_soyTestHelper.getTemplateResources(Arrays.asList("simple.soy"));

		SoyTofu soyTofuA = Mockito.mock(SoyTofu.class);

		SoyTofuCache.add(cachedTemplateResourcesA, soyTofuA);

		Assert.assertNotNull(SoyTofuCache.get(cachedTemplateResourcesA));

		List<TemplateResource> cachedTemplateResourcesB =
			_soyTestHelper.getTemplateResources(
				Arrays.asList("context.soy", "multi-context.soy"));

		SoyTofu soyTofuB = Mockito.mock(SoyTofu.class);

		SoyTofuCache.add(cachedTemplateResourcesB, soyTofuB);

		List<TemplateResource> templateResources =
			_soyTestHelper.getTemplateResources(Arrays.asList("context.soy"));

		SoyTofuCache.removeIfAny(templateResources);

		Assert.assertNull(SoyTofuCache.get(cachedTemplateResourcesB));
		Assert.assertNotNull(SoyTofuCache.get(cachedTemplateResourcesA));
	}

	@Test
	public void testSoyTemplateResourceEquals() throws Exception {
		List<TemplateResource> cachedTemplateResourcesA =
			_soyTestHelper.getTemplateResources(Arrays.asList("simple.soy"));

		TemplateResource templateResource = cachedTemplateResourcesA.get(0);

		SoyTemplateResource soyTemplateResourceA = new SoyTemplateResource(
			templateResource);

		SoyTemplateResource soyTemplateResourceB = new SoyTemplateResource(
			templateResource);

		Assert.assertEquals(soyTemplateResourceA, soyTemplateResourceB);
	}

	@Test
	public void testSoyTemplateResourceHashcode() throws Exception {
		List<TemplateResource> cachedTemplateResourcesA =
			_soyTestHelper.getTemplateResources(Arrays.asList("simple.soy"));

		TemplateResource templateResource = cachedTemplateResourcesA.get(0);

		SoyTemplateResource soyTemplateResourceA = new SoyTemplateResource(
			templateResource);

		SoyTemplateResource soyTemplateResourceB = new SoyTemplateResource(
			templateResource);

		Assert.assertEquals(
			soyTemplateResourceA.hashCode(), soyTemplateResourceB.hashCode());
	}

	@Test
	public void testSoyTemplateResourceHashcodeDifferent() throws Exception {
		List<TemplateResource> cachedTemplateResourcesA =
			_soyTestHelper.getTemplateResources(Arrays.asList("simple.soy"));

		TemplateResource templateResourceA = cachedTemplateResourcesA.get(0);

		SoyTemplateResource soyTemplateResourceA = new SoyTemplateResource(
			templateResourceA);

		List<TemplateResource> cachedTemplateResourcesB =
			_soyTestHelper.getTemplateResources(Arrays.asList("context.soy"));

		TemplateResource templateResourceB = cachedTemplateResourcesB.get(0);

		SoyTemplateResource soyTemplateResourceB = new SoyTemplateResource(
			templateResourceB);

		Assert.assertNotEquals(
			soyTemplateResourceA.hashCode(), soyTemplateResourceB.hashCode());
	}

	private final SoyTestHelper _soyTestHelper = new SoyTestHelper();

}