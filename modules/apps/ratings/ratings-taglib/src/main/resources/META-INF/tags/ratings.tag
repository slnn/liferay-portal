<%@ tag import="com.liferay.portal.kernel.util.PortalUtil" %>
<%@ tag import="com.liferay.ratings.kernel.RatingsType" %>
<%@ tag import="com.liferay.portal.kernel.util.HashMapBuilder" %>
<%@ tag import="com.liferay.trash.kernel.util.TrashUtil" %>
<%@ tag
	import="com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil" %>
<%@ tag import="com.liferay.portal.kernel.theme.ThemeDisplay" %>
<%@ tag import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ tag
	import="com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil" %>
<%@ tag import="com.liferay.portal.kernel.util.Validator" %>
<%@ tag import="com.liferay.portal.kernel.model.Group" %>
<%@ tag
	import="com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil" %>
<%@ tag import="com.liferay.portal.kernel.exception.PortalException" %>
<%@ tag import="java.util.Map" %>
<%@ tag
	import="com.liferay.ratings.taglib.internal.servlet.ServletContextUtil" %>
<%@ tag body-content="tagdependent" dynamic-attributes="true"%>
<%@ attribute name="className" required="true" rtexprvalue="true" %>
<%@ attribute name="classPK" required="true" rtexprvalue="true" type="java.lang.Long" %>
<%@ attribute name="inTrash" required="false" rtexprvalue="true" type="java.lang.Boolean" %>
<%@ attribute name="numberOfStars" required="false" rtexprvalue="true" %>
<%@ attribute name="ratingsEntry" required="false" rtexprvalue="true" type="com.liferay.ratings.kernel.model.RatingsEntry" %>
<%@ attribute name="ratingsStats" required="false" rtexprvalue="true" type="com.liferay.ratings.kernel.model.RatingsStats" %>
<%@ attribute name="type" required="false" rtexprvalue="true" %>
<%@ attribute name="url" required="false" rtexprvalue="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
	taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
	taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
	taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
	taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
	taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%

	if (inTrash == null) {
		inTrash = TrashUtil.isInTrash(className, classPK);
	}

	if (Validator.isNull(ratingsStats)) {
		ratingsStats = RatingsStatsLocalServiceUtil.fetchStats(
			className, classPK);
	}

	double result1 = 0.0;
	if (ratingsStats != null) {
		result1 = ratingsStats.getTotalScore();
	}

	int positiveVotes = (int)Math.round(result1);

	if ((ratingsEntry != null) && (ratingsStats != null)) {
		ratingsEntry = RatingsEntryLocalServiceUtil.fetchEntry(
			themeDisplay.getUserId(), className, classPK);
	}

	double userScore1 = -1.0;

	if (ratingsEntry != null) {
		userScore1 = ratingsEntry.getScore();
	}

	double userScore = userScore1;

	boolean thumbUp = false;
	if ((userScore != -1.0) && (userScore >= 0.5)) {
		thumbUp = true;
	}

	int totalEntries1 = 0;

	if (ratingsStats != null) {
		totalEntries1 = ratingsStats.getTotalEntries();
	}

	int totalEntries = totalEntries1;
	if (Validator.isNull(type)) {
		Group group = themeDisplay.getSiteGroup();
		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}
		RatingsType ratingsType = null;
		if (group != null) {
			try {
				ratingsType =
					PortletRatingsDefinitionUtil.getRatingsType(
						themeDisplay.getCompanyId(),
						group.getGroupId(), className);
			}
			catch (PortalException portalException) {

			}
		}
		if (ratingsType == null) {
			ratingsType = RatingsType.STARS;
		}

		type = ratingsType.getValue();
	}

	if (Validator.isNull(url)) {
		url = themeDisplay.getPathMain() + "/portal/rate_entry";
	}

	double result = 0;
	if (ratingsStats != null) {
		result = ratingsStats.getAverageScore();
	}

	boolean result2 = false;
	if (!inTrash) {
		Group group = themeDisplay.getSiteGroup();

		if (!group.isStagingGroup() && !group.isStagedRemotely()) {
			result2 = true;
		}
	}

	boolean result3 = false;
	if ((userScore != -1.0) && (userScore < 0.5)) {
		result3 = true;
	}

	Map<String, Object> data = HashMapBuilder.<String, Object>put(
			"className", className
		).put(
			"classPK", classPK
		).put(
			"enabled", result2
		).put(
			"initialAverageScore", result
		).put(
			"initialLiked", thumbUp
		).put(
			"initialNegativeVotes", totalEntries - positiveVotes
		).put(
			"initialPositiveVotes", positiveVotes
		).put(
			"initialTotalEntries", totalEntries
		).put(
			"inTrash", inTrash
		).put(
			"numberOfStars", numberOfStars
		).put(
			"positiveVotes", positiveVotes
		).put(
			"signedIn", themeDisplay.isSignedIn()
		).put(
			"thumbDown", result3
		).put(
			"thumbUp", thumbUp
		).put(
			"type", type
		).put(
			"url", url
		).put(
			"userScore", userScore
		).build();
%>

<liferay-util:html-top
	outputKey="com.liferay.ratings.taglib.servlet.taglib#/page.jsp"
>
	<link href='<%= PortalUtil.getStaticResourceURL(request, ServletContextUtil.getServletContext().getContextPath() + "/css/main.css") %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<c:choose>
	<c:when test="<%= type.equals(RatingsType.LIKE.getValue()) %>">
		<div>
			<button class="btn btn-outline-borderless btn-outline-secondary btn-sm" disabled type="button">
				<svg class="lexicon-icon lexicon-icon-heart">
					<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#heart" />
				</svg>
			</button>

			<react:component
				data="<%= data %>"
				module="js/components/Ratings"
				servletContext="<%= ServletContextUtil.getServletContext() %>"
			/>
		</div>
	</c:when>
	<c:when test="<%= type.equals(RatingsType.THUMBS.getValue()) %>">
		<div>
			<button class="btn btn-outline-borderless btn-outline-secondary btn-sm" disabled type="button">
				<svg class="lexicon-icon lexicon-icon-thumbs-up">
					<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#thumbs-up" />
				</svg>
			</button>

			<button class="btn btn-outline-borderless btn-outline-secondary btn-sm" disabled type="button">
				<svg class="lexicon-icon lexicon-icon-thumbs-down">
					<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#thumbs-down" />
				</svg>
			</button>

			<react:component
				data="<%= data %>"
				module="js/components/Ratings"
				servletContext="<%= ServletContextUtil.getServletContext() %>"
			/>
		</div>
	</c:when>
	<c:when test="<%= type.equals(RatingsType.STARS.getValue()) %>">
		<div>
			<div class="autofit-row autofit-row-center ratings ratings-stars">
				<div class="autofit-col">
					<div class="dropdown">
						<button class="btn btn-outline-borderless btn-outline-secondary dropdown-toggle btn-sm" disabled type="button">
							<svg class="lexicon-icon lexicon-icon-star-o">
								<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#star-o" />
							</svg>

							<span>-</span>
						</button>
					</div>
				</div>

				<div class="autofit-col">
					<svg class="lexicon-icon lexicon-icon-star ratings-stars-average-icon">
						<use xlink:href="<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg#star" />
					</svg>
				</div>
			</div>

			<react:component
				data="<%= data %>"
				module="js/components/Ratings"
				servletContext="<%= ServletContextUtil.getServletContext() %>"
			/>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:ratings
			className="<%= className %>"
			classPK="<%= classPK %>"
			inTrash="<%= inTrash %>"
			ratingsEntry="<%= ratingsEntry %>"
			ratingsStats="<%= ratingsStats %>"
			type="<%= type %>"
		/>
	</c:otherwise>
</c:choose>