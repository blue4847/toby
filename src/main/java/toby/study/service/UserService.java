package toby.study.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;

import javax.sql.DataSource;
import java.util.List;

/**
 * Userに関するサービスを提供
 */
public class UserService {

    private UserDao userDao;

    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() throws Exception {

        /** start transaction synchronization */

        /** get current transaction-status */
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                    userLevelUpgradePolicy.upgradeLevel(user);
                    userDao.update(user);
                }
            }
            this.transactionManager.commit(status);
        } catch (Exception e) {
            /** rollback */
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
