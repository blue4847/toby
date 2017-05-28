package toby.study.dao;

import toby.study.domain.User;

import java.util.ArrayList;
import java.util.List;

/** 
 * UserDao의 Mock구현체
 * 업그레이드 실시 기록을 보존한다.
 * 테스트에 사용되지 않는 메소드는 별도로 구현하지 않고, UnsupportedOperationException을 던진다.
 */
public class MockUserDao implements UserDao{

	/**
	 * 레벨 업그레이드 후보 User 오브젝트 목록
	 */
    private List<User> users;

	/**
	 * 업그레이드 대상 오브젝트를 저장해둘 목록
	 */
    private List<User> updated = new ArrayList<>();

    public MockUserDao(List<User> users){
        this.users = users;
    }

    public List<User> getUpdated(){
        return this.updated;
    }

	/**
	 * 스텁 기능 제공
	 */
    @Override
    public List<User> getAll() {
        return users;
    }

	/**
	 * 목 오브젝트 기능 제공
	 */
    @Override
    public void update(User user) {
        updated.add(user);
    }

    /** 테스트에 사용되지 않는 메소드 */

    @Override
    public void add(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
        throw new UnsupportedOperationException();
    }

}
