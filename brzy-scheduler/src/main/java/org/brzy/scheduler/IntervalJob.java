package org.brzy.scheduler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IntervalJob {
    int value() default 1000 * 60 * 60;
    String method() default "execute";
    String startTime() default "";
}
