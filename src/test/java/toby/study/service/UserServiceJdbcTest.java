package toby.study.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import toby.study.dao.MockMailSender;
import toby.study.dao.UserDao;
import toby.study.dao.UserDaoJdbc;
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
 * UserDao : UserDaoJdbc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"
        , "/META-INF/toby-study-test-context.xml"})
public class UserServiceJdbcTest {

    // UserService의 Transaction경계 코드를 가진 구현체
    // UserServiceImpl을 DI받아 UserServiceImpl을 사용하게 된다.
    @Autowired
    @Qualifier("userServiceJdbc")
    UserService userService;

    @Autowired
    @Qualifier("userServiceImplJdbc")
    UserServiceImpl userServiceImpl;

    @Autowired
    @Qualifier("transactionManagerJdbc")
    PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("userDaoJdbc")
    UserDao userDao;

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
        assertThat(true, is(this.userDao instanceof UserDaoJdbc));
    }

    /**
     * test data
     */
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

        /**
         * 메일 발송 결과를 테스트 할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입해준다.
         */
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        /**
         * 업그레이드 테스트.
         * 메일 발송이 일어나면 MockMailSender 오브젝트의 리스트에 그 결과가 저장된다.
         */
        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        // Upgrade : true, Mail send : true
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        // Upgrade : true, Mail send : true
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        /**
         * 목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인한다.
         */
        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));

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
        if( upgraded)
            assertThat( userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
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
        // 특정 유저의 아이디를 업그레이드 할 경우, 예외를 내보내도록 하는 테스트용 업그레이드 정책
        UserLevelUpgradePolicy transactionPolicy = new TestPolicyUserUpgradeTransaction( users.get(3).getId());

        // UserService의 비즈니스 로직 구현 클래스
        UserServiceImpl transactionServiceImpl = new UserServiceImpl();

        // 수정된 LevelUpgradePolicy 및 필요 객체들을 DI받는다.
        transactionServiceImpl.setUserLevelUpgradePolicy(transactionPolicy);
        transactionServiceImpl.setUserDao(userDao);

        // mock 오브젝트를 사용해서 테스트 하는 경우, 물론 XML을 통해서도 DI는 가능하지만
        // 테스트상에서 명시하기 위해 수동으로 DI해주는 것이 좋다.
        MockMailSender mockMailSender = new MockMailSender();
        transactionServiceImpl.setMailSender(mockMailSender);

        // UserService의 Transaction 처리용 클래스
        UserServiceTx transactionServiceTx = new UserServiceTx();

        // UserService의 비즈니스 로직 구현체를 DI받은 후, Tx클래스를 통해 서비스를 실행한다.
        transactionServiceTx.setUserService(transactionServiceImpl);
        transactionServiceTx.setTransactionManager(transactionManager);

        transactionServiceTx.deleteAll();
        for( User user: users)
            transactionServiceTx.add(user);

        try{
            // Level Upgrade 실시
            transactionServiceTx.upgradeLevels();

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

