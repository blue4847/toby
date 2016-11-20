package toby.study.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import toby.study.domain.User;

/**
 * @Runwith : 
 *  Spring의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
 *  ApplicationContext를 관리
 * @ContextConfiguration :
 *  테스트 컨텍스트가 자동으로 만들어 줄 ApplicationContext의 위치 지정
 * @author blue4
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"})
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
	
	@Resource(name="userDao")
	private UserDao dao;

	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() { 

		user1 = new User("homuhomu", "호무라", "pw00");
		user2 = new User("madoka", "마도카", "pw11");
		user3 = new User("mamiru", "마미", "pw22");

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
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));

		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
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
	 * @throws SQLException
	 */
	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {

		dao.deleteAll();

		assertThat(dao.getCount(), is(0));

		// Exception throwing expected
		dao.get("unknown_id");

	}

}
