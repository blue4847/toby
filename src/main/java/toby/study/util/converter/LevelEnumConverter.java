package toby.study.util.converter;


import toby.study.domain.Level;

import javax.persistence.AttributeConverter;

/**
 * Entity attribute converter
 * @Entity toby.study.domain.Level
 * Created by blue4 on 2017-02-11.
 */
public class LevelEnumConverter implements AttributeConverter<Level, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Level level){
        return level.intValue();
    }

    @Override
    public Level convertToEntityAttribute(Integer integer) {
        return Level.valueOf(integer);
    }


}
