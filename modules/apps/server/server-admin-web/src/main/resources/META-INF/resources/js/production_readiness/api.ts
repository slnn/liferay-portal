/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createResourceURL, fetch} from 'frontend-js-web';

import {ResultsPayload} from './types';

const RESOURCE_ID_GET_RESULTS =
	'/server_admin/get_production_readiness_results';
const RESOURCE_ID_IGNORE_RULE =
	'/server_admin/ignore_production_readiness_rule';
const RESOURCE_ID_UNIGNORE_RULE =
	'/server_admin/unignore_production_readiness_rule';

function buildURL(baseResourceURL: string, resourceId: string): string {
	return createResourceURL(baseResourceURL, {
		p_p_resource_id: resourceId,
	}).toString();
}

export async function fetchResults(
	baseResourceURL: string
): Promise<ResultsPayload> {
	const response = await fetch(
		buildURL(baseResourceURL, RESOURCE_ID_GET_RESULTS)
	);

	if (!response.ok) {
		throw new Error(`HTTP ${response.status}`);
	}

	return response.json();
}

export async function ignoreRule(
	baseResourceURL: string,
	ruleKey: string,
	reason: string
): Promise<void> {
	const formData = new FormData();

	formData.append('reason', reason);
	formData.append('ruleKey', ruleKey);

	const response = await fetch(
		buildURL(baseResourceURL, RESOURCE_ID_IGNORE_RULE),
		{
			body: formData,
			method: 'POST',
		}
	);

	if (!response.ok) {
		throw new Error(`HTTP ${response.status}`);
	}
}

export async function unignoreRule(
	baseResourceURL: string,
	ruleKey: string
): Promise<void> {
	const formData = new FormData();

	formData.append('ruleKey', ruleKey);

	const response = await fetch(
		buildURL(baseResourceURL, RESOURCE_ID_UNIGNORE_RULE),
		{
			body: formData,
			method: 'POST',
		}
	);

	if (!response.ok) {
		throw new Error(`HTTP ${response.status}`);
	}
}
