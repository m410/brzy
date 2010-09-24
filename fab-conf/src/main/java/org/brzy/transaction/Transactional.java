package org.brzy.transaction;

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */
public @interface Transactional {
    String isolation();
    String propagation();
    boolean readOnly() default false;


}
