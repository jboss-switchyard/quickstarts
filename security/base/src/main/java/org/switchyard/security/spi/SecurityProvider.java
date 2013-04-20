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
package org.switchyard.security.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.switchyard.ServiceSecurity;
import org.switchyard.security.SecurityContext;

import org.switchyard.security.BaseSecurityLogger;

/**
 * SecurityProvider.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public abstract class SecurityProvider {

    private static final SecurityProvider INSTANCE;
    static {
        SecurityProvider instance;
        try {
            ServiceLoader<SecurityProvider> services = ServiceLoader.load(SecurityProvider.class, SecurityProvider.class.getClassLoader());
            Iterator<SecurityProvider> iterator = services.iterator();
            instance = iterator.hasNext() ? iterator.next() : null;
        } catch (Throwable t) {
            instance = null;
        }
        INSTANCE = instance != null ? instance : new JaasSecurityProvider();
        BaseSecurityLogger.ROOT_LOGGER.usingSecurityProviderImplementation(INSTANCE.getClass().getName());
    }

   /**
    * Authenticates the SecurityContext for the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether authentication was successful
    */
   public abstract boolean authenticate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Propagates any existing container-specific (most likely thread-local) security information into the SecurityContext.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether propagation was successful
    */
   public abstract boolean propagate(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
     * Adds the ServiceSecurity's runAs Role to the SecurityContext's Subject.
     * @param serviceSecurity the ServiceSecurity
     * @param securityContext the SecurityContext
     * @return whether adding the runAs Role was successful
    */
   public abstract boolean addRunAs(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Checks if the Subject associated in the SecurityContext has at least one of the allowed roles in the ServiceSecurity.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether the allowed roles check was successful
    */
   public abstract boolean checkRolesAllowed(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Clears the SecurityContext and any underlying SecurityContextAssociation.
    * @param serviceSecurity the ServiceSecurity
    * @param securityContext the SecurityContext
    * @return whether the clear was successful
    */
   public abstract boolean clear(ServiceSecurity serviceSecurity, SecurityContext securityContext);

   /**
    * Gets the singleton instance of the SecurityProvider.
    * @return the singleton instance of the SecurityProvider
    */
   public static final SecurityProvider instance() {
       return INSTANCE;
   }

}
