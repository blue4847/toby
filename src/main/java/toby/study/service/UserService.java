package toby.study.service;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;


import javax.sql.ConnectionEvent;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * Userに関するサービスを提供
 */
public class UserService {

    private UserDao userDao;

    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    private DataSource dataSource;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {

        /** start transaction synchronization */
        // initialize synchronization
        TransactionSynchronizationManager.initSynchronization();
        // get a connection from Spring-connection-repository by DataSourceUtils
        Connection c = DataSourceUtils.getConnection(dataSource);
        // start transaction
        c.setAutoCommit(false);

        try{
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                    userLevelUpgradePolicy.upgradeLevel(user);
                    userDao.update(user);
                }
            }
            c.commit();
        }
        catch (Exception e){
            /** rollback */
            c.rollback();
            throw e;
        }
        finally{
            // close connection
            DataSourceUtils.releaseConnection(c, dataSource);
            /** end transaction synchronization */
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }

    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
