package org.brzy.saved.trx;

import org.brzy.mock.PojoFixture;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
public class JProxyFactoryTest {

    @Test
    public void testJProxy() throws Exception {
        JProxyFactory factory = new JProxyFactory();
        PojoFixture result = (PojoFixture)factory.proxy(PojoFixture.class,new Object[]{}, new JavaMethodHandler());
        System.out.println("result: " + result);
        assertNotNull(result);
        assertEquals("hello Fred",result.hello("Fred"));
    }

//    @Test
//    public void testScalaProxy() throws Exception {
//        JProxyFactory factory = new JProxyFactory();
//        ProxyFixture result = (ProxyFixture)factory.proxy(ProxyFixture.class,new Object[]{}, new JavaMethodHandler());
//        System.out.println("result: " + result);
//        assertNotNull(result);
//        assertEquals("hello: Bob",result.hello("Bob"));
//    }
}


