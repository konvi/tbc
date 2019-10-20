package konvi.utils.tbc.domain;

import com.atlassian.jira.issue.worklog.Worklog;

import java.util.HashMap;
import java.util.Map;

public class Summary {

	private Map<Activity, Map<String, TimeLogged>> activityToDevToTime = new HashMap<>();

	public void addWorklog(Worklog worklog) {
		Activity activity = WorklogService.getActivity(worklog);

		Map<String, TimeLogged> timeByDev = activityToDevToTime.computeIfAbsent(activity, key -> new HashMap<>());
		TimeLogged timeLogged = timeByDev.computeIfAbsent(worklog.getAuthorObject().getDisplayName(), key -> new TimeLogged());
		timeLogged.addTime(worklog.getTimeSpent());
		timeLogged.addActivity(activity);
	}

	public Map<Activity, Map<String, TimeLogged>> getActivityToDevToTime() {
		return activityToDevToTime;
	}
}
