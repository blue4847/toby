package toby.study.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import toby.study.dao.UserDao;
import toby.study.dao.UserDaoJpa;
import toby.study.domain.Level;
import toby.study.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static toby.study.service.UserLevelUpgradePolicyLoginCountAndRecommend.MIN_LOGCOUNT_FOR_SILVER;
import static toby.study.service.UserLevelUpgradePolicyLoginCountAndRecommend.MIN_RECOMMEND_FOR_GOLD;

/**
 * UserService Test class
 *
 * UserDao : UserDaoJpa
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"})
public class UserServiceJpaTest {

    @Autowired
    @Qualifier("userServiceJpa")
    UserService userService;

    @Autowired
    @Qualifier("userDaoJpa")
    UserDao userDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
        assertThat(true, is(this.userDao instanceof UserDaoJpa));
    }

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("homuhomu", "ほむほむ", "pw01", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "homu@study.com")
                , new User("mamimami", "まみまみ", "pw02", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER , 0, "mami@email.test")
                , new User("madomado", "まどまど", "pw03", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD -1, "mado@mail.e")
                , new User("ruliruli", "ルリルリ", "pw04", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "ruli@web.com")
                , new User("mikumiku", "ミクミク", "pw05", Level.GOLD, 100, Integer.MAX_VALUE, "miku@miku.miku")
        );
    }

    @Test
    public void upgradeLevel() throws Exception{
        userService.deleteAll();
        for (User user : users)
            userService.add(user);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false); 
    }

	/**
	 * Level 확인용 메소드
	 */
    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userService.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

	/**
	 * Level 업그레이드 여부 확인용 메소드
	 */
    private void checkLevelUpgraded( User user, boolean upgraded){
        User userUpdate = userService.get(user.getId());
        if( upgraded){
            assertThat( userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        }
        else{
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }

    }

    @Test
    public void add() throws Exception{
        userService.deleteAll();

        User userWithLevel = users.get(4);

        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userService.get(userWithLevel.getId());
        User userWithoutLevelRead = userService.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));

    }

	/**
	 * UserService의 Transaction 여부를 확인
	 */
    @Test
    public void upgradeAllOrNothing() throws Exception{ 

		/**
		 * Transaction테스트를 위한 수정된 UserService를 생성
		 * 다른 테스트에 영향을 주지 않기위해 Spring 관리 UserService가 아닌 별도의 객체를 생성한다.
		 */
        UserLevelUpgradePolicy transactionPolicy = new TestPolicyUserUpgradeTransaction( users.get(3).getId());
        UserService transactionService = new UserService();
        transactionService.setUserDao(userDao);
        transactionService.setTransactionManager(this.transactionManager);
        transactionService.setUserLevelUpgradePolicy(transactionPolicy);

        userService.deleteAll();
        for( User user: users)
            userService.add(user);

        try{
			// Level Upgrade 실시
            transactionService.upgradeLevels();

			// Transaction 예외가 발생하지 않을 경우, 테스트 실패
            fail("TestUserServiceException expected");
        }
        catch(TestUserServiceException e){
        }
		// Rollback 확인
        checkLevelUpgraded(users.get(1), false); 
    }

    /**
     * user-upgrade policy for transaction test
     */
    static class TestPolicyUserUpgradeTransaction extends UserLevelUpgradePolicyLoginCountAndRecommend{

		/**
		 * 테스트에 사용 될 예외 대상 ID
		 */
        private String id;

		/**
		 * 테스트에 사용 될 예외 대상 ID를 지정한다.
		 */
        private TestPolicyUserUpgradeTransaction(String id){
            this.id = id;
        }

		/**
		 * 테스트 전용 메소드로서, 생성시 설정한 ID와 동일한 ID의 User를 발견할 경우 예외를 발생시킨다.
		 */
		@Override
        public void upgradeLevel(User user){
            if( user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    /**
     * temp-exception for transaction test
     */
    static class TestUserServiceException extends RuntimeException{
    }



}
