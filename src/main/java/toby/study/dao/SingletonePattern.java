package toby.study.dao;

// singleton pattern practice
public class SingletonePattern {
	private static SingletonePattern INSTANCE;

	/** private constructor */
	private SingletonePattern() {
	}

	/** get single-tone bean instance */
	public static synchronized SingletonePattern getInstance() {
		
		if (INSTANCE == null)
			// create new instance
			INSTANCE = new SingletonePattern();
		
		// return single-tone instance
		return INSTANCE;
	}
}
