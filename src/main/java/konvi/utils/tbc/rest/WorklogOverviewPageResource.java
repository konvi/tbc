package konvi.utils.tbc.rest;

import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import konvi.utils.tbc.domain.Activity;
import konvi.utils.tbc.domain.Summary;
import konvi.utils.tbc.domain.TimeLogged;
import konvi.utils.tbc.domain.WorklogService;

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
import java.util.Comparator;
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
	//private final WorklogService worklogService;

	@Inject
	public WorklogOverviewPageResource(WorklogManager worklogManager/*, WorklogService worklogService*/) {
		this.worklogManager = worklogManager;
		//this.worklogService = worklogService;
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

		Map<String, Map<String, TimeLogged>> timeByDevByItem = new HashMap<>();
		Summary summary = new Summary();
		Map<String, Long> estimationByItem = new HashMap<>();

		Set<String> devs = new HashSet<>();
		for (Worklog worklog : worklogs) {
			Map<String, TimeLogged> timeByDev = timeByDevByItem.computeIfAbsent(worklog.getIssue().getKey(), key -> new HashMap<>());
			TimeLogged timeLogged = timeByDev.get(worklog.getAuthorObject().getDisplayName());
			if (timeLogged == null) {
				timeLogged = new TimeLogged();
				timeByDev.put(worklog.getAuthorObject().getDisplayName(), timeLogged);
			}
			timeLogged.addTime(worklog.getTimeSpent());
			timeLogged.addActivity(WorklogService.getActivity(worklog));

			devs.add(worklog.getAuthorObject().getDisplayName());
			estimationByItem.put(worklog.getIssue().getKey(), worklog.getIssue().getOriginalEstimate());
			summary.addWorklog(worklog);
		}

		List<String> devsAlphabetically = new ArrayList<>(devs);
		Collections.sort(devsAlphabetically);

		List<WorklogOverviewPageResourceModel> items = new ArrayList<>();
		for (String item : timeByDevByItem.keySet()) {
			items.add(new WorklogOverviewPageResourceModel(
					item,
					estimationByItem.get(item),
					timeByDevByItem.get(item),
					devsAlphabetically));
		}

		Comparator<WorklogOverviewPageResourceModel> nullsInTheEnd = (o1, o2) -> {
			if (o1.getEstimation() != null && o2.getEstimation() == null) {
				return -1;
			} else if (o1.getEstimation() == null && o2.getEstimation() != null) {
				return 1;
			} else {
				return o1.getItem().compareTo(o2.getItem());
			}
		};
		items.sort(nullsInTheEnd);

		List<WorklogOverviewPageResourceModel> summaries = new ArrayList<>();
		for (Activity activity : summary.getActivityToDevToTime().keySet()) {
			summaries.add(new WorklogOverviewPageResourceModel(
					activity.name(),
					null,
					summary.getActivityToDevToTime().get(activity),
					devsAlphabetically));
		}

		Map<String, List<WorklogOverviewPageResourceModel>> result = new HashMap<>();
		result.put("items", items);
		result.put("summary", summaries);
		return Response.ok(result).build();
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