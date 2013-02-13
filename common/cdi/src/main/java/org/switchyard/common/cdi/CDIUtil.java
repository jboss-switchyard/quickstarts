/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.common.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
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
        try {
            BeanManagerProvider provider = BeanManagerProvider.getInstance();
            if (provider != null) {
                return provider.getBeanManager();
            }
            return null;
        } catch (IllegalStateException e) {
            return null;
        }
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
}
