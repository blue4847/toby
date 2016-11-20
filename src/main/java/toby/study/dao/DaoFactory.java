package toby.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	 * @return UserDao
	 */
	@Bean
	public UserDao userDao(){
		UserDao userDao = new UserDao();
		userDao.setConnectionMaker(connectionMaker());
		return userDao;
	}
	
	/**
	 * @return SimpleConnectionMaker
	 */
	@Bean
	public ConnectionMaker connectionMaker(){ 
		return new SimpleConnectionMaker();
	} 
}