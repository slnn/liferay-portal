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

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.FileAvailabilityUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.jsp.JspException;

/**
 * @author Dante Wang
 */
public class SimpleIncludeTag extends IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			String page = getPage();

			if (Validator.isNull(page)) {
				page = getEndPage();

				if (Validator.isNull(page)) {
					return processEndTag();
				}
			}

			callSetAttributes();

			if (themeResourceExists(page)) {
				doIncludeTheme(page);

				return EVAL_PAGE;
			}

			if (!FileAvailabilityUtil.isAvailable(servletContext, page)) {
				logUnavailablePage(page);

				return processEndTag();
			}

			doInclude(page, false);

			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspException(e);
		}
		finally {
			doClearTag();
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			String page = getStartPage();

			if (Validator.isNull(page)) {
				return processStartTag();
			}

			callSetAttributes();

			if (themeResourceExists(page)) {
				doIncludeTheme(page);

				return EVAL_BODY_INCLUDE;
			}

			if (!FileAvailabilityUtil.isAvailable(servletContext, page)) {
				logUnavailablePage(page);

				return processStartTag();
			}

			doInclude(page, true);

			return EVAL_BODY_INCLUDE;
		}
		catch (Exception e) {
			throw new JspException(e);
		}
	}

	@Override
	protected String getPage() {
		return null;
	}

	private static final String _PAGE =
		"/html/taglib/ui/simple_portlet/page.jsp";

}