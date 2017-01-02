package toby.study.template;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * test class of toby.study.template.Calculator
 */
public class CalcSumTest {

    Calculator calculator;
    String numFilepath;

    @Before
    public void setUp() {
        calculator = new Calculator();
        File textFile = new File("src/test/java/toby/study/template/numbers.txt");
        numFilepath = textFile.getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        int sum = calculator.calcSum(numFilepath);
        assertThat(sum, is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(this.numFilepath), is(24));

    }

    @Test
    public void concatenateOfStrings() throws IOException{
        assertThat(calculator.concatenate(this.numFilepath), is("1234"));
    }
}

