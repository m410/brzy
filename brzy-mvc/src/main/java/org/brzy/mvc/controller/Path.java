package org.brzy.mvc.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value();
//    String method() default "GET";
}
