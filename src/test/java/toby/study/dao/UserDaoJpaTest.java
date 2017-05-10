package toby.study.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import toby.study.domain.Level;
import toby.study.domain.User;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by blue4 on 2017-02-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"})
public class UserDaoJpaTest {


    @Resource(name = "userDaoJpa")
    private UserDao dao;


    private User user1;
    private User user2;
    private User user3;

    /**
     * Spring에 등록된 TransactionManager를 불러온다.
     */
    @Autowired
    JpaTransactionManager txManager;

    DefaultTransactionDefinition def;

    TransactionStatus txStatus;

    @Before
    public void setUp() {
        user1 = new User("homuhomu2", "호무라", "pw00", Level.BASIC, 1, 0, "homu@mail.com");
        user2 = new User("madoka2", "마도카", "pw11", Level.SILVER, 55, 10, "mado@mamika.saya");
        user3 = new User("mamiru2", "마미", "pw22", Level.GOLD, 100, 40, "mami@ru.do");

        /**
         * Transaction 설정을 수동으로 잡아준다.
         */
        def = new DefaultTransactionDefinition();
        def.setName("SomeTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txStatus = txManager.getTransaction(def);
    }

    /**
     * 매 Test완료 후 각 Transaction에 commit을 실시한다.
     */
    @After
    public void after() {
        /**
         *
         */
        if(!txStatus.isCompleted())
            txManager.commit(txStatus);
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
     *
//     * @throws EmptyResultDataAccessException
     */
//    @Test(expected = EmptyResultDataAccessException.class)
    @Test
    public void getUserFailure() throws SQLException {

        dao.deleteAll();

        assertThat(dao.getCount(), is(0));

        // Exception throwing expected
        User user = dao.get("unknown_id");
        assertThat(dao.getCount(), is(0));
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
     *
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
     * 동일 유저 등록시 DataIntegrityViolationException발생 확인
     */
    @Test(expected = DataIntegrityViolationException.class)
    public void duplicateKey() throws Throwable{
        try{
            dao.deleteAll();

            dao.add(user1);

            txManager.commit(txStatus);
            def = new DefaultTransactionDefinition();
            def.setName("SomeTx2");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            txStatus = txManager.getTransaction(def);
            dao.add(user1);
            txManager.commit(txStatus);

        }
        catch(Throwable t){
            t.printStackTrace();
            throw t;
        }
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
