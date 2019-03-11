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

package com.liferay.portal.template.soy.renderer.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class SoyJavaScriptRendererUtil {

	public static String getJavaScript(
		Map<String, Object> context, String id, String module) {

		return getJavaScript(context, id, module, true);
	}

	public static String getJavaScript(
		Map<String, Object> context, String id, String module,
		boolean wrapper) {

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		String contextString = jsonSerializer.serializeDeep(context);

		StringBundler componentSB = new StringBundler(9);

		componentSB.append("Liferay.component('");
		componentSB.append(id);
		componentSB.append("', new ");
		componentSB.append(module);
		componentSB.append(".default(context");

		if (wrapper) {
			componentSB.append(", '#");
			componentSB.append(id);
			componentSB.append("'");
		}

		componentSB.append("), componentConfig);");

		StringBundler sb = new StringBundler(10);

		sb.append("var context = Object.assign(");
		sb.append(contextString);
		sb.append(", Liferay.getComponentCache('$ID'));");

		sb.append("var componentConfig = { cacheState: context.cacheState, ");
		sb.append("destroyOnNavigate: true, portletId: context.portletId};");

		if (Validator.isNotNull(context.get("defaultEventHandler"))) {
			sb.append("Liferay.componentReady(context.defaultEventHandler).");
			sb.append("then(function(defaultEventHandler) {");
			sb.append("context.defaultEventHandler = defaultEventHandler;");
			sb.append(componentSB.toString());
			sb.append("});");
		}
		else {
			sb.append(componentSB.toString());
		}

		return sb.toString();
	}

}