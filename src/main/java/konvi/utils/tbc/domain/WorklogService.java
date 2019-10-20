package konvi.utils.tbc.domain;

import com.atlassian.jira.issue.worklog.Worklog;

public class WorklogService {

	public static Activity getActivity(Worklog worklog) {
		String comment = worklog.getComment();
		Activity activity = Activity.DEV;
		if (comment.equalsIgnoreCase("Mentoring") || comment.equalsIgnoreCase("Mentor")) {
			activity = Activity.MENTOR;
		} else if (comment.equalsIgnoreCase("Code Review") || comment.equalsIgnoreCase("CR")) {
			activity = Activity.CR;
		}
		return activity;
	}
}
