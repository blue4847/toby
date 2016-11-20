package junit;

import static org.hamcrest.CoreMatchers.is; 
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class JUnitTest {
	
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	static JUnitTest testObject;
	
	@Test 
	public void test1(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);
	}
	
	@Test
	public void test3(){
		assertThat(testObjects, is( not( hasItem(this)))); 
		testObjects.add(this);
	}
	


}
