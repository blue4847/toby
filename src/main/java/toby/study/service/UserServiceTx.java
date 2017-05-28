package toby.study.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.study.domain.User;

/**
 * UserService의 Transaction처리 코드를 분리시키기 위한 UserService구현 클래스
 * UserService의 실제 비즈니스 로직을 구현한 UserServiceImpl 등을 DI받은 후, Transaction템플릿 내부에서
 * 해당 UserService의 로직을 실행시키도록 한다.
 * 결과적으로 비즈니스 로직과 Transaction처리 코드를 분리시키고 맡은 로직에 집중할 수 있게 한다.
 */
public class UserServiceTx implements UserService {

	/**
	 * UserServiceTx가 작업을 위임할 다른 UserService구현체를 ID받는다.
	 */
    private UserService userService;

    /**
     * 기술에 따른 트랜젝션 경계설정 로직을 추상화 하기 위한 TransactionManager
     * JDBC, JPA등 각 기술에 맞는 TransactionManager를 DI받는다
     */
    private PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void deleteAll() throws Exception {
        this.updateTemplate(() -> {
            userService.deleteAll();
        });
    }

    @Override
    public void add(User user) throws Exception {
        this.updateTemplate(() -> userService.add(user));
    }

    @Override
    public void upgradeLevels() throws Exception {
        this.updateTemplate(() -> userService.upgradeLevels());
    }

    @Override
    public User get(String id) {
        return userService.get(id);
    }


    /**
     * Update 에 관련된 동작을 담을 Callback
     */
    private interface UpdateCallback {
        void update() throws Exception;
    }

    /**
     * transactionManager의 설정과 commit, rollback을 담은 템플릿 메소드
     *
     * @param callback
     */
    private void updateTemplate(UpdateCallback callback) throws Exception {
        /** get current transaction-status */
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            /** do Update */
            callback.update();
            /** commit */
            this.transactionManager.commit(status);
        } catch (Exception e) {
            /** rollback */
            this.transactionManager.rollback(status);
            throw e;
        }
    }


}
