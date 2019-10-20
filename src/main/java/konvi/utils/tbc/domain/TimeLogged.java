package konvi.utils.tbc.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeLogged {
	private Long time = 0L;
	private Set<Activity> activities = new HashSet<>();

	public TimeLogged() {
	}

	@JsonProperty
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}

	public void addTime(Long timeSpent) {
		time += timeSpent;
	}

	@JsonProperty
	public String getActivity() {
		return String.join(" ", activities.stream().map(a -> a.name()).collect(Collectors.toSet()));
	}
}
