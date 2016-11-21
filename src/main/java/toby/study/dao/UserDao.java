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

	private String INSERT = "INSERT INTO USERS(ID, NAME, PASSWORD) VALUES(?,?,?)";
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
	public void add( User user) throws SQLException {

		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement(INSERT);
			ps.setString(1, user.getId());
			ps.setString(2, user.getName());
			ps.setString(3, user.getPassword());

			ps.executeUpdate();
			
		}
		catch(SQLException se){
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

		Connection c = null;
		PreparedStatement ps = null;

		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("DELETE FROM USERS");
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

}
