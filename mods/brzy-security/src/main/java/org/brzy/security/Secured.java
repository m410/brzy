package org.brzy.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {
    String[] value();
}