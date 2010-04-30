package org.brzy.controller;

/**
 * @author Michael Fortin
 * @version $Id: $
 */
public @interface Path {
    String value();
    String method() default "GET";
}
