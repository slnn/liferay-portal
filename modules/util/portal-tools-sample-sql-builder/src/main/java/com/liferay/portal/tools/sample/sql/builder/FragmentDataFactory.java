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

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollectionModel;
import com.liferay.fragment.model.FragmentEntryLinkModel;
import com.liferay.fragment.model.FragmentEntryModel;
import com.liferay.fragment.model.impl.FragmentCollectionModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl;
import com.liferay.fragment.model.impl.FragmentEntryModelImpl;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureModel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelModel;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureModelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.page.template.util.LayoutPageTemplateStructureHelperUtil;
import com.liferay.layout.util.structure.ContainerLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.template.LayoutData;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Lily Chi
 */
public class FragmentDataFactory extends BaseDataFactory {

	public FragmentCollectionModel newFragmentCollectionModel(long groupId) {
		FragmentCollectionModel fragmentCollectionModel =
			new FragmentCollectionModelImpl();

		fragmentCollectionModel.setUuid(SequentialUUID.generate());
		fragmentCollectionModel.setFragmentCollectionId(counter.get());
		fragmentCollectionModel.setGroupId(groupId);
		fragmentCollectionModel.setCompanyId(COMPANY_ID);
		fragmentCollectionModel.setUserId(SAMPLE_USER_ID);
		fragmentCollectionModel.setCreateDate(new Date());
		fragmentCollectionModel.setModifiedDate(new Date());
		fragmentCollectionModel.setFragmentCollectionKey("fragmentcollection");
		fragmentCollectionModel.setName("fragmentcollection");

		return fragmentCollectionModel;
	}

	public FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, FragmentEntryModel fragmentEntryModel) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());
		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());
		fragmentEntryLinkModel.setGroupId(fragmentEntryModel.getGroupId());
		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());
		fragmentEntryLinkModel.setFragmentEntryId(
			fragmentEntryModel.getFragmentEntryId());
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setCss(fragmentEntryModel.getCss());
		fragmentEntryLinkModel.setJs(fragmentEntryModel.getJs());
		fragmentEntryLinkModel.setHtml(fragmentEntryModel.getHtml());
		fragmentEntryLinkModel.setEditableValues(StringPool.BLANK);
		fragmentEntryLinkModel.setNamespace(StringUtil.randomId());
		fragmentEntryLinkModel.setPosition(0);

		return fragmentEntryLinkModel;
	}

	public List<FragmentEntryLinkModel> newFragmentEntryLinkModels(
			List<LayoutModel> layoutModels)
		throws Exception {

		List<FragmentEntryLinkModel> fragmentEntryLinkModels =
			new ArrayList<>();

		Map<String, String> nameSpaces = HashMapBuilder.put(
			_HEADING_RENDER_KEY, StringUtil.randomId()
		).put(
			_PARAGRAPH_RENDER_KEY, StringUtil.randomId()
		).put(
			"LoginPortlet", StringUtil.randomId()
		).build();

		for (LayoutModel layoutModel : layoutModels) {
			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, "", "", "", "",
					readFile("loginPortlet_editValue.json"), 0,
					nameSpaces.get("LoginPortlet")));

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, _HEADING_RENDER_KEY, readFile("heading.css"),
					readFile("heading.html"),
					readFile("heading_configuration.json"),
					readFile("heading_editValue.json"), 0,
					nameSpaces.get(_HEADING_RENDER_KEY)));

			fragmentEntryLinkModels.add(
				newFragmentEntryLinkModel(
					layoutModel, _PARAGRAPH_RENDER_KEY,
					readFile("paragraph.css"), readFile("paragraph.html"),
					readFile("paragraph_configuration.json"),
					readFile("paragraph_editValue.json"), 0,
					nameSpaces.get(_PARAGRAPH_RENDER_KEY)));
		}

		return fragmentEntryLinkModels;
	}

	public FragmentEntryModel newFragmentEntryModel(
			long groupId, FragmentCollectionModel fragmentCollectionModel)
		throws Exception {

		FragmentEntryModel fragmentEntryModel = new FragmentEntryModelImpl();

		fragmentEntryModel.setUuid(SequentialUUID.generate());
		fragmentEntryModel.setFragmentEntryId(counter.get());
		fragmentEntryModel.setGroupId(groupId);
		fragmentEntryModel.setCompanyId(COMPANY_ID);
		fragmentEntryModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryModel.setCreateDate(new Date());
		fragmentEntryModel.setModifiedDate(new Date());
		fragmentEntryModel.setFragmentCollectionId(
			fragmentCollectionModel.getFragmentCollectionId());
		fragmentEntryModel.setFragmentEntryKey("web_content");
		fragmentEntryModel.setName("web_content");
		fragmentEntryModel.setCss(StringPool.BLANK);
		fragmentEntryModel.setHtml(readFile("web_content.html"));
		fragmentEntryModel.setJs(StringPool.BLANK);
		fragmentEntryModel.setType(FragmentConstants.TYPE_COMPONENT);
		fragmentEntryModel.setStatus(WorkflowConstants.STATUS_APPROVED);

		return fragmentEntryModel;
	}

	public LayoutPageTemplateStructureModel newLayoutPageTemplateStructureModel(
		LayoutModel layoutModel) {

		LayoutPageTemplateStructureModel layoutPageTemplateStructureModel =
			new LayoutPageTemplateStructureModelImpl();

		layoutPageTemplateStructureModel.setUuid(SequentialUUID.generate());

		layoutPageTemplateStructureModel.setLayoutPageTemplateStructureId(
			counter.get());

		layoutPageTemplateStructureModel.setGroupId(layoutModel.getGroupId());
		layoutPageTemplateStructureModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureModel.setCreateDate(new Date());
		layoutPageTemplateStructureModel.setModifiedDate(new Date());
		layoutPageTemplateStructureModel.setClassNameId(
			getClassNameId(Layout.class));
		layoutPageTemplateStructureModel.setClassPK(layoutModel.getPlid());

		return layoutPageTemplateStructureModel;
	}

	public LayoutPageTemplateStructureRelModel
		newLayoutPageTemplateStructureRelModel(
			LayoutModel layoutModel,
			LayoutPageTemplateStructureModel layoutPageTemplateStructureModel,
			FragmentEntryLinkModel fragmentEntryLinkModel) {

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());
		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());
		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);

		LayoutData layoutData = LayoutData.of(
			layoutModel.toEscapedModel(),
			layoutRow -> layoutRow.addLayoutColumns(
				layoutColumn -> {
					List<Long> fragmentEntryLinkIds =
						layoutColumn.getFragmentEntryLinkIds();

					fragmentEntryLinkIds.add(
						fragmentEntryLinkModel.getFragmentEntryLinkId());
				}));

		JSONObject jsonObject = layoutData.getLayoutDataJSONObject();

		layoutPageTemplateStructureRelModel.setData(jsonObject.toString());

		return layoutPageTemplateStructureRelModel;
	}

	public LayoutPageTemplateStructureRelModel
		newLayoutPageTemplateStructureRelModel(
			LayoutModel layoutModel,
			LayoutPageTemplateStructureModel layoutPageTemplateStructureModel,
			List<FragmentEntryLinkModel> fragmentEntryLinkModels) {

		List<FragmentEntryLinkModel> targetFragmentEntryLinkModels =
			new ArrayList<>();

		for (FragmentEntryLinkModel model : fragmentEntryLinkModels) {
			if (model.getClassPK() == layoutModel.getPlid()) {
				targetFragmentEntryLinkModels.add(model);
			}
		}

		LayoutPageTemplateStructureRelModel
			layoutPageTemplateStructureRelModel =
				new LayoutPageTemplateStructureRelModelImpl();

		layoutPageTemplateStructureRelModel.setUuid(SequentialUUID.generate());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureRelId(
			counter.get());
		layoutPageTemplateStructureRelModel.setGroupId(
			layoutPageTemplateStructureModel.getGroupId());
		layoutPageTemplateStructureRelModel.setCompanyId(COMPANY_ID);
		layoutPageTemplateStructureRelModel.setUserId(SAMPLE_USER_ID);
		layoutPageTemplateStructureRelModel.setUserName(SAMPLE_USER_NAME);
		layoutPageTemplateStructureRelModel.setCreateDate(new Date());
		layoutPageTemplateStructureRelModel.setModifiedDate(new Date());
		layoutPageTemplateStructureRelModel.setLayoutPageTemplateStructureId(
			layoutPageTemplateStructureModel.
				getLayoutPageTemplateStructureId());
		layoutPageTemplateStructureRelModel.setSegmentsExperienceId(0L);

		JSONObject originJsonObject =
			LayoutPageTemplateStructureHelperUtil.
				generateContentLayoutStructure(
					new ArrayList<>(),
					LayoutPageTemplateEntryTypeConstants.TYPE_BASIC);

		LayoutStructure originLayoutStructure = LayoutStructure.of(
			originJsonObject.toString());

		LayoutStructure layoutStructure = _generateJsonData(
			originLayoutStructure, layoutModel, fragmentEntryLinkModels);

		JSONObject jsonObject = layoutStructure.toJSONObject();

		layoutPageTemplateStructureRelModel.setData(jsonObject.toString());

		return layoutPageTemplateStructureRelModel;
	}

	protected FragmentEntryLinkModel newFragmentEntryLinkModel(
		LayoutModel layoutModel, String renderKey, String css, String html,
		String configuration, String editValue, int position,
		String nameSpace) {

		FragmentEntryLinkModel fragmentEntryLinkModel =
			new FragmentEntryLinkModelImpl();

		fragmentEntryLinkModel.setUuid(SequentialUUID.generate());
		fragmentEntryLinkModel.setFragmentEntryLinkId(counter.get());
		fragmentEntryLinkModel.setGroupId(layoutModel.getGroupId());
		fragmentEntryLinkModel.setCompanyId(COMPANY_ID);
		fragmentEntryLinkModel.setUserId(SAMPLE_USER_ID);
		fragmentEntryLinkModel.setUserName(SAMPLE_USER_NAME);
		fragmentEntryLinkModel.setCreateDate(new Date());
		fragmentEntryLinkModel.setModifiedDate(new Date());
		fragmentEntryLinkModel.setFragmentEntryId(0);
		fragmentEntryLinkModel.setClassNameId(getClassNameId(Layout.class));
		fragmentEntryLinkModel.setClassPK(layoutModel.getPlid());
		fragmentEntryLinkModel.setPlid(layoutModel.getPlid());
		fragmentEntryLinkModel.setRendererKey(renderKey);
		fragmentEntryLinkModel.setConfiguration(configuration);
		fragmentEntryLinkModel.setCss(css);
		fragmentEntryLinkModel.setHtml(html);
		fragmentEntryLinkModel.setConfiguration(configuration);
		fragmentEntryLinkModel.setEditableValues(editValue);
		fragmentEntryLinkModel.setNamespace(nameSpace);
		fragmentEntryLinkModel.setPosition(position);

		return fragmentEntryLinkModel;
	}

	private LayoutStructure _generateJsonData(
		LayoutStructure layoutStructure, LayoutModel layoutModel,
		List<FragmentEntryLinkModel> fragmentEntryLinkModels) {

		//Generate the first Container in the home page
		String parentItemId = layoutStructure.getMainItemId();

		ContainerLayoutStructureItem containerLayoutStructureItem1 =
			(ContainerLayoutStructureItem)
				layoutStructure.addContainerLayoutStructureItem(
					parentItemId, 0);

		JSONObject jsonObject1 = JSONUtil.put(
			"title", _BACKGROUND_PICTURE_TITLE
		).put(
			"url", _BACKGROUND_PICTURE_URL
		);

		containerLayoutStructureItem1.setBackgroundImageJSONObject(jsonObject1);

		containerLayoutStructureItem1.setAlign(null);
		containerLayoutStructureItem1.setBorderColor(null);
		containerLayoutStructureItem1.setBorderRadius("");
		containerLayoutStructureItem1.setBorderWidth(0);
		containerLayoutStructureItem1.setContentDisplay("block");
		containerLayoutStructureItem1.setJustify("");
		containerLayoutStructureItem1.setMarginBottom(0);
		containerLayoutStructureItem1.setMarginLeft(0);
		containerLayoutStructureItem1.setMarginRight(0);
		containerLayoutStructureItem1.setMarginTop(0);
		containerLayoutStructureItem1.setOpacity(100);
		containerLayoutStructureItem1.setPaddingBottom(8);
		containerLayoutStructureItem1.setPaddingLeft(0);
		containerLayoutStructureItem1.setPaddingRight(0);
		containerLayoutStructureItem1.setPaddingTop(8);
		containerLayoutStructureItem1.setShadow("");
		containerLayoutStructureItem1.setWidthType("fluid");

		//Generate the login portlet in the first Container of the home page

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			String rendererKey = fragmentEntryLinkModel.getRendererKey();

			if (rendererKey.equals("") &&
				(fragmentEntryLinkModel.getPlid() == layoutModel.getPlid())) {

				layoutStructure.addFragmentLayoutStructureItem(
					fragmentEntryLinkModel.getFragmentEntryLinkId(),
					containerLayoutStructureItem1.getItemId(), 0);

				break;
			}
		}

		//Generate second Container in the home page

		parentItemId = containerLayoutStructureItem1.getItemId();

		ContainerLayoutStructureItem containerLayoutStructureItem2 =
			(ContainerLayoutStructureItem)
				layoutStructure.addContainerLayoutStructureItem(
					parentItemId, 1);

		JSONObject jsonObject2 = JSONFactoryUtil.createJSONObject();

		containerLayoutStructureItem2.setBackgroundImageJSONObject(jsonObject2);

		containerLayoutStructureItem2.setAlign(null);
		containerLayoutStructureItem2.setBorderColor(null);
		containerLayoutStructureItem2.setBorderRadius("");
		containerLayoutStructureItem2.setBorderWidth(0);
		containerLayoutStructureItem2.setContentDisplay("block");
		containerLayoutStructureItem2.setJustify("");
		containerLayoutStructureItem2.setMarginBottom(0);
		containerLayoutStructureItem2.setMarginLeft(0);
		containerLayoutStructureItem2.setMarginRight(0);
		containerLayoutStructureItem2.setMarginTop(0);
		containerLayoutStructureItem2.setOpacity(100);
		containerLayoutStructureItem2.setPaddingBottom(0);
		containerLayoutStructureItem2.setPaddingLeft(3);
		containerLayoutStructureItem2.setPaddingRight(3);
		containerLayoutStructureItem2.setPaddingTop(0);
		containerLayoutStructureItem2.setShadow("");
		containerLayoutStructureItem2.setWidthType("fixed");

		//Generate fragment components on home page
		FragmentLayoutStructureItem fragmentLayoutStructureItem = null;

		for (FragmentEntryLinkModel fragmentEntryLinkModel :
				fragmentEntryLinkModels) {

			String rendererKey = fragmentEntryLinkModel.getRendererKey();

			if (rendererKey.equals(_HEADING_RENDER_KEY) &&
				(fragmentEntryLinkModel.getPlid() == layoutModel.getPlid())) {

				fragmentLayoutStructureItem =
					(FragmentLayoutStructureItem)
						layoutStructure.addFragmentLayoutStructureItem(
							fragmentEntryLinkModel.getFragmentEntryLinkId(),
							containerLayoutStructureItem2.getItemId(), 0);
			}
			else if (rendererKey.equals(_PARAGRAPH_RENDER_KEY) &&
					 (fragmentEntryLinkModel.getPlid() ==
						 layoutModel.getPlid())) {

				fragmentLayoutStructureItem =
					(FragmentLayoutStructureItem)
						layoutStructure.addFragmentLayoutStructureItem(
							fragmentEntryLinkModel.getFragmentEntryLinkId(),
							containerLayoutStructureItem2.getItemId(), 1);
			}
		}

		return layoutStructure;
	}

	private static final String _BACKGROUND_PICTURE_TITLE =
		"welcome_bg_benchmark.png";

	private static final String _BACKGROUND_PICTURE_URL =
		"/welcome_bg_benchmark.png";

	private static final String _HEADING_RENDER_KEY = "BASIC_COMPONENT-heading";

	private static final String _PARAGRAPH_RENDER_KEY =
		"BASIC_COMPONENT-paragraph";

}