package org.brzy.action;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value();
    String[] methods() default {"GET"};
}
