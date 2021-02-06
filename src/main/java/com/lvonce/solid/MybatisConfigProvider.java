package com.lvonce.solid;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;


public class MybatisConfigProvider implements Provider<MultiContainer<MybatisConfiguration>> {

    private final MultiContainer<DataSource> sources = new MultiContainer<>();

    private final String envId;

    private final Map<String, Class<BaseMapper>> mapperClasses;

    @Inject
    public MybatisConfigProvider(
            @Named("environment.id") String envId,
            DataSourceProvider dataSourceProvider,
            @Named("classpath-aware") Map<String, Class<BaseMapper>> mapperClasses) {
        this.envId = envId;
        this.sources.addAll(dataSourceProvider.get());
        this.mapperClasses = mapperClasses;
    }

    @Override
    public MultiContainer<MybatisConfiguration> get() {
        MultiContainer<MybatisConfiguration> configs = new MultiContainer<>();
        sources.getAll().forEach((key, it)-> {
            MybatisConfiguration config = new MybatisConfiguration();
            TransactionFactory manager = new JdbcTransactionFactory();
            Environment environment = new Environment(envId, manager, it);
            config.setEnvironment(environment);
            MapperRegistry registry = config.getMapperRegistry();
            mapperClasses.forEach((mapperKey, mapperClass)-> {
                if (key.equals(mapperKey)) {
                    registry.addMapper(mapperClass);
                }
            });
            configs.set(key, config);
        });
        return configs;
    }
}
