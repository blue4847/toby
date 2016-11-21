package junit;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit 실행 환경에 대한 테스트
 * 1. JUnit always create new test object for each test
 * 2. Spring create only one ApplicationContext object
 * @author blue4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JUnitTest {

	@Autowired
	ApplicationContext context;
	
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();

	static ApplicationContext contextObject = null;
	
	static JUnitTest testObject;
	
	@Test 
	public void test1(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);
		
		assertThat( contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);

		assertThat( contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
	}
	
	@Test
	public void test3(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);

		assertThat( contextObject == null || contextObject == this.context, is(true));
		contextObject = this.context;
	}
	


}
