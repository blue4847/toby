package toby.study.dao;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import toby.study.dao.CountingConnectionMaker;
import toby.study.dao.CountingDaoFactory;
import toby.study.dao.UserDao;

public class UserDaoConnectionCountingTest {
	
	public static void main( String[] args) throws ClassNotFoundException, SQLException{

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext( CountingDaoFactory.class);
		
		UserDao dao = context.getBean("userDao", UserDao.class); 
		
		/**
		 * DAO 사용 코드
		 */
		CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
		System.out.println(" Connection counter : " + ccm.getCounter());
		
	}

}
