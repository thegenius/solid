package com.lvonce.solid;
import static com.google.inject.util.Providers.guicify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;


@Slf4j
public class MybatisModule extends AbstractModule {

    private ClasspathAware classpathAware;

    private <T> void bindMapper(String mapperKey, Class<T> mapperClass) {
        MapperProvider<T> provider = new MapperProvider<>(mapperKey, mapperClass);
        bind(mapperClass).toProvider(guicify(provider)).in(Scopes.SINGLETON);
    }

    public MybatisModule(ClasspathAware classpathAware) {
        this.classpathAware = classpathAware;
    }

    @Override
    protected void configure() {
        Map<String, Class<BaseMapper>> mappers = classpathAware.getMappers();
        mappers.forEach(this::bindMapper);
    }
}
