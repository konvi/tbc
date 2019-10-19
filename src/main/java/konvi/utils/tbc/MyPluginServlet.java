package konvi.utils.tbc;

import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
 * The first version of TBC: servlet-based.
 */
@Named
@Deprecated
public class MyPluginServlet extends HttpServlet {

	private final WorklogManager worklogManager;

	@Inject
	public MyPluginServlet(WorklogManager worklogManager) {
		this.worklogManager = worklogManager;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// 17/Jul/19 10:54 PM - 22/Aug/19
		Long since = LocalDate.of(2019, 9, 23).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		Long to = LocalDate.of(2019, 10, 18).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		List<Worklog> worklogs = getWorklogsUpdated(since, to);

		response.setContentType("text/html");

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
//			response.getWriter().write(" " + worklog.getAuthorObject().getDisplayName() + " " +
//					worklog.getIssue() + "  " + worklog.getTimeSpent() + "\r\n");
		}

		response.getWriter().write("<table>");
		response.getWriter().write("<thead><tr><th>Item</th><th>Estimation</th>");

		List<String> devsAlphabetically = new ArrayList<>(devs);
		Collections.sort(devsAlphabetically);
		for (String dev : devsAlphabetically) {
			response.getWriter().write("<th>" + dev + "</th>");
		}
		response.getWriter().write("</th></thead><tbody>");
		for (String item : timeByDevByItem.keySet()) {
			response.getWriter().write("<tr><td>" + item + "</td><td>" + estimationByItem.get(item) + "</td>");
			Map<String, Long> timeByDev = timeByDevByItem.get(item);
			devsAlphabetically.forEach(dev -> {
				try {
					response.getWriter().write("<td>" + (timeByDev.get(dev) == null ? "" : timeByDev.get(dev)) + "</td>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		response.getWriter().write("</table>");
	}

	private List<Worklog> getWorklogsUpdated(final Long sinceInMilliseconds, final Long toInMilliseconds) {
		List<Worklog> result = new ArrayList<>();
		long pageStart = sinceInMilliseconds;
		do
		{
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
