package toby.study.service;

import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;


import java.util.List;

/**
 * Userに関するサービスを提供
 */
public class UserService {

    private UserDao userDao;

    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if(userLevelUpgradePolicy.canUpgradeLevel(user)){
                userLevelUpgradePolicy.upgradeLevels(user);
                userDao.update(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
