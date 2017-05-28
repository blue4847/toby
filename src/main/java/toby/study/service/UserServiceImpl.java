package toby.study.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;

import java.util.List;

/**
 * Users 테이블에 대한 비지니스 로직을 다루는 Service클래스 * 
 * 비지니스 로직과 더불어 트랜젝션 경계 설정을 다룬다
 * 비즈니스 로직만을 다루며, Transaction 경계에 대한 로직은 또다른 UserService를 구현한 클래스에서 다룬다.
 * DAO에서는 데이터 로직에 집중할 수 있도록 하며, 비지니스 로직에 따른 제반 사항은 이곳에서 다룬다.
 */
public class UserServiceImpl implements UserService {

    /**
     * Users 테이블에 대한 데이터 로직을 담은 DAO 인터페이스
     * 데이터 로직만을 다루며 각 데이터 엑세스 기술에 따라 구현된 DAO를 DI받는다
     */
    private UserDao userDao;

    /**
     * User의 Level에 관련된 로직을 담당하는 인터페이스
     */
    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }


    /**
     * Users 테이블의 모든 데이터를 삭제
     */
    @Override
    public void deleteAll() throws Exception {
        userDao.deleteAll();
    }

    /**
     * 모든 User의 업그레이드 가능 여부를 확인 후, 업그레이드 실시
     */
    @Override
    public void upgradeLevels() throws Exception {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
                sendUpgradeEMail(user);
            }
        }
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
    @Override
    public void add(User user) throws Exception {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    /**
     * User의 취득
     *
     * @param id
     */
    @Override
    public User get(String id) {
        return userDao.get(id);
    }
}
