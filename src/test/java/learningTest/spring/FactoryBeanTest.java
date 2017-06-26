package learningTest.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;


/**
 * Created by blue4 on 2017-06-04.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring-connection.xml", "/META-INF/toby-study-context.xml"
        , "/META-INF/toby-study-test-context.xml"})
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean(){
        Object message = context.getBean("message");
		// 타입 확인
        assertThat(message, instanceOf(Message.class));
		// 설정과 기능 확인
        assertThat(((Message)message).getText(), is("Factory Bean"));
    }

	@Test
	public void getFactoryBean(){
		/// &가 붙고 안 붙고에 따라 getBean() 메소드가 반환하는 오브젝트가 달라진다.
		Object factory = context.getBean("&message");
        assertThat(factory, instanceOf(MessageFactoryBean.class));
	}

}
