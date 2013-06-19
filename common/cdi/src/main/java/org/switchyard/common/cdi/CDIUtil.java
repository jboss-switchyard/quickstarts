/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.common.cdi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Method;
import java.util.Set;
/**
 * CDI bean utilities.
 */
public final class CDIUtil {

    private CDIUtil() {}
    
    /**
     * Looks up a BeanManager.
     * @return BeanManager instance
     */
    public static BeanManager lookupBeanManager() {
        BeanManager beanManager = getCDIBeanManager("java:comp");
        if (beanManager == null) {
            beanManager = getCDIBeanManager("java:comp/env");
        }
        if (beanManager == null) {
            beanManager = getOSGICDIBeanManager();
        }
        return beanManager;
    }
    
    /**
     * Looks up a CDI Bean.
     * @param name bean name
     * @return bean instance
     */
    public static Object lookupBean(String name) {
        BeanManager manager = lookupBeanManager();
        Set<Bean<?>> beans = manager.getBeans(name);
        if (beans != null && !beans.isEmpty()) {
            Bean<?> bean = beans.iterator().next();
            CreationalContext<?> context = manager.createCreationalContext(bean);
            return manager.getReference(bean, Object.class, context);
        }
        return null;
    }
    
    /**
     * Gets a value of @Named annotation for the specified class.
     * @param clazz class object
     * @return bean name
     */
    public static String getNamedAnnotationValue(Class<?> clazz) {
        Named named = clazz.getAnnotation(Named.class);
        if (named == null) {
            return null;
        }
        return named.value();
    }

    private static BeanManager getCDIBeanManager(String jndiLocation) {
        Context javaComp = getJavaComp(jndiLocation);

        if (javaComp != null) {
            try {
                return (BeanManager) javaComp.lookup("BeanManager");
            } catch (NamingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static Context getJavaComp(String jndiName) {
        InitialContext initialContext = null;

        try {
            initialContext = new InitialContext();
            return (Context) initialContext.lookup(jndiName);
        } catch (NamingException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected Exception retrieving '" + jndiName + "' from JNDI namespace.", e);
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    throw new IllegalStateException("Unexpected error closing InitialContext.", e);
                }
            }
        }
    }

    private static BeanManager getOSGICDIBeanManager() {
        try {
            return OSGICDISupport.getBeanManager();
        } catch (Throwable t) {
            return null;
        }
    }

    private static class OSGICDISupport {
        public static BeanManager getBeanManager() throws Exception {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader instanceof BundleReference) {
                Bundle bundle = ((BundleReference) classLoader).getBundle();
                ServiceReference[] refs = bundle.getBundleContext().getServiceReferences(
                        "org.ops4j.pax.cdi.spi.CdiContainer", "(bundleId=" + bundle.getBundleId() + ")");
                if (refs != null && refs.length == 1) {
                    Object cdiContainer = bundle.getBundleContext().getService(refs[0]);
                    try {
                        Method method = cdiContainer.getClass().getMethod("getBeanManager");
                        return (BeanManager) method.invoke(cdiContainer);
                    } finally {
                        bundle.getBundleContext().ungetService(refs[0]);
                    }
                }
            }
            return null;
        }
    }

}
