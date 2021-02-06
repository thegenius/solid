package com.lvonce.solid;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(SqlDataSources.class)
public @interface SqlDataSource {
    String env() default "default";
    String key() default "default";
}
