package toby.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * UserDao에 Template method pattern
 * @author blue4
 *
 */
public class UserDaoDeleteAll extends UserDao {

	/**
	 * UserDao클래스의 기능을 확장하고 싶을 때마다 기능 메소드를 추상 메소드로 선언 후,
	 * 서브 클래스에서 해당 기능을 구현한다.
	 */
	
	/**
	 * Good : 
	 * OCP는 지키는 구조를 구현
	 * 
	 * Bad :
	 * number of method(logic) <= number of class
	 * 확장 구조가 클래스를 설계하는 시점에서 고정되어 버림
	 */
	
	
	protected PreparedStatement makeStatement(Connection c) throws SQLException{
		PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
		return ps; 
	}

}
