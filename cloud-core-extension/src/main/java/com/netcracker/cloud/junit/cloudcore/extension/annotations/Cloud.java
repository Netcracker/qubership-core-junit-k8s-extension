package org.qubership.cloud.junit.cloudcore.extension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Cloud {

    Value cloud() default @Value(prop = "clouds.cloud.name");

    Value namespace() default @Value(prop = "clouds.cloud.namespaces.namespace");

}
