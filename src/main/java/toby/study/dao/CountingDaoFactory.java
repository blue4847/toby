package toby.study.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * Factory class<br>
 * CountingConnectionMaker를 ConnectionMaker로 하는 UserDao의 생성<br>
 * @author blue4
 */
@Configuration
public class CountingDaoFactory {
	
	/**
	 * @DataSource CountingConnectionMaker
	 * @return UserDao
	 */
	@Bean
	public UserDao userDao(){
		UserDao userDao = new UserDaoDeleteAll();
		userDao.setDataSource(dataSource());
		return userDao;
	}
	
	/**
	 * @DataSource SimpleDriverDataSource
	 * @return CountingDataSource
	 */
	@Bean
	public DataSource dataSource(){
		CountingDataSource countingDataSource = new CountingDataSource();
		countingDataSource.setDataSource(realDataSource());
		return countingDataSource;
	}
	
	/**
	 * @return SimpleDriverDataSource
	 */
	@Bean
	public DataSource realDataSource(){
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setUrl("url");
		dataSource.setDriverClass(org.mariadb.jdbc.Driver.class);
		dataSource.setUsername("userName");
		dataSource.setPassword("password");
		return dataSource; 
	} 
}
