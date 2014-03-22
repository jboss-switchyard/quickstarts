/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.BeanManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

/**
 * Retrieve the BeanManager from the CdiContainer service.
 */
final class OSGICDISupport {
    /**
     * Retrieve the BeanManager from the CdiContainer service.
     */
    static BeanManager getBeanManager() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader instanceof BundleReference) {
            Bundle bundle = ((BundleReference) classLoader).getBundle();
            ServiceReference<?>[] refs = bundle.getBundleContext().getServiceReferences(
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

    private OSGICDISupport() {
    }

}
