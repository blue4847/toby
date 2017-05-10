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

import toby.study.domain.Level;
import toby.study.domain.User;
import toby.study.service.UserService;

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
 *  
 * @test CountingDataSource 작동 확인
 * @test ApplicationContext에서 DI받은 Object가 singleton인가를 확인
 * @test ApplicationContext 에서 DI받은 Object가 동일한 Object인가를 확인
 *  
 * @author blue4
 *
 */
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml" })
public class UserDaoJdbcConnectionCountingTest {

	@Autowired
	private UserDaoJdbc userDao;
	
	@Autowired
	private UserDaoJdbc countingUserDao;
	
	private int counter = 0;
	private int initCounter = 0;

	@Autowired
	private CountingDataSource countingDataSource;

	@Autowired
	private UserService userService;

	private User user1;
	private User user2;
	private User user3;

	@Before
	public void setUp() throws Exception {

		userService.deleteAll();

		user1 = new User("homuhomu", "호무라", "pw00", Level.BASIC, 1, 0);
		user2 = new User("madoka", "마도카", "pw11", Level.SILVER, 55, 10);
		user3 = new User("mamiru", "마미", "pw22", Level.GOLD, 100, 40);

		userService.add(user1);
		userService.add(user2);
		userService.add(user3);

		userDao.setDataSource(countingDataSource);
		// singleton object인가를 확인하기 위해, 1회 카운트
		countingUserDao.get(user1.getId());
	}

	/**
	 * ApplicationContext로부터 DI받은 CountingDataSource가 동일한 Object인가를 확인
	 * @throws SQLException
	 */
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
	
	/**
	 * @Before에서 실행한 카운트가 반영되었는가를 확인
	 * @throws SQLException
	 */
	@Test
	public void countingDataSourceAndSingletonCheck2() throws SQLException {
		
		countingUserDao.get(user1.getId());
		assertThat(countingDataSource.getCounter(), not(initCounter+1));

	}

}
