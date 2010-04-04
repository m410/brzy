package org.brzy.saved.trx;

import javassist.util.proxy.MethodHandler;
import java.lang.reflect.Method;

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class JavaMethodHandler implements MethodHandler {
    public Object invoke(Object self, Method m, Method proceed, Object[] args) throws Throwable {
        System.out.println("Name: " + m.getName());
        return proceed.invoke(self, args);  // execute the original method.
    }
}