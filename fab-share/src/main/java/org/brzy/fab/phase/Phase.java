package org.brzy.fab.phase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Phase {
    String name();
    String desc() default "";
	String defaultTask() default "";
	String[] dependsOn() default "";
}