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
