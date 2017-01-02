package toby.study.template;

/**
 * Callback class of toby.study.template.Calculator
 */
public interface LineCallback<T> {
    /** callback method */
    T doSomethingWithLine(String line, T value);
}
