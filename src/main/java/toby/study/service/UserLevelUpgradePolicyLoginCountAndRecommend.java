package toby.study.service;

import toby.study.domain.Level;
import toby.study.domain.User;

/**
 * User policy interface for upgrading level
 *
 * BASIC -> SILVER : Login count >= 50 *
 * SILVER -> GOLD : LEVEL is BASIC, recommended count > 30 *
 * GOLD -> there is no upgrade and return false
 */
public class UserLevelUpgradePolicyLoginCountAndRecommend implements UserLevelUpgradePolicy{

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;

    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    /**
     * @param user
     * @return T : Upgrade 가능, F : Upgrade 불가
     */
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
				// 현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킨다.
				// 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다.
                throw new IllegalArgumentException("Unknown Level : " + currentLevel);
        }
    }

	/**
	 * user의 Level업그레이드
	 * @param user
	 */
    public void upgradeLevel(User user) {
        user.upgradeLevel();
    }
}
