package learningTest.jdk;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * HelloUppercase와 마찬가지로 모든 요청을 타깃에 위임하면서 리턴 값을 대문자로 바꿔주는 부가기능을 가진 InvocationHandler 구현 클래스.
 * Dynamic proxy로부터 메소드 호출 정보를 받아서 처리한다.
 */
public class UppercaseHandler implements InvocationHandler{

	/**
	 * Dynamic proxy로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하기 때문에
	 * 타깃 오브젝트를 주입받아둔다.
	 * 어차피 reflectiond의 Method 인터페이스를 이용해 타깃의 메소드를 호출하는 것이니 Hello 타입의 타깃으로 제한할 필요는 없다.
	 */
    Object target;

    public UppercaseHandler(Object target){
        this.target = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// 타깃으로 위임. 인터페이스의 메소드 호출에 모두 적용된다.  
		Object ret = method.invoke(target, args);

		/**
		 * 리턴 타입이 String이고 호출한 메소드의 이름이 "say"인 경우에만 대문자 변경 기능을 적용.
		 */
		if(ret instanceof String && method.getName().startsWith("say"))
			return ((String)ret).toUpperCase();
		else
			return ret;
    }
}
