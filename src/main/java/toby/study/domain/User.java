package toby.study.domain;

/** UserTable model */
public class User {

	/**
	 * ID
	 */
	private String id;
	
	/**
	 * NAME
	 */
	private String name;
	
	/**
	 * PASSWORD
	 */
	private String password;

	/**
	 * get ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * set ID
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
