package toby.study.util.converter;

import org.junit.Test;
import toby.study.domain.Level;

import java.util.EnumSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by blue4 on 2017-02-11.
 */
public class LevelEnumConverterTest {

    LevelEnumConverter converter = new LevelEnumConverter();

    @Test
    public void convertToDatabaseColumn(){
        Set<Level> levels = EnumSet.allOf(Level.class );
        for(Level le : levels){

            Integer value = converter.convertToDatabaseColumn(le);
            assertThat( value, is(le.intValue()));
        }
    }


    @Test
    public void convertToEntityAttribute(){
        Integer[] values = {1, 2, 3};
        for(Integer val : values){
            Level level = converter.convertToEntityAttribute(val);
            assertThat(level, is(Level.valueOf(val)));
        }

    }

}