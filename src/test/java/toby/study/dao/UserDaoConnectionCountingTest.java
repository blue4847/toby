package toby.study.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import toby.study.domain.User;

/**
 * @Runwith : 
 *  Spring의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
 *  ApplicationContext를 관리
 * @ContextConfiguration :
 *  테스트 컨텍스트가 자동으로 만들어 줄 ApplicationContext의 위치 지정
 * @DirtiesContext :
 *  해당 클래스의 테스트에서 ApplicationContext의 상태를 변경한다는 것을 알림
 *  테스트 컨텍스트는 이 해당 클래스에서의 ApplicationContext의 공유를 허락하지 않음
 *  테스트 메소드를 수행하고 나면 매번 새로운 ApplicationContext를 생성하여 다음 테스트가 사용하도록 함
 * @author blue4
 *
 */
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml" })
public class UserDaoConnectionCountingTest {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserDao countingUserDao;
	
	private int counter = 0;
	private int initCounter = 0;

	@Autowired
	private CountingDataSource countingDataSource;

	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() throws SQLException{ 
		userDao.setDataSource(countingDataSource);

		user1 = new User("homuhomu", "호무라", "pw00");
		user2 = new User("madoka", "마도카", "pw11");
		user3 = new User("mamiru", "마미", "pw22");

		countingUserDao.get(user1.getId());
	}

	@Test
	public void countingDataSourceAndSingletonCheck1() throws SQLException { 
		initCounter = counter++;

		countingUserDao.get(user1.getId());
		assertThat(countingDataSource.getCounter(), is(++counter));

		userDao.get(user1.getId());
		assertThat(countingDataSource.getCounter(), is(++counter));

		countingUserDao.get(user2.getId());
		assertThat(countingDataSource.getCounter(), is(++counter));

		userDao.get(user2.getId());
		assertThat(countingDataSource.getCounter(), is(++counter));

		userDao.get(user3.getId());
		assertThat(countingDataSource.getCounter(), is(++counter));

		countingUserDao.get(user3.getId());
		assertThat(countingDataSource.getCounter(), is(++counter)); 
		
		
	} 
	
	@Test
	public void countingDataSourceAndSingletonCheck2() throws SQLException {
		countingUserDao.get(user1.getId());
		assertThat(countingDataSource.getCounter(), not(initCounter));

	}

}
