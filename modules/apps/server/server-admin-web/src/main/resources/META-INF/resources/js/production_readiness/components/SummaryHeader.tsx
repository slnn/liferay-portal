/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface Props {
	failed: number;
	ignored: number;
	passed: number;
	total: number;
}

const SummaryHeader: React.FC<Props> = ({failed, ignored, passed, total}) => {
	return (
		<div data-testid="production-readiness-summary">
			<div className="small text-secondary text-uppercase">
				{Liferay.Language.get('summary')}
			</div>

			<div className="align-items-baseline d-flex">
				<span
					className="h2 mb-0 mr-1"
					data-testid="production-readiness-count-total"
				>
					{total}
				</span>

				<span className="mr-3 text-secondary">
					{Liferay.Language.get('validations')}
				</span>

				<span
					className="h2 mb-0 mr-1 text-success"
					data-testid="production-readiness-count-passed"
				>
					{passed}
				</span>

				<span className="mr-3 text-secondary">
					{Liferay.Language.get('passed')}
				</span>

				<span
					className="h2 mb-0 mr-1 text-danger"
					data-testid="production-readiness-count-failed"
				>
					{failed}
				</span>

				<span className="mr-3 text-secondary">
					{Liferay.Language.get('failed')}
				</span>

				<span
					className="h2 mb-0 mr-1 text-secondary"
					data-testid="production-readiness-count-ignored"
				>
					{ignored}
				</span>

				<span className="text-secondary">
					{Liferay.Language.get('ignored')}
				</span>
			</div>

			<div className="mt-2">
				<a
					className="text-primary"
					href="https://docs.google.com/document/d/1hlsjG1y7oZz27OjDj_g7OXa4fOHFfQ3xFWtmmLLLtAg/edit?tab=t.0#heading=h.cx1snzuu21pk"
					rel="noopener noreferrer"
					target="_blank"
				>
					{Liferay.Language.get('learn-more-abour-liferay-settings')}
				</a>
			</div>
		</div>
	);
};

export default SummaryHeader;
