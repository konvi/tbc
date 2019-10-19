package konvi.utils.tbc.rest;

//import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A resource of message.
 */
@Named
@Path("/tbc_page")
public class WorklogOverviewPageResource {

	private final WorklogManager worklogManager;

	@Inject
	public WorklogOverviewPageResource(WorklogManager worklogManager) {
		this.worklogManager = worklogManager;
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON/*, MediaType.APPLICATION_XML*/})
	public Response getUsersForWeek(/*@QueryParam("start") final String start,
            @QueryParam("end") final String end*/) {
		// AUTHENTICATION
//        if (!this.isUserLoggedIn()) {
//            return getUnauthorizedErrorResponse();
//        }
		// BUSINESS LOGIC
		// 17/Jul/19 10:54 PM - 22/Aug/19


//        Long since = LocalDate.parse(start).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
//        Long to = LocalDate.parse(start).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();

		Long since = LocalDate.of(2019, 9, 23).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		Long to = LocalDate.of(2019, 10, 18).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		List<Worklog> worklogs = getWorklogsUpdated(since, to);

		Map<String, Map<String, Long>> timeByDevByItem = new HashMap<>();
		Map<String, Long> estimationByItem = new HashMap<>();
		Set<String> devs = new HashSet<>();
		for (Worklog worklog : worklogs) {
			Map<String, Long> timeByDev = timeByDevByItem.computeIfAbsent(worklog.getIssue().getKey(), key -> new HashMap<>());
			if (timeByDev.containsKey(worklog.getAuthorObject().getDisplayName())) {
				timeByDev.put(worklog.getAuthorObject().getDisplayName(),
						timeByDev.get(worklog.getAuthorObject().getDisplayName()) + worklog.getTimeSpent());
			} else {
				timeByDev.put(worklog.getAuthorObject().getDisplayName(), worklog.getTimeSpent());
			}
			devs.add(worklog.getAuthorObject().getDisplayName());
			estimationByItem.put(worklog.getIssue().getKey(), worklog.getIssue().getOriginalEstimate());
		}

		List<String> devsAlphabetically = new ArrayList<>(devs);
		Collections.sort(devsAlphabetically);

		List<WorklogOverviewPageResourceModel> responseList = new ArrayList<>();
		for (String item : timeByDevByItem.keySet()) {
			responseList.add(new WorklogOverviewPageResourceModel(
					item,
					estimationByItem.get(item),
					timeByDevByItem.get(item),
					devsAlphabetically));
		}

		return Response.ok(responseList).build();
	}

	private List<Worklog> getWorklogsUpdated(final Long sinceInMilliseconds, final Long toInMilliseconds) {
		List<Worklog> result = new ArrayList<>();
		long pageStart = sinceInMilliseconds;
		do {
			final List<Worklog> worklogsUpdatedSince = worklogManager.getWorklogsUpdatedSince(pageStart);
			boolean needMore = true;

			for (Worklog worklog : worklogsUpdatedSince) {
				if (toInMilliseconds >= worklog.getUpdated().getTime()) {
					result.add(worklog);
				} else {
					needMore = false;
					break;
				}
			}
			if (!needMore || worklogsUpdatedSince.size() < WorklogManager.WORKLOG_UPDATE_DATA_PAGE_SIZE) {
				break;
			}
			pageStart = worklogsUpdatedSince.get(worklogsUpdatedSince.size() - 1).getUpdated().getTime();
		} while (pageStart < toInMilliseconds);

		return result;
	}
}