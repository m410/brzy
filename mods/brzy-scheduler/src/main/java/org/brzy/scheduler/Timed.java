package org.brzy.scheduler;

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */
public @interface Timed {
    int interval();
    int start();
}
