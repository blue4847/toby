package learningTest.jdk;

/**
 * Proxy class
 */
public class HelloUppercase implements Hello{

	/**
	 * 위임할 타겟 오브젝트. 여기서는 타겟 클래스의 오브젝트인 것은 알지만
	 * 다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.
	 */
    Hello hello;

    public HelloUppercase(Hello hello){
        this.hello = hello;
    }

	/**
	 * 위임과 부가기능 적용.
	 * 이 코드에는 프록시 적용의 일반적인 문제점 두 가지를 모두 갖고 있다.
	 * 인터페이스의 모든 메소드를 구현해 위임하도록 코드를 만들어야 하면, 부가기능인 리턴 값을
	 * 대문자로 바꾸는 기능이 모든 메소드에 중복돼서 나타난다.
	 */
    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
