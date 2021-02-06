package com.lvonce.solid;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import io.github.classgraph.*;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;

import javax.inject.Named;
import javax.inject.Provider;
import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Getter
public class ClasspathAware extends AbstractModule {

    public static final String SQL_DATA_SOURCE_ANNOTATION = "com.lvonce.solid.SqlDataSource";
    public static final String PROVIDER_INTERFACE_NAME = "javax.inject.Provider";
    public static final String MAPPER_INTERFACE_NAME = "com.baomidou.mybatisplus.core.mapper.BaseMapper";

    private final String env;
    private final Injector injector = Guice.createInjector();
    private final Map<String, DataSource> sourceMap = new LinkedHashMap<>();
    private final Map<String, Class<BaseMapper>> mappers = new LinkedHashMap<>();
    private final List<String> xmlMappers = new ArrayList<>();


    public ClasspathAware(String env) {
        this.env = env;
        scan();
    }

    @Provides
    @Named("environment.id")
    public String provideEnv() {
        return this.env;
    }

    @Provides
    @Named("classpath-aware")
    public Map<String, DataSource> provideSqlDataSources() {
        return sourceMap;
    }


    @Provides
    @Named("classpath-aware")
    public Map<String, Class<BaseMapper>> provideMappers() {
        return mappers;
    }


    @Provides
    @Named("classpath-aware-xml-mapper")
    public List<String> provideXMLMappers() {
        return xmlMappers;
    }


    @SuppressWarnings("unchecked")
    private void handleDataSource(ClassInfo classInfo) {
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(SQL_DATA_SOURCE_ANNOTATION);
        AnnotationParameterValueList values = annotationInfo.getParameterValues();
        String key = (String) values.getValue("key");
        String env = (String) values.getValue("env");
        if (env.equals(this.env)) {
            Provider<DataSource> provider
                    = (Provider<DataSource>) injector.getInstance(classInfo.loadClass());
            DataSource dataSource = provider.get();
            sourceMap.put(key, dataSource);
        }
    }

    private void handleMapper(ClassInfo classInfo) {
        AnnotationInfoList annotationInfo = classInfo.getAnnotationInfo();
        if (annotationInfo == null) {
            return;
        }
        AnnotationInfoList sourceAnnotations = annotationInfo.getRepeatable(SQL_DATA_SOURCE_ANNOTATION);
        for (AnnotationInfo info : sourceAnnotations) {
            AnnotationParameterValueList values = info.getParameterValues();
            String key = (String) values.getValue("key");
            String env = (String) values.getValue("env");
            if (env.equals(this.env)) {
                Class<BaseMapper> mapperClass = classInfo.loadClass(BaseMapper.class);
                mappers.put(key, mapperClass);
            }
        }
    }

    private void handleXMLMapper(Resource resource) {
        try {
            log.info("xml mapper: {}", resource.getPathRelativeToClasspathElement());
            xmlMappers.add(resource.getPathRelativeToClasspathElement());
        } catch (Exception ex) {
            log.error("handleXMLMapper error: {}", ex.getMessage());
        }
    }

    public void scan() {

        ClassGraph graph = new ClassGraph().enableAllInfo().acceptPackages("*");
        try (ScanResult scanResult = graph.scan()) {
            ClassInfoList list
                    = scanResult.getClassesWithAnnotation(SQL_DATA_SOURCE_ANNOTATION);
            for (ClassInfo info : list) {
                if (info.implementsInterface(PROVIDER_INTERFACE_NAME)) {
                    handleDataSource(info);
                }
            }

            ClassInfoList mapperList = scanResult.getClassesImplementing(MAPPER_INTERFACE_NAME);
            mapperList.forEach(this::handleMapper);
            log.info("mapper list: {}", mapperList);
        }
        ClassGraph xmlMapperGraph = new ClassGraph().acceptPathsNonRecursive("mappers");
        try (ScanResult result = xmlMapperGraph.scan()) {
            ResourceList resources = result.getResourcesWithExtension(".xml");
            for (Resource resource : resources) {
                handleXMLMapper(resource);
            }
        }


    }
}
