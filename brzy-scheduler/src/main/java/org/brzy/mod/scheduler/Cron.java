package org.brzy.mod.scheduler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Cron {
    String value() default "* * * * *";// every second
    String method() default "execute";
}
