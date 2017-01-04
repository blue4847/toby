package toby.study.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import toby.study.dao.UserDao;
import toby.study.domain.Level;
import toby.study.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by blue4 on 2017-01-04.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"})
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("homuhomu", "", "pw01", Level.BASIC, 49, 0)
                , new User("mamimami", "", "pw02", Level.BASIC, 50, 0)
                , new User("madomado", "", "pw03", Level.SILVER, 60, 29)
                , new User("ruliruli", "", "pw04", Level.SILVER, 60, 30)
                , new User("mikumiku", "", "pw05", Level.GOLD, 100, 100)
        );
    }

    @Test
    public void upgradeLevel() {
        userDao.deleteAll();
        for (User user : users)
            userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }
}