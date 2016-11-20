package toby.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * interface for strategy pattern
 * @author blue4
 */
public interface ConnectionMaker {

	public Connection makeConnection() throws ClassNotFoundException, SQLException;

}
