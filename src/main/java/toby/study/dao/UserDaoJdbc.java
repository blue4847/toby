package toby.study.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import toby.study.domain.Level;
import toby.study.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Application Component<br>
 *
 * @author blue4
 */
public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;

    /**
     * callback to create User object from ResultSet
     */
    private RowMapper<User> userMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("ID"));
            user.setName(rs.getString("NAME"));
            user.setPassword(rs.getString("PASSWORD"));
            user.setLevel(Level.valueOf(rs.getInt("LEVEL")));
            user.setLogin(rs.getInt("LOGIN"));
            user.setRecommend(rs.getInt("RECOMMEND"));
            return user;
        }
    };

    /**
     * DataSource setter
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Add User Scheme
     *
     * @param user
     */
    public void add(final User user) throws DuplicateKeyException {
        this.jdbcTemplate.update("INSERT INTO USERS ( ID, NAME, PASSWORD, LEVEL, LOGIN, RECOMMEND) VALUES (?, ?, ?, ?, ?, ?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
    }

    /**
     * Get User Scheme
     *
     * @param id
     * @return
     */
    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                "SELECT * FROM USERS WHERE ID = ?"
                , new Object[]{id}
                , this.userMapper
        );
    }

    /**
     * get all users table records
     *
     * @return
     */
    public List<User> getAll() {
        return this.jdbcTemplate.query(
                "SELECT * FROM USERS ORDER BY ID"
                , this.userMapper
        );
    }

    /**
     * delete all data of users table
     */
    public void deleteAll() {
        this.jdbcTemplate.update("DELETE FROM USERS");
    }

    /**
     * get number of user
     *
     * @return
     */
    public int getCount() {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
    }

    public void update(User user) {
        this.jdbcTemplate.update("UPDATE USERS SET NAME = ?, PASSWORD = ?, LEVEL = ?, LOGIN = ?, RECOMMEND = ? WHERE ID = ?"
                , user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
    }
}
