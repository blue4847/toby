package toby.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import toby.study.domain.User;

/**
 * Application Component<br>
 * @author blue4
 */
public class UserDao {

	private String INSERT = "INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)";
	private String SELECT = "select * from users where id = ?";

	/** DI property area */
	/** UserDao의 관심영역 외의 로직, Object는 외부로부터 DI받아 사용 */
	/** ConnectionMaker interface */
	private ConnectionMaker connectionMaker;

	/** dataSource interface */
	private DataSource dataSource;

	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** Business logic area */
	/**
	 * Add User Scheme
	 * @param user
	 * @throws SQLException
	 */
	public void add(User user) throws SQLException {

		// Connection from DataSource
		Connection c = dataSource.getConnection();
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
	public User get(String id) throws SQLException {
		
		// Connection from dataSource
		Connection c = dataSource.getConnection(); 
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
