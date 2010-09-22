package org.brzy.fab.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Michael Fortin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    String name();
    String desc();
}