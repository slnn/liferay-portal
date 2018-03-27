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

package com.liferay.dynamic.insert.taglib.servlet.taglib;

import com.liferay.dynamic.insert.taglib.internal.util.DynamicInsertUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.taglib.BaseBodyTagSupport;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * @author Matthew Tambara
 */
public class DynamicInsertTag extends BaseBodyTagSupport implements BodyTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			StringBundler sb = getBodyContentAsStringBundler();

			sb = DynamicInsertUtil.insert(_name, pageContext, sb);

			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(sb.toString());

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspException(e);
		}
		finally {
			_name = null;
		}
	}

	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _name;

}