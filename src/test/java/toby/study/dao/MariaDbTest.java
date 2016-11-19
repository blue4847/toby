package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


/**
 * DataBase Connection test
 * @author blue4
 */
public class MariaDbTest { 
	public static void main(String[] args) throws SQLException{
		// set context
		ApplicationContext context = new GenericXmlApplicationContext( "/META-INF/spring-connection.xml" ); 
		
		// get DataSource
		DataSource dataSource = context.getBean("dataSource", DataSource.class);
		
		// get Connection
		Connection conn = dataSource.getConnection();
		
		// connection check
		boolean reachable = conn.isValid(10);
		
		// result
		System.out.println(reachable);
		
	}
}
