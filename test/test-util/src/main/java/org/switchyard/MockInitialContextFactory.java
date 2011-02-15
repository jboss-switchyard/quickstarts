/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Mock Naming Context.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class MockInitialContextFactory implements InitialContextFactory {

    /**
     * Context.
     */
    private static Context _context;
    /**
     * Bound objects.
     */
    private static Map<Object, Object> _boundObjects = new HashMap<Object, Object>();

    /**
     * Install a context instance.
     */
    public static void install() {
        if (System.getProperty(Context.INITIAL_CONTEXT_FACTORY) == null) {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
        }
    }

    /**
     * Clear all objects bound into the instance.
     */
    public static void clear() {
        _boundObjects.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        if (_context == null) {
            _context = (Context) Proxy.newProxyInstance(Context.class.getClassLoader(),
                                                    new Class[]{Context.class},
                                                    new ContextInvocationHandler());
        }

        return _context;
    }

    class ContextInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if (methodName.equals("bind") && args.length == 2) {
                _boundObjects.put(args[0], args[1]);
                return null;
            } else if (methodName.equals("lookup") && args.length == 1) {
                Object object = _boundObjects.get(args[0]);
                if (object != null) {
                    return object;
                }
                throw new NamingException("Unknown object name '" + args[0] + "'.");
            } else if (methodName.equals("close")) {
                return true;
            }

            throw new NamingException("Unexpected call to '" + method.getName() + "'.");
        }
    }
}
