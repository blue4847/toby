package toby.study.service;

import toby.study.domain.Level;
import toby.study.domain.User;

/**
 * User's Level upgrade policy class
 *
 * BASIC -> SILVER : Login count >= 50
 *
 * SILVER -> GOLD : LEVEL is BASIC, recommended count > 30
 *
 * GOLD -> there is no upgrade and return false
 */
public class UserLevelUpgradePolicyLoginCountAndRecommend implements UserLevelUpgradePolicy{

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;

    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= this.MIN_LOGCOUNT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= this.MIN_RECOMMEND_FOR_GOLD);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level : " + currentLevel);
        }
    }

    public void upgradeLevels(User user) {
        user.upgradeLevel();
    }
}
