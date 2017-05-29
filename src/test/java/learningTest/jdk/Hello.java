package learningTest.jdk;

/**
 * Dynamic Proxy학습에 사용할 인터페이스.
 */
public interface Hello {
    String sayHello(String name);
    String sayHi(String name);
    String sayThankYou(String name);
}
