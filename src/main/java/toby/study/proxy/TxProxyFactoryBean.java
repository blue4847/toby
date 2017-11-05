package toby.study.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import toby.study.handler.TransactionHandler;

import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Created by blue4 on 2017-07-05.
 */
// 생성할 오브젝트 타입을 지정할 수도 있지만 범용적으로 사용하기 위해 Object로 한다.
public class TxProxyFactoryBean implements FactoryBean<Object> {

	// TransactionHandler를 생성할 때 필요한 필드.
	Object target;
	PlatformTransactionManager transactionManager;
	Set<String> patterns;

	// Dynamic Proxy를 생성할 때 필요하다. 
	// UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다.
	Class<?> serviceInterface;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    // FactoryBean 인터페이스 구현 메소드
    // DI 받은 정보를 이용해서 TransactionHandler를 사용하는 Dynamic Proxy를 생성한다.
    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPatterns(patterns);
        return Proxy.newProxyInstance(
                getClass().getClassLoader()
                , new Class[]{serviceInterface}
                , txHandler
        );
    }

    @Override
    public Class<?> getObjectType() {
        // 팩토리 빈이 생성하는 오브젝트의 타입은 DI 받은 인터페이스 타입에 따라 달라진다.
        // 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용 할 수 있다.
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        // 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 뜻이다.
        return false;
    }
}
