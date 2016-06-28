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

import com.liferay.portal.kernel.servlet.DirectServletRegistryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.FileAvailabilityUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * @author Dante Wang
 */
public class SimpleIncludeTag extends IncludeTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		DirectServletRegistryUtil.putServlet(
			_PAGE, new SimpleIncludeTagPageServlet());

		super.setPageContext(pageContext);
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE =
		"/html/taglib/ui/simple_portlet/page.jsp";

	public class SimpleIncludeTagPageServlet implements Servlet {

		@Override
		public void init(ServletConfig servletConfig) throws ServletException {
			_servletConfig = servletConfig;
		}

		@Override
		public ServletConfig getServletConfig() {
			return _servletConfig;
		}

		@Override
		public void service(
				ServletRequest servletRequest, ServletResponse servletResponse)
			throws ServletException, IOException {

			System.out.println("ABC");
		}

		@Override
		public String getServletInfo() {
			return _PAGE;
		}

		@Override
		public void destroy() {
		}

		private ServletConfig _servletConfig;
	}

}