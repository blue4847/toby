package toby.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Factory class<br>
 * CountingConnectionMaker를 ConnectionMaker로 하는 UserDao의 생성<br>
 * @author blue4
 */
@Configuration
public class CountingDaoFactory {
	
	/**
	 * @ConnectionMaer CountingConnectionMaker
	 * @return UserDao
	 */
	@Bean
	public UserDao userDao(){
		UserDao userDao = new UserDao();
		userDao.setConnectionMaker(connectionMaker());
		return userDao;
	}
	
	/**
	 * @ConnectionMaker SimpleConnectionMaker
	 * @return CountingConnectionMaker
	 */
	@Bean
	public ConnectionMaker connectionMaker(){
		return new CountingConnectionMaker( realConnectionMaker());
	}
	
	/**
	 * @return SimpleConnectionMaker
	 */
	@Bean
	public ConnectionMaker realConnectionMaker(){
		return new SimpleConnectionMaker();
	}

}
