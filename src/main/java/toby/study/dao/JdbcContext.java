package toby.study.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Template class to excute sql
 */
public class JdbcContext {
    private DataSource dataSource;

    /**
     * DataSource타입 빈을 주입받을 수 있도록 준비해 둔다.
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeSql( final String query) throws SQLException{
        workWithSattementStrategy(
                new StatementStrategy() {
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        return c.prepareStatement(query);
                    }
                }
        );
    }


    public void workWithSattementStrategy( StatementStrategy stmt) throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try{
            c = this.dataSource.getConnection();
            ps = stmt.makePreparedStatement( c);

            ps.executeUpdate();
        }
        catch( SQLException e){
            throw e;
        }
        finally{
            if( ps != null){ try{ ps.close();} catch( SQLException e){}}
            if( c != null){ try{ c.close();} catch( SQLException e){}}
        }
    }
}

