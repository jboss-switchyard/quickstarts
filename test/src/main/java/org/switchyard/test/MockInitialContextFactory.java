/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.test;

import org.junit.Assert;
import org.switchyard.test.mixins.CDIMixIn;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
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

    protected static final NameParser NAME_PARSER = new NameParser() {
        public Name parse(String name) throws NamingException {
            return new CompositeName(name);
        }
    };

    /**
     * Bound objects.
     */
    private static Map<ClassLoader, Context> _contexts = new HashMap<ClassLoader, Context>();

    /**
     * Install a context instance.
     */
    public static void install() {
        if (System.getProperty(Context.INITIAL_CONTEXT_FACTORY) == null) {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MockInitialContextFactory.class.getName());
        }

        try {
            createContextForTCCL();
        } catch (NamingException e) {
            e.printStackTrace();
            Assert.fail("Failed to create context for TCCL: " + e.getMessage());
        }
    }

    /**
     * Clear contexts.
     */
    public static void clear() {
        _contexts.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return createContextForTCCL();
    }

    /**
     * Get the component context (java:comp).
     * @return The component context.
     * @throws NamingException Exception while looking up the context.
     */
    public static Context getJavaComp() throws NamingException {
        return getSubContext("java:comp");
    }

    /**
     * Get the named sub context.
     * @param name Sub context name.
     * @return The context.
     * @throws NamingException Exception while looking up the context.
     */
    public static Context getSubContext(String name) throws NamingException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Context context = _contexts.get(tccl);

        if (context == null) {
            Assert.fail("No global namespace context bound for current test Thread.");
        }

        return (Context) context.lookup(name);
    }

    private static Context createContextForTCCL() throws NamingException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Context context = _contexts.get(tccl);

        if (context == null) {
            context = (Context) Proxy.newProxyInstance(Context.class.getClassLoader(),
                    new Class[]{Context.class},
                    new ContextInvocationHandler());
            _contexts.put(tccl, context);

            context.createSubcontext("java:comp");
        }
        return context;
    }

    static class ContextInvocationHandler implements InvocationHandler {

        private Map<Object, Object> _boundObjects = new HashMap<Object, Object>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            if (methodName.equals("bind") && args.length == 2) {
                _boundObjects.put(args[0], args[1]);
                return null;
            } else if (methodName.equals("rebind") && args.length == 2) {
                _boundObjects.put(args[0], args[1]);
                return null;
            } else if (methodName.equals("unbind") && args.length == 1) {
                _boundObjects.remove(args[0]);
                return null;
            } else if (methodName.equals("lookup") && args.length == 1) {
                Object object = _boundObjects.get(args[0]);
                if (object != null) {
                    return object;
                }
                tryHelpDeveloper(args[0]);
                throw new NamingException("Unknown object name '" + args[0] + "'.");
            } else if (methodName.equals("createSubcontext")) {
                Object name = args[0];
                if (_boundObjects.containsKey(name)) {
                    throw new NamingException("Subcontext '" + name + "' already exists.");
                }
                Context subContext = (Context) Proxy.newProxyInstance(Context.class.getClassLoader(),
                                                        new Class[]{Context.class},
                                                        new ContextInvocationHandler());
                _boundObjects.put(name, subContext);
                return subContext;
            } else if (methodName.equals("destroySubcontext")) {
                Object name = args[0];
                if (!_boundObjects.containsKey(name)) {
                    throw new NamingException("Subcontext '" + name + "' doesn't exists.");
                }
                _boundObjects.remove(name);
                return null;
            } else if (methodName.equals("getNameParser")) {
                return NAME_PARSER;
            } else if (methodName.equals("close")) {
                return true;
            }

            throw new NamingException("Unexpected call to '" + method.getName() + "'.");
        }

        private void tryHelpDeveloper(Object objectName) {
            if (objectName.equals("BeanManager")) {
                Assert.fail("Your test requires access to the CDI BeanManager.  You need to annotate your test class with '@"
                        + SwitchYardTestCaseConfig.class.getSimpleName() + "(mixins = " + CDIMixIn.class.getSimpleName() + ".class)'");
            }
        }
    }
}
