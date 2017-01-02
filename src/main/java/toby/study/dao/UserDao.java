package toby.study.dao;

import toby.study.domain.User;

import java.util.List;

/**
 * Created by blue4 on 2017-01-02.
 */
public interface UserDao {
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
}
