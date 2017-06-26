package toby.study.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import toby.study.dao.MockMailSender;
import toby.study.dao.MockUserDao;
import toby.study.dao.UserDao;
import toby.study.dao.UserDaoJpa;
import toby.study.domain.Level;
import toby.study.domain.User;
import toby.study.handler.TransactionHandler;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static toby.study.service.UserLevelUpgradePolicyLoginCountAndRecommend.MIN_LOGCOUNT_FOR_SILVER;
import static toby.study.service.UserLevelUpgradePolicyLoginCountAndRecommend.MIN_RECOMMEND_FOR_GOLD;

/**
 * UserService Test class
 *
 * UserDao : UserDaoJpa
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"
        , "/META-INF/toby-study-test-context.xml"})
public class UserServiceJpaTest {

	// UserService의 Transaction경계 코드를 가진 구현체
	// UserServiceImpl을 DI받아 UserServiceImpl을 사용하게 된다.
    @Autowired
    @Qualifier("userServiceJpa")
    UserService userService;

    @Autowired
    @Qualifier("userServiceImplJpa")
    UserServiceImpl userServiceImpl;

    @Autowired
    @Qualifier("transactionManagerJpa")
    PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("userDaoJpa")
    UserDao userDao;

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
        assertThat(true, is(this.userDao instanceof UserDaoJpa));
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

	/**
	 * upgradeLevels의 비즈니스 로직만을 테스트하기 위해 고립된 테스트로 작성한다.
	 * 고립된 테스트에서는 테스트 대상이 의존하고 있는 대상을 분리시키고, 철저히 테스트 로직만의 동작으로 움직이도록 한다.
	 * 이를 위해 필요한 의존 대상의 Mock오브젝트를 만들어 주입시킨다.
	 * 단위 테스트만을 위해 배제하거나 대체된 클래스는 다음과 같다
	 *
	 * MockUserDao : Users 테이블로의 데이터 액세스 로직 UserDao의 Mock 클래스
	 * MockMailSender : 이메일 발송 시 사용되는 MailSender의 Mock 클래스
	 * UserServiceTx : DB와의 Transaction 경계 설정을 위한 UserService 구현 클래스
	 */
    @Test
    public void upgradeLevels() throws Exception{

		/**
		 * 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
		 */ 
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		/**
		 * 목 오브젝트로 만든 UserDao를 직접 DI 해준다.
		 */
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		userServiceImpl.setUserLevelUpgradePolicy(new UserLevelUpgradePolicyLoginCountAndRecommend());

		/**
		 * 메일 발송 결과를 테스트 할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입해준다.
		 */
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

		/**
		 * 업그레이드 테스트.
		 * 메일 발송이 일어나면 MockMailSender 오브젝트의 리스트에 그 결과가 저장된다.
		 */
        userServiceImpl.upgradeLevels();

		// MockUserDao로부터 업데이트 결과를 가져온다.
		List<User> updated = mockUserDao.getUpdated();

		/**
		 * 업데이트 횟수와 정보를 확인한다.
		 */
		assertThat(updated.size(), is(2));
		checkUserAndLevel( updated.get(0), users.get(1).getId(), Level.SILVER);
		checkUserAndLevel( updated.get(1), users.get(3).getId(), Level.GOLD);

		/**
		 * 목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인한다.
		 */
        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));

    }

	/**
	 * Mock Framework를 사용한 upgradeLevels 테스트.
	 * Mockito 프레임워크를 사용하여 별도의 목 오브젝트의 구현 없이도 생성, 사용, 검증할 수 있다.
	 */
    @Test 
    public void mockUpgradeLevels() throws Exception{
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserLevelUpgradePolicy(new UserLevelUpgradePolicyLoginCountAndRecommend());

        /**
         * 다이나믹한 목 오브젝트 생성과 메소드의 리턴 값 설정, 그리고 DI까지 3줄이면 충분하다.
         */
		// 아무런 기능이 없는 목 오브젝트의 생성
        UserDao mockUserDao = mock(UserDao.class);
		// getAll()에 사용자 목록을 리턴하도록 스텁 기능을 추가해준다.
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        /**
         * 리턴 값이 없는 메소드를 가진 목 오브젝트는 더욱 간단하게 만들 수 있다.
         */
        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

		/**
		 * 목 오브젝트가 제공하는 검증 기능을 통해서 어떤 메소드가 몇 번 호출됐는지,
		 * 파라미터는 무엇인지 확인할 수 있다.
		 */
		// update() 메소드가 총 2번 호출되었음을 확인함.
        verify(mockUserDao, times(2)).update(any(User.class ));
		// update() 메소드에 "mamimami" user가 파라미터로서 사용됐음을 확인한다.
        verify(mockUserDao).update(users.get(1));
		// "mamimami" user의 Level이 업그레이드 되어, BASIC 에서 SILVER로 업그레이드 됨을 확인한다.
        assertThat(users.get(1).getLevel(), is(Level.SILVER));

		// update() 메소드에 "ruliruli" user가 파라미터로서 사용됐음을 확인한다.
        verify(mockUserDao).update(users.get(3));
		// "ruliruli" user의 Level이 업그레이드 되어, SILVER 에서 GOLD로 업그레이드 됨을 확인한다.
        assertThat(users.get(3).getLevel(), is(Level.GOLD));


        // 파라미터를 정밀하게 검사하기 위해 캡쳐할 수도 있다.
        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		// 보내진 메일 리스트와 각 메일의 주소가 업그레이드 대상과 일치하는지 확인한다.
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));

    }
	/**
	 * Level 확인용 메소드.
	 */
    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userService.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

	/**
	 * id와 Level을 확인하는 간단한 헬퍼 메소드.
	 */
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel)); 
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
	 * UserService의 Transaction 여부를 확인.
	 * UserService interface를 직접 구현한 proxy 클래스를 사용.
	 */
    @Test
    public void upgradeAllOrNothing() throws Exception{

        upgradeAllOrNothing(userServiceImpl -> {
            // UserService의 Transaction 처리용 클래스
            UserServiceTx transactionServiceTx = new UserServiceTx();

            // UserService의 비즈니스 로직 구현체를 DI받은 후, Tx클래스를 통해 서비스를 실행한다.
            transactionServiceTx.setUserService(userServiceImpl);
            transactionServiceTx.setTransactionManager(transactionManager);
            return transactionServiceTx;
        });
    }

	/**
	 * UserService의 Transaction 여부를 확인.
	 * dynamic proxy를 사용하여 구현.
	 */
    @Test
    public void upgradeAllOrNothingDynamicProxy() throws Exception{

        upgradeAllOrNothing(userServiceImpl ->{
            TransactionHandler txHandler = new TransactionHandler();
            txHandler.setTarget(userServiceImpl);
            txHandler.setTransactionManager(transactionManager);

            // transaction 경계설정이 필요한 메소드 패턴을 추가한다.
            Set<String> patterns = Stream.of("upgradeLevels", "deleteAll", "add")
                    .collect(Collectors.toCollection(HashSet::new));
            txHandler.setPatterns(patterns);

            // dynamic proxy를 생성한다.
            UserService txUserService = (UserService) Proxy.newProxyInstance(
                    // 동적으로 생성되는 dynamic proxy 클래스의 로딩에 사용할 클래스 로더
                    getClass().getClassLoader()
                    // 구현할 인터페이스
                    // dynamic proxy는 한 번에 하나 이상의 interface를 구현할 수도 있다
                    , new Class[]{ UserService.class}
                    // 부가기능과 위임 코드를 담은 InvocationHandler
                    , txHandler
            );
            return txUserService;
        });

    }

    /**
     * upgradeLevels의 transaction 처리 테스트를 위한 Callback 인터페이스.
	 * 각 테스트에 사용하고자 하는 transaction 경계처리 구현 클래스를 반환한다.
	 * 괜히 심심해서 callback-template 패턴으로 만들었다.
     */
    private interface UpgradeAllOrNothingCallback{

		/**
		 * UserService의 transaction 경계처리 기능을 담은 proxy를 반환한다.
		 */
        UserService getUserServiceTx(UserServiceImpl userServiceImpl);
    }

    /**
     * upgradeLevels의 transaction 처리 테스트를 위한 템플릿 메소드.
	 * 각 테스트에 사용하고자 하는 transaction 경계처리 구현 클래스를 이용하여 transaction 처리를 테스트한다.
	 * 괜히 심심해서 callback-template 패턴으로 만들었다.
     * @param callback
     * @throws Exception
     */
    private void upgradeAllOrNothing( UpgradeAllOrNothingCallback callback) throws Exception{

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

        // UserService의 Transaction 경계처리 기능 구현 proxy 클래스
        UserService transactionServiceTx = callback.getUserServiceTx(transactionServiceImpl);


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
	 * Transaction테스트를 위한 수정된 UserService를 생성
	 * 다른 테스트에 영향을 주지 않기위해 Spring 관리 UserService가 아닌 별도의 객체를 생성한다.
	 */
	private UserServiceImpl transactionTestUserServiceImpl(){
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

		return transactionServiceImpl; 
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
