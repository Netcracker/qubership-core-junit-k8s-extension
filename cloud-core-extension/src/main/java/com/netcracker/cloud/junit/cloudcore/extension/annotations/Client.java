package com.netcracker.cloud.junit.cloudcore.extension.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures fabric8 client parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Client {

    IntValue requestTimeout() default @IntValue(5000);

    IntValue websocketPingInterval() default @IntValue(10000);

    IntValue watchReconnectInterval() default @IntValue(5000);

}
