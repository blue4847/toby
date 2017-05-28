package toby.study.service;

import toby.study.domain.User;

/**
 * 기존의 Transaction 처리와 비즈니스 로직을 담은 UserService를 분리시키기 위해 Interface화 한다.
 * toby.study.service.UserServiceTx 는 UserService를 구현하여 클라이언트에 대해 UserService 타입 오브젝트로서 동작한다.
 * UserServiceTx는 Transaction 처리만을 구현하고, 실제 비즈니스 로직은 UserService를 구현한 다른 타입의 클래스
 * toby.study.service.UserServiceImple 에게 위임한다.
 */
public interface UserService {

    void deleteAll() throws Exception;

    void add(User user) throws Exception;

    void upgradeLevels() throws Exception;

    User get(String id);
}
