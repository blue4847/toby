package toby.study.handler;


import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * UserServiceTx를 dynamic proxy방식으로 구현하기 위한 InvocationHandler구현 클래스.
 * 기존 UserServiceTx는 UserService의 모든 메소드를 구현해야 하고 트랜잭션이 필요한  메소드마다
 * transaction 처리코드가 중복되어 나타나는 비효율적 방법으로 만들어져있다.
 * transaction 부가기능을 제공하는 dynamic proxy를 만들어 제공하여 효율을 높인다.
 */
public class TransactionHandler implements InvocationHandler {

    /**
     * 부가기능을 제공할 타깃 오브젝트.
     * UserService가 아니더라도, 어떤 타입의 오브젝트든 적용 가능하다.
     */
    private Object target;

    /**
     * transaction 기능을 제공하는 데 필요한 트랜젝션 매니져
     */
    private PlatformTransactionManager transactionManager;

    private Set<String> patterns;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 특정 이름의 메소드만을 지정하여 transaction 경계설정 기능을 부여하기 위한 패턴 주입.
     */
    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * transaction 적용 대상 메소드를 선별해서 transaction 경계설정 기능을 부여해준다.
         */
        for (String pattern : patterns) {
            if (method.getName().startsWith(pattern))
                return invokeInTransaction(method, args);
        }
        return method.invoke(target, args);
    }

    private Object invokeInTransaction(Method method, Object... args) throws Throwable {
        /** get current transaction-status */
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            /** run method */
            Object ret = method.invoke(target, args);

            /** commit */
            this.transactionManager.commit(status);
            return ret;
        }
        // 롤백을 적용하기 위해서는 RuntimeException이 아닌 InvocationTargetException으로 잡아야 한다
        // Method.invoke() 를 이용해 타깃 오브젝트의 메소드를 호출할 때는 타깃 오브젝트에서 발생하는 예외가
        // InvocationTargetException로 한 번 포장돼서 전달된다.
        catch (InvocationTargetException e) {
            /** rollback */
            this.transactionManager.rollback(status);

            // 따라서 일단 InvocationTargetException으로 받은 후 getTargetException()으로 중첩되어 있는 예외를 가져와야 한다.
            throw e.getTargetException();
        }
    }

}
