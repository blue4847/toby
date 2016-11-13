package toby.study.dao;

// singleton pattern practice
public class SingletonePattern {
	private static SingletonePattern INSTANCE;

	private SingletonePattern() {
	}

	public static synchronized SingletonePattern getInstance() {
		if (INSTANCE == null)
			INSTANCE = new SingletonePattern();
		return INSTANCE;
	}
}
