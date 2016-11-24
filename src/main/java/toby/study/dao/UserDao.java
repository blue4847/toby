package toby.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import toby.study.domain.User;

/**
 * Application Component<br>
 * 
 * @author blue4
 */
public class UserDao {

	private String SELECT = "select * from users where id = ?";

	/** DI property area */
	private DataSource dataSource;

	public void setDataSource( DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** Business logic area */
	/**
	 * Add User Scheme
	 * 
	 * @param user
	 * @throws SQLException
	 */
	public void add( final User user) throws SQLException {
		/** micro DI */

		// Create object of strategy made for this method
		/**
		 * 내부클래스로 정의함으로서 메소드 레벨에서 사용되는 변수를 사용할 수 있다.
		 * 따라서 기존의 생성자를 통해 강제했던 변수 전달을 생략할 수 있다.
		 * 단, 내부 클래스에서 외부의 변수를 사용할 때는 외부 변수를 반드시 final로 선언해주어야 한다.
		 * 
		 * @author blue4
		 */ 
		this.jdbcContextWithStatementStrategy(
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement( Connection c) throws SQLException {
						PreparedStatement ps = c.prepareStatement("INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)");

						ps.setString(1, user.getId());
						ps.setString(2, user.getName());
						ps.setString(3, user.getPassword());

						return ps;
					}
				});

	}

	/**
	 * Get User Scheme
	 * 
	 * @param id
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User get( String id) throws SQLException {

		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement(SELECT);
			ps.setString(1, id);
			rs = ps.executeQuery();

			User user = null;
			if( rs.next()){
				user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
			// 습득 데이터가 없는지 확인 후, 예외를 던져준다
			if( user == null)
				throw new EmptyResultDataAccessException(1);

			return user;
		}
		catch( SQLException se){
			throw se;
		}
		finally{
			if( rs != null){
				try{
					rs.close();
				}
				catch( SQLException se){
				}
			}
			if( ps != null){
				try{
					ps.close();
				}
				catch( SQLException se){
				}
			}
			if( c != null){
				try{
					c.close();
				}
				catch( SQLException se){
				}
			}
		}
	}

	public void deleteAll() throws SQLException {
		/** micro DI */

		this.jdbcContextWithStatementStrategy(
				new StatementStrategy(){
					public PreparedStatement makePreparedStatement( Connection c) throws SQLException {
						return c.prepareStatement("DELETE FROM USERS");
					}
				});

	}

	public int getCount() throws SQLException {

		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("SELECT COUNT(*) FROM USERS");
			rs = ps.executeQuery();
			rs.next();
			int count = rs.getInt(1);

			return count;
		}
		catch( SQLException se){
			throw se;
		}
		finally{
			if( rs != null){
				try{
					rs.close();
				}
				catch( SQLException se){
				}
			}
			if( ps != null){
				try{
					ps.close();
				}
				catch( SQLException se){
				}
			}
			if( c != null){
				try{
					c.close();
				}
				catch( SQLException se){
				}
			}
		}
	}

	public void jdbcContextWithStatementStrategy( StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try{
			c = dataSource.getConnection();

			ps = stmt.makePreparedStatement(c);

			ps.executeUpdate();
		}
		catch( SQLException se){
			throw se;
		}
		finally{
			if( ps != null){
				try{
					ps.close();
				}
				catch( SQLException se){
				}
			}
			if( c != null){
				try{
					c.close();
				}
				catch( SQLException se){
				}
			}
		}

	}

}
