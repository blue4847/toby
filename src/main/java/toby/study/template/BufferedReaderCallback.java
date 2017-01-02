package toby.study.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Callback interface of toby.study.template.Calculator
 */
public interface BufferedReaderCallback {
    /** callback method */
    Integer doSomethingWithReader( BufferedReader br) throws IOException;
}
