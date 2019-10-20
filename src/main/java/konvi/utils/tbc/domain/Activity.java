package konvi.utils.tbc.domain;

public enum Activity {
	DEV("Development"),
	MENTOR("Development"),
	CR("Code Review");

	private final String name;

	Activity(String name) {
		this.name = name;
	}
}
