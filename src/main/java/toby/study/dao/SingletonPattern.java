package toby.study.dao;

/**
 * Singleton pattern practice
 * @author blue4
 *
 */
public class SingletonPattern {
	private static SingletonPattern INSTANCE;

	/** private constructor */
	private SingletonPattern() {
	}

	/** get singleton bean instance */
	public static synchronized SingletonPattern getInstance() {
		// if there is no exist instance of singleton object
		if (INSTANCE == null)
			// create new instance
			INSTANCE = new SingletonPattern();
		
		// return singleton object instance
		return INSTANCE;
	}
}
