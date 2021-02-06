package com.lvonce.solid;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.classgraph.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;


@Slf4j
public class MapperTest {
    @Test
    public void test() {
        ClasspathAware aware = new ClasspathAware("dev");
        MybatisModule mybatisModule = new MybatisModule(aware);
        Injector injector = Guice.createInjector(aware, mybatisModule);

        DataSourceProvider dataSourceProvider = injector.getInstance(DataSourceProvider.class);
        Assert.assertNotNull(dataSourceProvider);
        MultiContainer<DataSource> sources = dataSourceProvider.get();
        Assert.assertNotNull(sources.get("h2-mem"));

        MybatisConfigProvider configProvider = injector.getInstance(MybatisConfigProvider.class);
        Assert.assertNotNull(configProvider);
        MultiContainer<MybatisConfiguration> configs = configProvider.get();
        MybatisConfiguration config = configs.get("h2-mem");
        Assert.assertNotNull(config);

        SqlSessionManagerProvider managerProvider = injector.getInstance(SqlSessionManagerProvider.class);
        SqlSessionManager manager = managerProvider.get().get("h2-mem");
        Assert.assertNotNull(manager);

        PersonMapper personMapper = injector.getInstance(PersonMapper.class);
        Person person = new Person();
        person.setId(1);
        person.setName("wang");
        person.setAge(32);
        personMapper.insert(person);

        Person person2 = new Person();
        person2.setId(2);
        person2.setName("tu");
        person2.setAge(32);
        personMapper.insert(person2);

        Person result = personMapper.selectById(1);
        Assert.assertEquals(person.getName(), result.getName());

        Person result2 = personMapper.getStudentById(1);
        Assert.assertEquals(person.getName(), result2.getName());

        Person result3 = personMapper.getStudentByName("wang");
        Assert.assertEquals(person.getName(), result3.getName());

        Person result4 = personMapper.selectByName("tu");
        Assert.assertEquals(person2.getName(), result4.getName());

        Page<Person> pages = PageHelper.startPage(1, 1, true)
                .doSelectPage(()->personMapper.selectByAge(32));
        Assert.assertEquals(pages.getPages(), 2);

    }
}
