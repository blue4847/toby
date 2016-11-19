package toby.study.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import toby.study.domain.User;

public class UserDaoTest {

	// for test
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// this is my 2nd time commit
		// differ test

		// ApplicationContext context = new
		// AnnotationConfigApplicationContext(DaoFactory.class);

		// set XML for DI-Setting info
		ApplicationContext context = new GenericXmlApplicationContext(
				new String[] { "/META-INF/toby-chap01-context.xml", "/META-INF/spring-connection.xml" });
		// relational class path; this code not work
		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("/META-INF/toby-chap01-context.xml",
		// BaseClassName.class);

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
		 * DI DaoFactory daoFactory = new DaoFactory(); dao =
		 * daoFactory.userDao();
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
