package org.brzy.scheduler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CronJob {
    String value() default "* * * * *";// every second
    String method() default "execute";
}
