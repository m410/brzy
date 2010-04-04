package org.brzy.saved.trx;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Method;

/**
 * @author Michael Fortin
 * @version $Id: $
 */
public class JProxyFactory {

    public Object proxy(Class t, Object[] args, MethodHandler h) throws Exception {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(t);
        factory.setFilter(new MethodFilter() {
            public boolean isHandled(Method m) {
                return !m.getName().equals("finalize") &&
                        !m.getName().equals("toString") &&
                        !m.getName().equals("clone") &&
                        !m.getName().equals("hashCode") &&
                        !m.getName().equals("equals");
            }
        });

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++)
            classes[i] = args[i].getClass();

        return factory.create(classes, args, h);
    }
}

