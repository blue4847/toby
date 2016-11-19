package toby.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import toby.study.domain.User;

public class UserDao {

	private String INSERT = "INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)";
	private String SELECT = "select * from users where id = ?";

	/** ConnectionMaker interface */
	private ConnectionMaker connectionMaker;

	/** dataSource interface */
	private DataSource dataSource;

	public ConnectionMaker getConnectionMaker() {
		return connectionMaker;
	}

	public void setConnectionMaker(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Add User Scheme
	 * 
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
	 * 
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User get(String id) throws ClassNotFoundException, SQLException {
		
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
