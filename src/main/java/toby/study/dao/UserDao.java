package toby.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import toby.study.domain.User;

public class UserDao {

	private String INSERT = "INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)";
	private String SELECT = "select * from users where id = ?";

	/**
	 * 커넥션 메이커 선언
	 */
	private ConnectionMaker connectionMaker;

	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	/**
	 * Add User Scheme
	 * @param user
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void add(User user) throws ClassNotFoundException, SQLException {

		Connection c = connectionMaker.makeConnection();
		PreparedStatement ps = c.prepareStatement(INSERT);
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());

		ps.executeUpdate();

		ps.close();

		c.close();
	}

	/**
	 * Get User Scheme
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */ 
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = connectionMaker.makeConnection();
		PreparedStatement ps = c.prepareStatement(SELECT);
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));

		rs.close();
		ps.close();
		c.close();

		return user;
	}

}
