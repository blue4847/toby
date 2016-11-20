package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * DataBase Connection test
 * 
 * @author blue4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml" })
public class MariaDbTest {

	@Autowired
	private ApplicationContext context;

	@Test
	public void connection(String[] args) throws SQLException {
		// set context

		// get DataSource
		DataSource dataSource = context.getBean("dataSource", DataSource.class);

		// get Connection
		Connection conn = dataSource.getConnection();

		// connection check
		boolean reachable = conn.isValid(10);
		
		// result 
		assertTrue(reachable); 
	}
}
