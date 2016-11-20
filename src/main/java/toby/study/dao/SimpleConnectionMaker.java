package toby.study.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection을 제공하는 Component<br>
 * @author blue4
 */
public class SimpleConnectionMaker implements ConnectionMaker {

	protected String DRIVER = "org.mariadb.jdbc.Driver";
	protected String URL = "jdbc:mariadb://localhost:{port-No}:{db-name}";

	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		Class.forName(DRIVER);
		Connection c = DriverManager.getConnection(URL, "{id}", "{pw}");
		return c;

	}

}
