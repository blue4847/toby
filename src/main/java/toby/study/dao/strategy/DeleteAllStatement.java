package toby.study.dao.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import toby.study.dao.StatementStrategy;

public class DeleteAllStatement  implements StatementStrategy{
	
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException{
		
		PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
		
		return ps;

	}

}
