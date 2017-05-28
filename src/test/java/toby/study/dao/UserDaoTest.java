package toby.study.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import toby.study.domain.Level;
import toby.study.domain.User;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// Spring의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@RunWith(SpringJUnit4ClassRunner.class)
// 테스트 컨텍스트가 자동으로 만들어 줄 ApplicationContext의 위치 지정
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"})
public class UserDaoTest {

    public static void main(String... strings) {
        JUnitCore.main("toby.study.dao.UserDaoTest");
    }

    /**
     * ApplicationContext VS Factory class <br>
     * 1. 클라이언트는 구체적인 Factory 클래스를 알 필요가 없다
     * 2. ApplicationContext는 종합 IoC 서비스를 제공해준다
     * 3. ApplicationContext는 빈을 검색하는 다양한 방법을 제공한다
     *
     * @see toby spring 100p
     */
    @Resource(name = "userDaoJdbc")
    private UserDao dao;

    /**
     * test용 유저 데이터
     */
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {

        user1 = new User("homuhomu", "호무라", "pw00", Level.BASIC, 1, 0, "homu@homu.homu");
        user2 = new User("madoka", "마도카", "pw11", Level.SILVER, 55, 10, "mado@mail.com");
        user3 = new User("mamiru", "마미", "pw22", Level.GOLD, 100, 40, "mami@ru.do");

    }

    @Test
    public void addAndGet() throws SQLException {

        // test deleteAll
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        // test getCount
        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        // test add and get
        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void count() throws SQLException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    /**
     * EmptyResultDataAccessException의 발생 감지 테스트
     * @throws SQLException
     */
    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {

        dao.deleteAll();

        assertThat(dao.getCount(), is(0));

        // Exception throwing expected
        dao.get("unknown_id");

    }

    @Test
    public void getAll() throws SQLException {
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users1.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    /**
     * 유저 정보의 일치 확인용 메소드
     * @param user1
     * @param user2
     */
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

	/**
	 * 동일 유저 등록시 DuplicateKeyException발생 확인
	 */
    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("호무호무");
        user1.setPassword("pw13");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        User user2get = dao.get(user2.getId());
        checkSameUser(user1, user1update);
        checkSameUser(user2, user2get);
    }
}
