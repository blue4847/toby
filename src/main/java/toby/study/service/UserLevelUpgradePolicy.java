package toby.study.service;

import toby.study.domain.User;

/**
 * User policy interface for upgrading level
 */
public interface UserLevelUpgradePolicy {

    /**
     * @param user
     * @return T : Upgrade 가능, F : Upgrade 불가
     */
    boolean canUpgradeLevel(User user);

	/**
	 * user의 Level업그레이드
	 * @param user
	 */
    void upgradeLevel(User user);
}
