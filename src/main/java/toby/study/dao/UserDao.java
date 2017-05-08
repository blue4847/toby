package toby.study.dao;

import toby.study.domain.User;

import java.util.List;

/**
 * Users 테이블에 관련된 데이터 엑세스 인터페이스를 제공한다.
 * Users 테이블에 대한 데이터 로직은 본 인터페이스를 구현하여 사용한다.
 * 데이터 로직 외의 비즈니스 로직, 트랜젝션 경계 설정 등은 본 구현체를 사용하는 Service계층에서 구현한다.
 */
public interface UserDao { 

    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    void update(User user);
}
