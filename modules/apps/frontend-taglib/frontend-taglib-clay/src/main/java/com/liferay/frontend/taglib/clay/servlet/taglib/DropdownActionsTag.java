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

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.taglib.util.IncludeTag;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * @author Chema Balsas
 */
public class DropdownActionsTag extends IncludeTag {

	@Override
	public int doStartTag() {
		return EVAL_BODY_INCLUDE;
	}

	public void setButtonLabel(String buttonLabel) {
		_buttonLabel = buttonLabel;
	}

	public void setButtonStyle(String buttonStyle) {
		_buttonStyle = buttonStyle;
	}

	public void setButtonType(String buttonType) {
		_buttonType = buttonType;
	}

	public void setCaption(String caption) {
		_caption = caption;
	}

	public void setDefaultEventHandler(String defaultEventHandler) {
		_defaultEventHandler = defaultEventHandler;
	}

	public void setDropdownItems(List<DropdownItem> dropdownItems) {
		_dropdownItems = dropdownItems;
	}

	public void setExpanded(Boolean expanded) {
		_expanded = expanded;
	}

	public void setHelpText(String helpText) {
		_helpText = helpText;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		servletContext = ServletContextUtil.getServletContext();
	}

	public void setTriggerCssClasses(String triggerCssClasses) {
		_triggerCssClasses = triggerCssClasses;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_buttonLabel = null;
		_buttonStyle = null;
		_buttonType = null;
		_caption = null;
		_dropdownItems = null;
		_expanded = false;
		_helpText = null;
		_triggerCssClasses = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		request.setAttribute("clay:dropdown-actions:buttonLabel", _buttonLabel);
		request.setAttribute("clay:dropdown-actions:buttonStyle", _buttonStyle);
		request.setAttribute("clay:dropdown-actions:buttonType", _buttonType);
		request.setAttribute("clay:dropdown-actions:caption", _caption);
		request.setAttribute(
			"clay:dropdown-actions:dropdownItems",
			jsonSerializer.serializeDeep(_dropdownItems));
		request.setAttribute("clay:dropdown-actions:expanded", _expanded);
		request.setAttribute("clay:dropdown-actions:helpText", _helpText);
		request.setAttribute(
			"clay:dropdown-actions:triggerCssClasses", _triggerCssClasses);
	}

	private static final String _PAGE = "/dropdown_actions/page.jsp";

	private String _buttonLabel;
	private String _buttonStyle;
	private String _buttonType;
	private String _caption;
	private String _defaultEventHandler;
	private List<DropdownItem> _dropdownItems;
	private boolean _expanded;
	private String _helpText;
	private String _triggerCssClasses;

}