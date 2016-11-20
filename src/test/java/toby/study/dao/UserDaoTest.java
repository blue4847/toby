package toby.study.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import toby.study.domain.User;

public class UserDaoTest {

	// for test
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		/**
		 * ApplicationContext VS Factory class
		 * 1. 클라이언트는 구체적인 Factory 클래스를 알 필요가 없다
		 * 2. ApplicationContext는 종합 IoC 서비스를 제공해준다
		 * 3. ApplicationContext는 빈을 검색하는 다양한 방법을 제공한다
		 * @see toby spring 100p
		 */

		// ApplicationContext
		// 1. Factory class
//		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

		// 2. XML context file : absolute location
		ApplicationContext context = new GenericXmlApplicationContext(
				new String[] { "/META-INF/toby-test-context.xml", "/META-INF/spring-connection.xml" });

		// 3. XML context file : relational class path with Class
		// ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/toby-chap01-context.xml", // BaseClassName.class);

		/**
		 * dependency lookup
		 * 
		 * @param :
		 *            name of context method
		 * @param :
		 *            class type for generic return
		 */
		UserDao dao = context.getBean("userDao", UserDao.class);

		/*
		 * DI DaoFactory daoFactory = new DaoFactory(); 
		 * dao = daoFactory.userDao();
		 */
		User user = new User();
		user.setId("whiteShip");
		user.setName("백기선");
		user.setPassword("married");
		dao.add(user);
		System.out.println(user.getId() + " 등록 성공");

		User user2 = dao.get(user.getId());

		System.out.println(user2.getName());

		System.out.println(user2.getPassword());

		System.out.println(user.getId() + " 조회 성공");
		// this is my 3rd commit
	}
}
