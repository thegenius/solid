package com.lvonce.solid;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@SqlDataSource(env="dev", key="h2-mem")
@SqlDataSource(env="prod", key="mysql")
public interface PersonMapper extends BaseMapper<Person> {

     @Select("select * from person where name = #{name}")
     Person selectByName(String name);

     @Select("select * from person where age = #{age}")
     List<Person> selectByAge(int age);

     Person getStudentById(int id);

     Person getStudentByName(String name);
}
