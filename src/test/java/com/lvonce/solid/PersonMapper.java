package com.lvonce.solid;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@SqlDataSource(env="dev", key="h2-mem")
@SqlDataSource(env="prod", key="mysql")
public interface PersonMapper extends BaseMapper<Person> {

     Person getStudentById(int id);

     Person getStudentByName(String name);
}
