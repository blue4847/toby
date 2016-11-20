package toby.study.domain;

/** UserTable model */
public class User {
	
	public User( String id, String name, String password){
		this.id = id;
		this.name = name;
		this.password = password; 
	}
	
	/** 
	 * default constructor
	 * 자바빈의 규약을 따르는 클래스에 생성자를 명시적으로 추가했을 때는
	 * 파라미터가 없는 디폴트 생성자도 함께 정의해 주도록 할 것.
	 */
	public User(){
	}

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
