package learningTest.jdk.proxy;

import learningTest.jdk.Hello;
import learningTest.jdk.HelloTarget;
import learningTest.jdk.UppercaseHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Proxy;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class DynamicProxyTest {
    /**
     * JDK다이내믹 프록시 테스트
     */
    @Test
    public void simpleProxy() {
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader()
                , new Class[]{Hello.class}
                , new UppercaseHandler(new HelloTarget())
        );

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    /**
     * Spring의 ProxyFactoryBean을 이용한 테스트
     */
    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();

        // 타깃 설정
        pfBean.setTarget(new HelloTarget());

        // 부가기능을 담은 어드바이스를 추가한다.
        // 여러개를 추가할 수 있다.
        pfBean.addAdvice(new UppercaseAdvice());

        // FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        // 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        // 이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택하게 한다.
        pointcut.setMappedName("sayH*");

        // 포인트컷과 어드바이스를 Advisor로 묶어서 한 번에 추가.
        // 복수의 포인트컷과 어드바이스가 추가될 수 있으므로, 포인트컷과 적용 어드바이스를 묶어서 추가하도록 한다.
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
        // 메소드 이름이 포인트컷의 선정조건에 맞지 않으므로, 부가기능(대문자 변환)이 적용되지 않는다.
        assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
    }


    static class UppercaseAdvice implements MethodInterceptor {
        public Object invoke(MethodInvocation invocation) throws Throwable {

            // 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다.
            // MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다.
            String ret = (String) invocation.proceed();

            // 부가기능 적용
            return ret.toUpperCase();
        }
    }
}
