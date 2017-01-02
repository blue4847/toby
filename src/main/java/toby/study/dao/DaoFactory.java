package toby.study.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * Component의 구조와 관계를 정의한 설정 정보.<br>
 * Component Object와 애플리케이션의 구조를 결정하는 Object를 분리하기 위해 만들어진 Factory클래스.<br>
 * DI하고자 하는 Object의 생성 방법을 정의.<br>
 * method 이름은 얻고자 하는 Object의 id로 취급된다.<br>
 * application-context에서 사용하는 XML의 id에 해당.<br>
 * @author blue4
 */
@Configuration
public class DaoFactory { 

	/**
	 * @ConnectionMaker SimpleConnectionMaker
	 * @return UserDaoJdbc
	 */
	@Bean
	public UserDaoJdbc userDao(){
		UserDaoJdbc userDao = new UserDaoJdbc();
		userDao.setDataSource(dataSource());
		return userDao;
	}
	
	/**
	 * @return SimpleDriverDataSource
	 */
	@Bean
	public DataSource dataSource(){ 
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setUrl("url");
		dataSource.setDriverClass(org.mariadb.jdbc.Driver.class);
		dataSource.setUsername("userName");
		dataSource.setPassword("password");
		return dataSource; 
	} 
}