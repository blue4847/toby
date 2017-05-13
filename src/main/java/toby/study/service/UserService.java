package toby.study.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;

import java.util.List;

/**
 * Users 테이블에 대한 비지니스 로직을 다루는 Service클래스
 * 비지니스 로직과 더불어 트랜젝션 경계 설정을 다룬다
 * DAO에서는 데이터 로직에 집중할 수 있도록 하며, 비지니스 로직에 따른 제반 사항은 이곳에서 다룬다
 */
public class UserService {

    /**
     * Users 테이블에 대한 데이터 로직을 담은 DAO 인터페이스
     * 데이터 로직만을 다루며 각 데이터 엑세스 기술에 따라 구현된 DAO를 DI받는다
     */
    private UserDao userDao;

    /**
     * User의 Level에 관련된 로직을 담당하는 인터페이스
     */
    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    /**
     * 기술에 따른 트랜젝션 경계설정 로직을 추상화 하기 위한 TransactionManager
     * JDBC, JPA등 각 기술에 맞는 TransactionManager를 DI받는다
     */
    private PlatformTransactionManager transactionManager;

    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }


    /**
     * Users 테이블의 모든 데이터를 삭제
     */
    public void deleteAll() throws Exception {
        this.updateTemplate(() -> {
            userDao.deleteAll();
        });
    }

    /**
     * 모든 User의 업그레이드 가능 여부를 확인 후, 업그레이드 실시
     */
    public void upgradeLevels() throws Exception {
        this.updateTemplate(() -> {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                    userLevelUpgradePolicy.upgradeLevel(user);
                    userDao.update(user);
                    sendUpgradeEMail(user);
                }
            }
        });
    }

    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");

        mailSender.send(mailMessage);
    }

    /**
     * User의 추가
     */
    public void add(User user) throws Exception {
        this.updateTemplate(() -> {
            if (user.getLevel() == null) user.setLevel(Level.BASIC);
            userDao.add(user);
        });
    }

    /**
     * User의 취득
     *
     * @param id
     */
    public User get(String id) {
        return userDao.get(id);
    }

    /**
     * Update 에 관련된 동작을 담을 Callback
     */
    private interface UpdateCallback {
        void update();
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
