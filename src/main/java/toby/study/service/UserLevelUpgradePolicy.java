package toby.study.service;

import toby.study.domain.User;

/**
 * User policy interface for upgrading level
 */
public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);

    void upgradeLevels(User user);
}
