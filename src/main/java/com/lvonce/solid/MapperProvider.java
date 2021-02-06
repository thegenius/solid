package com.lvonce.solid;

import javax.inject.Inject;
import javax.inject.Provider;

public class MapperProvider<T> implements Provider<T> {

    @Inject
    private SqlSessionManagerProvider managerProvider;

    private final String mapperKey;
    private final Class<T> mapperClass;

    public MapperProvider(String mapperKey, Class<T> mapperClass) {
        this.mapperKey = mapperKey;
        this.mapperClass = mapperClass;
    }

    @Override
    public T get() {
        return managerProvider.get().get(mapperKey).getMapper(mapperClass);
    }
}
